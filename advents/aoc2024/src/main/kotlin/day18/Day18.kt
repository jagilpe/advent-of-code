package com.gilpereda.aoc2024.day18

import com.gilpereda.adventofcode.commons.geometry.Point
import com.gilpereda.adventofcode.commons.map.TypedTwoDimensionalMap

fun firstTask(input: Sequence<String>): String {
    val inputList = input.toList()
    val (width, height, bytes) = inputList.first().split(",").map { it.toInt() }

    val fallingBytes =
        inputList
            .drop(2)
            .take(bytes)
            .map {
                val (x, y) = it.split(",").take(2).map { it.toInt() }
                Point.from(x, y)
            }.toSet()
    val memory = Memory(width, height, fallingBytes)

    val result = memory.solve()

    return result!!.size.toString()
}

fun secondTask(input: Sequence<String>): String {
    val inputList = input.toList()
    val (width, height, bytes) = inputList.first().split(",").map { it.toInt() }

    val fallingBytes =
        inputList
            .drop(2)
            .map {
                val (x, y) = it.split(",").take(2).map { it.toInt() }
                Point.from(x, y)
            }.toList()
    val memory = Memory(width, height, fallingBytes.take(bytes).toSet())

    val (x, y) =
        fallingBytes
            .drop(bytes)
            .asSequence()
            .map { point ->
                point to memory.add(point).solve()
            }.first { it.second == null }
            .first

    return "$x,$y"
}

enum class Cell(
    private val s: String,
) {
    Empty("."),
    Wall("#"),
    ;

    override fun toString(): String = s
}

class Memory(
    width: Int,
    height: Int,
    fallingBytes: Set<Point>,
) {
    private val start = Point.from(0, 0)
    private val target = Point.from(width - 1, height - 1)

    private val map =
        TypedTwoDimensionalMap
            .from(Cell.Empty, width, height)
            .mapIndexed { point, _ -> if (point in fallingBytes) Cell.Wall else Cell.Empty }

    fun add(byte: Point): Memory {
        map[byte] = Cell.Wall
        return this
    }

    fun solve(): List<Point>? {
        val open = mutableSetOf(start)
        val cameFrom = mutableMapOf<Point, Point>()
        val gScore = GScores(start)

        val fScore = FScores(start, target)
        while (open.isNotEmpty()) {
            val current = open.minByOrNull { fScore[it] }!!
            if (current == target) {
                return reconstructPath(cameFrom, target)
            }

            open.remove(current)
            current.neighbours.values
                .filter { valid(it) }
                .forEach { neighbour ->
                    val tentativeGScore = gScore[current] + d(current, neighbour)
                    if (tentativeGScore < gScore[neighbour]) {
                        cameFrom[neighbour] = current
                        gScore[neighbour] = tentativeGScore
                        fScore.add(neighbour, tentativeGScore)
                        if (neighbour !in open) {
                            open.add(neighbour)
                        }
                    }
                }
        }
        return null
    }

    private fun dumpPath(
        cameFrom: Map<Point, Point>,
        point: Point,
    ): String {
        val path = reconstructPath(cameFrom, point)
        return map.dumpWithIndex { p, cell ->
            if (path.firstOrNull { it == p } != null) "O" else cell.toString()
        }
    }

    private fun valid(neighbour: Point): Boolean = map.getNullable(neighbour) == Cell.Empty

    private fun d(
        point: Point,
        neighbour: Point,
    ): Double = point.distanceTo(neighbour).toDouble()

    private fun reconstructPath(
        cameFrom: Map<Point, Point>,
        last: Point,
    ): List<Point> {
        val path = mutableListOf<Point>()
        var current = last
        while (cameFrom.containsKey(current)) {
            current = cameFrom[current]!!
            path.add(current)
        }
        return path
    }

    class GScores(
        start: Point,
    ) {
        private val scores = mutableMapOf(start to 0.0)

        operator fun set(
            point: Point,
            score: Double,
        ) {
            scores[point] = score
        }

        operator fun get(point: Point): Double = scores[point] ?: Double.MAX_VALUE
    }

    class FScores(
        start: Point,
        private val target: Point,
    ) {
        private fun h(point: Point): Double = target.euclideanDistanceTo(point)

        private val scores = mutableMapOf(start to 0.0)

        fun add(
            point: Point,
            score: Double,
        ) {
            scores[point] = score + h(point)
        }

        operator fun get(point: Point): Double = scores[point] ?: Double.MAX_VALUE
    }
}
