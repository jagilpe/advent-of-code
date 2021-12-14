package com.gilpereda.adventsofcode.adventsofcode2021.day09

fun calculateRisk(input: Sequence<String>): String =
    input.map { line -> line.toList().map { "$it".toInt() } }.toList()
        .let(::HeightsMap).risk.toString()

fun findBasins(input: Sequence<String>): String =
    input.map { line -> line.toList().map { "$it".toInt() } }.toList()
        .let(::HeightsMap)
        .basinSizes.sortedDescending().take(3).fold(1) { a, b -> a * b }.toString()

class HeightsMap(private val map: List<List<Int>>) {
    private val height = map.size
    private val width = map.first().size

    init {
        if (map.any { it.size != width }) throw IllegalArgumentException("All rows must have the same length")
    }

    val basinSizes: List<Int>
        get() = lows.map { getBasinSize(it) }

    fun getBasinSize(low: Point): Int {
        tailrec fun go(points: Set<Point>, basin: Set<Point>): Set<Point> {
            val newPoints = points.flatMap { it.neighbours }
                .filter { it.valid && it !in basin && it.value < 9 }
                .toSet()
            return if (newPoints.isNotEmpty()) {
                go(newPoints, basin + newPoints)
            } else {
                basin
            }
        }

        return go(setOf(low), setOf(low)).size
    }

    val risk: Int
        get() = lows.sumOf { it.value + 1 }

    val lows: List<Point> by lazy { (0 until height).flatMap { y -> (0 until width).map { x -> Point(x, y) } }
        .filter { it.isLow } }

    private val Point.isLow: Boolean
        get() = this.neighbours
                .filter { it.valid }
                .all { it.value > this.value }

    private val Point.valid: Boolean
        get() = (x >= 0) && (y >= 0) && (x < width) && (y < height)

    private val Point.value: Int
        get() = map[y][x]!!

    private val Point.neighbours: List<Point>
        get() = listOf(Point(x, y - 1), Point(x, y + 1), Point(x - 1, y), Point(x + 1, y))
}

data class Point(val x: Int, val y: Int)
