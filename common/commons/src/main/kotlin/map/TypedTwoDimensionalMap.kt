package com.gilpereda.adventofcode.commons.map

import com.gilpereda.adventofcode.commons.geometry.Point

typealias Index = Int

class TypedTwoDimensionalMap<T>(
    private val internalMap: MutableList<MutableList<T>>,
) {
    val height: Int by lazy {
        internalMap.size
    }

    val width: Int by lazy {
        internalMap.first().size
    }

    fun areAdjacentPoints(
        one: Point,
        other: Point,
    ): Boolean =
        one.x in maxOf(other.x - 1, 0)..minOf(other.x + 1, width - 1) &&
            one.y in maxOf(other.y - 1, 0)..minOf(other.y + 1, height - 1)

    fun toUnlimited(): UnlimitedTypedTwoDimensionalMap<T> = UnlimitedTypedTwoDimensionalMap(this)

    fun dump(transform: (T) -> String = { "$it" }): String = dump { _, t -> transform(t) }

    fun dumpWithIndex(transform: (Point, T) -> String): String =
        internalMap
            .transformWithIndexes(transform)
            .joinToString(separator = "\n") { line -> line.joinToString("") }

    fun dumpWithIndex(transform: (T) -> String = { "$it" }): String = dumpWithIndex { _, t -> transform(t) }

    private fun List<List<T>>.transformWithIndexes(transform: (Point, T) -> String): List<List<String>> =
        listOf(List(width) { y -> "${y % 10}" }) +
            mapIndexed { y, line -> line.mapIndexed { x, t -> transform(Point.from(x, y), t) } + "${y % 10}" }

    fun dump(transform: (Point, T) -> String): String =
        internalMap
            .mapIndexed { y, line -> line.mapIndexed { x, cell -> transform(Point.from(x, y), cell) } }
            .joinToString("\n") { line -> line.joinToString("") }

    fun dumpMultiline(transform: (Point, T) -> String): String =
        internalMap
            .flatMapIndexed { y, line ->
                line
                    .mapIndexed { x, cell -> transform(Point.from(x, y), cell).split("\n") }
                    .traversed()
            }.joinToString("\n") { line -> line.joinToString("") }

    private fun List<List<String>>.traversed(): List<List<String>> =
        fold(mapOf<Int, List<String>>()) { acc, next ->
            acc + next.mapIndexed { index, cell -> index to acc.getOrDefault(index, emptyList()) + cell }
        }.values.toList()

    val indices: List<Point> by lazy {
        internalMap.flatMapIndexed { y, line -> List(line.size) { x -> Point.from(x, y) } }
    }

    operator fun contains(point: Point): Boolean = point.withinLimits(0 until width, 0 until height)

    fun <B> map(transform: (T) -> B): TypedTwoDimensionalMap<B> = mapIndexed { _, value -> transform(value) }

    fun <B> mapIndexed(transform: (Point, T) -> B): TypedTwoDimensionalMap<B> =
        mapIndexed { x, y, value -> transform(Point.from(x, y), value) }

    fun <B> mapIndexed(transform: (x: Index, y: Index, value: T) -> B): TypedTwoDimensionalMap<B> =
        TypedTwoDimensionalMap(
            internalMap
                .mapIndexed {
                    y,
                    line,
                    ->
                    line.mapIndexed { x, value -> transform(x, y, value) }.toMutableList()
                }.toMutableList(),
        )

    fun <B> mapLines(transform: (List<T>) -> List<B>): TypedTwoDimensionalMap<B> =
        TypedTwoDimensionalMap(internalMap.map { transform(it).toMutableList() }.toMutableList())

    fun values(): List<T> = internalMap.flatten()

    fun valuesIndexed(): List<Pair<Point, T>> =
        internalMap.flatMapIndexed { y, line -> line.mapIndexed { x, c -> Pair(Point.from(x, y), c) } }

    fun allMatch(predicate: (T) -> Boolean): Boolean = values().all(predicate)

    operator fun get(point: Point): T = internalMap[point.y][point.x]

    fun getNullable(point: Point): T? = internalMap.getOrNull(point.y)?.getOrNull(point.x)

    fun diagonalLinesUpDown(): List<List<T>> =
        diagonalLines(
            start = Point.from(0, 0),
            next = { Point.from(x + 1, y - 1) },
            nextStart = {
                when {
                    y < height - 1 -> Point.from(0, y + 1)
                    else -> Point.from(x + 1, y)
                }
            },
        )

    fun diagonalLinesDownUp(): List<List<T>> =
        diagonalLines(
            start = Point.from(0, height - 1),
            next = { Point.from(x + 1, y + 1) },
            nextStart = {
                when {
                    y > 0 -> Point.from(0, y - 1)
                    else -> Point.from(x + 1, 0)
                }
            },
        )

    private fun diagonalLines(
        start: Point,
        next: Point.() -> Point,
        nextStart: Point.() -> Point,
    ): List<List<T>> =
        sequence<List<T>> {
            var currentStart = start
            val finish =
                Point.from(
                    x = if (start.x == 0) width - 1 else 0,
                    y = if (start.y == 0) height - 1 else 0,
                )
            while (currentStart != finish) {
                val line = mutableListOf<T>()
                var current = currentStart
                var currentElement = getNullable(current)
                while (currentElement != null) {
                    line.add(currentElement)
                    current = current.next()
                    currentElement = getNullable(current)
                }
                yield(line)
                currentStart = currentStart.nextStart()
            }
        }.toList()

    operator fun set(
        point: Point,
        value: T,
    ): TypedTwoDimensionalMap<T> {
        internalMap[point.y][point.x] = value
        return this
    }

    fun <B> rows(transform: (Collection<T>) -> B): List<B> = internalMap.map(transform)

    fun <B> columns(transform: (Collection<T>) -> B): List<B> = transpose().internalMap.map(transform)

    fun transpose(): TypedTwoDimensionalMap<T> {
        val initial = MutableList(internalMap.first().size) { mutableListOf<T>() }
        return TypedTwoDimensionalMap(
            internalMap
                .fold(initial) { acc, row ->
                    row.forEachIndexed { x, c ->
                        acc[x].add(c)
                    }
                    acc
                }.toMutableList(),
        )
    }

    fun mirror(): TypedTwoDimensionalMap<T> =
        TypedTwoDimensionalMap(
            internalMap.map { it.reversed().toMutableList() }.toMutableList(),
        )

    fun count(predicate: (T) -> Boolean): Int = values().count(predicate)

    override fun equals(other: Any?): Boolean = other is TypedTwoDimensionalMap<*> && internalMap == other.internalMap

    companion object {
        fun <T> from(
            value: T,
            width: Int,
            height: Int,
        ): TypedTwoDimensionalMap<T> = TypedTwoDimensionalMap(List(height) { List(width) { value }.toMutableList() }.toMutableList())
    }
}

inline fun <reified T> String.parseToMap(transform: (c: Char) -> T): TypedTwoDimensionalMap<T> = split("\n").parseToMap(transform)

inline fun <reified T> List<String>.parseToMap(transform: (c: Char) -> T): TypedTwoDimensionalMap<T> =
    parseToMap { _, _, c -> transform(c) }

inline fun <reified T> List<String>.parseToMap(transform: (point: Point, c: Char) -> T): TypedTwoDimensionalMap<T> =
    parseToMap { x, y, c -> transform(Point.from(x, y), c) }

inline fun <reified T> List<String>.parseToMap(transform: (x: Index, y: Index, c: Char) -> T): TypedTwoDimensionalMap<T> =
    TypedTwoDimensionalMap(mapIndexed { y, line -> line.mapIndexed { x, cell -> transform(x, y, cell) }.toMutableList() }.toMutableList())

inline fun <reified T> Sequence<String>.parseToMap(crossinline transform: (c: Char) -> T): TypedTwoDimensionalMap<T> =
    parseToMap { _, _, c -> transform(c) }

inline fun <reified T> Sequence<String>.parseToMap(crossinline transform: (point: Point, c: Char) -> T): TypedTwoDimensionalMap<T> =
    parseToMap { x, y, c -> transform(Point.from(x, y), c) }

inline fun <reified T> Sequence<String>.parseToMap(crossinline transform: (x: Index, y: Index, c: Char) -> T): TypedTwoDimensionalMap<T> =
    TypedTwoDimensionalMap(mapIndexed { y, line -> line.mapIndexed { x, cell -> transform(x, y, cell) }.toMutableList() }.toMutableList())
