@file:OptIn(ExperimentalStdlibApi::class)

package com.gilpereda.adventsofcode.adventsofcode2021.day15

import arrow.core.flatten
import arrow.core.valid
import kotlin.math.pow
import kotlin.math.sqrt

fun minimumRisk(input: Sequence<String>): String = calPath(parseInput(input))

fun minimumRisk2(input: Sequence<String>): String = calPath(parseInput(input).wholeMap)

fun calPath(riskMap: RiskMap): String {
    val goal = Point(riskMap.width - 1, riskMap.height - 1)
    val d: (Point, Point) -> Double = { _, point -> riskMap.getRisk(point).toDouble() }

    val minRiskPath = findMinRiskPath(Point(0, 0), goal, { goal.distanceTo(it) }, d, riskMap::isValid).reversed() + goal
    return riskMap.totalRisk(minRiskPath).toString()
}

fun findMinRiskPath(start: Point, goal: Point, h: (Point) -> Double, d: (Point, Point) -> Double, valid: (Point) -> Boolean): List<Point> {
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
            .filter { valid(it) }
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
    private val scores = mutableMapOf(start to h(start))

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




//data class Finder(
//    val open: Set<Node>,
//    val closed: Set<Node>,
//    val goal: Node
//)

//data class Node(val x: Int, val y: Int, val cameFrom: Node, val risk: Int)


fun parseInput(input: Sequence<String>): RiskMap =
    input.map { it.toList().map { "$it".toInt() } }
        .toList()
        .let { map ->
            val width = map.first().size
            val height = map.size
            assert(map.all { it.size == width }) { "All rows must have the same length" }
            RiskMap(map, width, height)
        }

//val minRiskPath = DeepRecursiveFunction<Triple<RiskMap, Point, Point?>, Path?> { (riskMap, to, next) ->
//    if (to != Point(0, 0)) {
//        to.neighbours
//            .filter { riskMap.isValid(it) && it != next }
//            .mapNotNull { callRecursive(Triple(riskMap, it, to)) }
//            .minByOrNull { it.risk }
//            ?.let { path -> path.copy(path.points + to, path.risk + riskMap.getRisk(to))  }
//    } else {
//        Path(listOf(to), riskMap.getRisk(to))
//    }
//}

data class RiskMap(val map: List<List<Int>>, val width: Int, val height: Int) {
    fun isValid(point: Point): Boolean =
        point.x in 0 until width && point.y in 0 until height

    fun getRisk(point: Point): Int = map[point.y][point.x]

    fun totalRisk(points: List<Point>): Int =
        points.drop(1).fold(0) { acc, point -> acc + getRisk(point) }
}

data class Path(val points: List<Point>, val risk: Int)

data class Point(val x: Int, val y: Int) {
    val neighbours: List<Point>
        get() = listOf(
            Point(x-1, y),
            Point(x+1, y),
            Point(x, y-1),
            Point(x, y+1),
        )

    fun distanceTo(other: Point): Double =
        sqrt((other.x - x).toDouble().pow(2.0) + (other.y - y).toDouble().pow(2.0))
}

val RiskMap.wholeMap: RiskMap
    get() {
        val newHeight = height * 5
        val newWidth = width * 5
        val newRows = map.map { row -> generateSequence(row) { r -> r.map { newRisk(it, 1) } }.take(5).toList().flatten() }
        val newMap = generateSequence(newRows) { rows -> rows.map { row -> row.map { newRisk(it, 1) } } }.take(5).toList().flatten()
        return RiskMap(newMap, newWidth, newHeight)
    }

fun newRisk(old: Int, inc: Int): Int {
    val newValue = old + inc
    return if (newValue > 9) 1 else newValue
}