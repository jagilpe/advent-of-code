package com.gilpereda.aoc2022.day12

import kotlin.math.pow
import kotlin.math.sqrt

fun firstTask(input: Sequence<String>): String =
    input.parsed().calPathSteps().toString()

fun secondTask(input: Sequence<String>): String {
    val map = input.parsed()
    return map.heightsMap.flatMapIndexed { y, line ->
        line.mapIndexed { x, value -> Point(x, y) to value }
    }.filter { it.second == 'a' }.map { it.first }
        .minOfOrNull { start ->
        try {
            map.copy(start = start).calPathSteps()
        } catch (ex: Exception) {
            Int.MAX_VALUE
        }
        }!!.toString()
}


fun Sequence<String>.parsed(): HeightsMap {
    val list = toList()
    val map = list.map { line ->
        line.toList().map {
            when (it) {
                'S' -> 'a'
                'E' -> 'z'
                else -> it
            }
        }
    }
    val startY = list.indexOfFirst { it.contains('S') }
    val startX = list[startY].indexOfFirst { it == 'S' }

    val goalY = list.indexOfFirst { it.contains('E') }
    val goalX = list[goalY].indexOfFirst { it == 'E' }
    return HeightsMap(heightsMap = map, start = Point(startX, startY), goal = Point(goalX, goalY))
}

data class Point(
    val x: Int,
    val y: Int,
) {
    val neighbours: List<Point>
        get() = listOf(
            Point(x - 1, y),
            Point(x + 1, y),
            Point(x, y - 1),
            Point(x, y + 1),
        )

    fun distanceTo(other: Point): Double =
        sqrt((other.x - x).toDouble().pow(2.0) + (other.y - y).toDouble().pow(2.0))

    fun directionTo(other: Point): Char =
        when {
            x < other.x -> '>'
            x > other.x -> '<'
            y < other.y -> 'v'
            else -> '^'
        }
}

data class HeightsMap(
    val heightsMap: List<List<Char>>,
    private val start: Point,
    private val goal: Point,
) {

    private val mapHeight: Int = heightsMap.size
    private val mapWidth: Int = heightsMap.first().size
    fun calPathSteps(): Int {
        val d: (Point, Point) -> Double = { _, _ -> 1.0 }

        val maxSavingPath = findMaxSavingPath(start, goal, { goal.distanceTo(it) }, d, this::isValid).reversed() + goal
        return maxSavingPath.size - 1
    }

    private val Point.height: Int
        get() = value.code

    private val Point.value: Char
        get() = try {
            heightsMap[y][x]
        } catch (ex: Exception) {
            throw ex
        }

    private fun isValid(from: Point, to: Point): Boolean =
        to.x in 0 until mapWidth
                && to.y in 0 until mapHeight
                && to.height - from.height < 2

    private fun printPath(path: List<Point>) {
        val result = (0 until mapHeight).map { y ->
            (0 until mapWidth).map { x ->
                val current = Point(x, y)
                val index = path.indexOf(current)
                if (index != -1) {
                    path.getOrNull(index + 1)
                        ?.let { other ->
                            "${current.directionTo(other)}${current.value}"
                        } ?: "E${current.value}"
                } else {
                    ".${current.value}"
                }
            }.joinToString(" ")
        }.joinToString("\n")

        println(result)
    }
}


fun findMaxSavingPath(
    start: Point,
    goal: Point,
    h: (Point) -> Double,
    d: (Point, Point) -> Double,
    valid: (Point, Point) -> Boolean
): List<Point> {
    val open = mutableSetOf(start)

    val cameFrom = mutableMapOf<Point, Point>()
    val gScore = GScores(start)

    val fScore = FScores(start, h)

    while (open.isNotEmpty()) {
        val current = open.minByOrNull { fScore[it] }!!
        if (current == goal)
            return reconstructPath(cameFrom, current)

        open.remove(current)
        current.neighbours
            .filter { valid(current, it) }
            .forEach { neighbour ->
                val tentativeGScore = gScore[current] + d(current, neighbour)
                if (tentativeGScore < gScore[neighbour]) {
                    cameFrom[neighbour] = current
                    gScore[neighbour] = tentativeGScore
                    fScore.add(neighbour, tentativeGScore)
                    if (neighbour !in open)
                        open.add(neighbour)
                }
            }
    }
    throw Exception("No path found")
}

class GScores(start: Point) {
    private val scores = mutableMapOf(start to 0.0)

    operator fun set(point: Point, score: Double) {
        scores[point] = score
    }

    operator fun get(point: Point): Double = scores[point] ?: Double.MAX_VALUE
}

class FScores(start: Point, private val h: (Point) -> Double) {
    private val scores = mutableMapOf(start to 0.0)

    fun add(point: Point, score: Double) {
        scores[point] = score + h(point)
    }

    operator fun get(point: Point): Double = scores[point] ?: Double.MAX_VALUE
}

fun reconstructPath(cameFrom: Map<Point, Point>, start: Point): List<Point> {
    val path = mutableListOf<Point>()
    var current = start
    while (cameFrom.containsKey(current)) {
        current = cameFrom[current]!!
        path.add(current)
    }
    return path
}