package com.gilpereda.aoc2022.utils

import com.gilpereda.aoc2022.utils.geometry.Point
import com.gilpereda.aoc2022.utils.map.UnlimitedTypedTwoDimensionalMap

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

    fun toUnlimited(): UnlimitedTypedTwoDimensionalMap<T> =
        UnlimitedTypedTwoDimensionalMap(this)

    fun dump(transform: (T) -> String = { "$it"}): String =
        internalMap.joinToString(separator = "\n") { line -> line.joinToString("") { transform(it)} }

    fun dump(transform: (Point, T) -> String): String =
        internalMap.mapIndexed { y, line -> line.mapIndexed { x, cell -> transform(Point.from(x, y), cell) } }
            .joinToString("\n") { line -> line.joinToString("") }


    val indices: List<Point> by lazy {
        internalMap.flatMapIndexed { y, line -> List(line.size) { x -> Point.from(x, y) } }
    }

    fun withinMap(point: Point): Boolean =
        point.withinLimits(0 until width, 0 until height)

    fun <B> map(transform: (T) -> B): TypedTwoDimensionalMap<B> =
        mapIndexed { _, value -> transform(value) }

    fun <B> mapIndexed(transform: (Point, T) -> B): TypedTwoDimensionalMap<B> =
        mapIndexed { x, y, value -> transform(Point.from(x, y), value) }

    fun <B> mapIndexed(transform: (x: Index, y: Index, value: T) -> B): TypedTwoDimensionalMap<B> =
        TypedTwoDimensionalMap(internalMap.mapIndexed { y, line -> line.mapIndexed { x, value -> transform(x, y, value) }.toMutableList() }.toMutableList())

    fun <B> mapLines(transform: (List<T>) -> List<B>): TypedTwoDimensionalMap<B> =
        TypedTwoDimensionalMap(internalMap.map { transform(it).toMutableList() }.toMutableList())

    fun values(): List<T> = internalMap.flatten()

    fun valuesIndexed(): List<Pair<Point, T>> = internalMap.flatMapIndexed { y, line -> line.mapIndexed { x, c -> Pair(Point.from(x, y), c) } }

    fun allMatch(predicate: (T) -> Boolean): Boolean =
        values().all(predicate)

    operator fun get(point: Point): T =
        internalMap[point.y][point.x]

    fun getNullable(point: Point): T? =
        internalMap.getOrNull(point.y)?.getOrNull(point.x)

    operator fun set(point: Point, value: T): TypedTwoDimensionalMap<T> {
        internalMap[point.y][point.x] = value
        return this
    }

    fun <B> rows(transform: (Collection<T>) -> B): List<B> =
        internalMap.map(transform)

    fun <B> columns(transform: (Collection<T>) -> B): List<B> =
        transpose().internalMap.map(transform)

    fun transpose(): TypedTwoDimensionalMap<T> {
        val initial = MutableList(internalMap.first().size) { mutableListOf<T>() }
        return TypedTwoDimensionalMap(
            internalMap.fold(initial) { acc, row ->
                row.forEachIndexed { x, c ->
                    acc[x].add(c)
                }
                acc
            }.toMutableList()
        )
    }

    fun mirror(): TypedTwoDimensionalMap<T> =
        TypedTwoDimensionalMap(
            internalMap.map { it.reversed().toMutableList() }.toMutableList()
        )

    fun count(predicate: (T) -> Boolean): Int =
        values().count(predicate)
}

inline fun <reified T> String.parseToMap(transform: (c: Char) -> T): TypedTwoDimensionalMap<T> =
    split("\n").parseToMap(transform)

inline fun <reified T> List<String>.parseToMap(transform: (c: Char) -> T): TypedTwoDimensionalMap<T> =
    parseToMap { _, _, c -> transform(c) }

inline fun <reified T> List<String>.parseToMap(transform: (point: Point, c: Char) -> T): TypedTwoDimensionalMap<T> =
    parseToMap { x, y, c -> transform(Point.from(x, y), c)}

inline fun <reified T> List<String>.parseToMap(transform: (x: Index, y: Index, c: Char) -> T): TypedTwoDimensionalMap<T> =
    TypedTwoDimensionalMap(mapIndexed { y, line -> line.mapIndexed { x, cell -> transform(x, y, cell) }.toMutableList() }.toMutableList())