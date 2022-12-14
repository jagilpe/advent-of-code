package com.gilpereda.aoc2022.day14

fun firstTask(input: Sequence<String>): String {
    val initState = State(
        Cave(input.parsed()),
        Point(500, 0),
        Point(500, 0)
    )
    return generateSequence(initState) { it.next }.first { it.finished }
        .cave.sandCount.toString()
}

fun secondTask(input: Sequence<String>): String {
    val cave = Cave(input.parsed())
    val initState = State2(
        cave,
        Point(500, 0),
        Point(500, 0),
        cave.maxY +  2,
    )
    return generateSequence(initState) { it.next }.first { it.finished }
        .cave.apply { println(this) }
        .sandCount.toString()
}


fun Sequence<String>.parsed(): List<Path> =
    filter { it.isNotBlank() }
        .map { line ->
            line.split(" -> ")
                .map { point ->
                    val (x, y) = point.split(",")
                    Point(x.toInt(), y.toInt())
                }.let(::Path)
        }.toList()

data class State(
    val cave: Cave,
    val currentSand: Point,
    val startPoint: Point,
) {
    val finished: Boolean
        get() = currentSand.y > cave.maxY

    val next: State
        get () =
            currentSand.nextPoints.firstOrNull { cave.isFree(it) }
                ?.let { nextPoint -> copy(currentSand = nextPoint) }
                ?: copy(cave = cave.addItem(currentSand, Sand), currentSand = startPoint)
}

data class State2(
    val cave: Cave,
    val currentSand: Point,
    val startPoint: Point,
    val floor: Int,
    val finished: Boolean = false
) {
    val next: State2
        get () =
            currentSand.nextPoints
                .filter { it.y < floor }
                .firstOrNull { cave.isFree(it) }
                ?.let { nextPoint -> copy(currentSand = nextPoint) }
                ?: copy(cave = cave.addItem(currentSand, Sand), currentSand = startPoint, finished = currentSand == startPoint)
}


data class Point(val x: Int, val y: Int) {
    fun pointsTo(other: Point): Set<Point> =
        when {
            x == other.x -> (minOf(y, other.y)..maxOf(y, other.y)).map { Point(x, it) }.toSet()
            y == other.y -> (minOf(x, other.x)..maxOf(x, other.x)).map { Point(it, y) }.toSet()
            else -> throw IllegalArgumentException("Diagonals are unsupported.")
        }

    val nextPoints: List<Point>
        get() = listOf(
            Point(x, y + 1),
            Point(x - 1, y + 1),
            Point(x + 1, y + 1)
        )
}

data class Path(val edges: List<Point>) {
    val points: Set<Point> = edges
        .windowed(2, 1, false)
        .flatMap { (from, to) -> from.pointsTo(to) }
        .toSet()

    companion object {
        fun of(vararg points: Point) = Path(points.toList())
    }
}

class Cave(paths: List<Path>) {
    private val minX
        get() = cave.flatMap { it.value.keys }.min()
    private val maxX
        get() = cave.flatMap { it.value.keys }.max()
    private val minY
        get() = cave.keys.min()
    val maxY
        get() = cave.keys.max()

    val sandCount: Int
        get() = cave.flatMap { it.value.values }.count { it == Sand }

    fun isFree(point: Point): Boolean = getItem(point) == Empty

    fun addItem(point: Point, item: CaveItem): Cave {
        cave.computeIfAbsent(point.y) { mutableMapOf() }.also { it[point.x] = item }
        return this
    }

    private fun getItem(point: Point): CaveItem =
        cave.getOrDefault(point.y, mapOf()).getOrDefault(point.x, Empty)


    private val cave: MutableMap<Int, MutableMap<Int, CaveItem>> = mutableMapOf<Int, MutableMap<Int, CaveItem>>()

    init {
        paths.flatMap { it.points }.forEach { point -> addItem(point,Rock) }
    }

    override fun toString(): String =
        (minY..maxY).joinToString("\n") { y ->
            "$y" + (minX..maxX).joinToString("") { x ->
                getItem(Point(x, y)).toString()
            }
        }
}

sealed interface CaveItem

object Rock : CaveItem {
    override fun toString(): String = "#"
}

object Sand : CaveItem {
    override fun toString(): String = "o"
}

object Empty : CaveItem {
    override fun toString(): String = "."
}