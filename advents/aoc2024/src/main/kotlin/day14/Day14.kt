package com.gilpereda.aoc2024.day14

import com.gilpereda.adventofcode.commons.geometry.Point
import com.gilpereda.adventofcode.commons.map.TypedTwoDimensionalMap

private val DIMENSIONS_REGEX = "w=(\\d+) h=(\\d+)".toRegex()
private val ROBOT_REGEX = "p=(\\d+),(\\d+) v=([\\-0-9]+),([\\-0-9]+)".toRegex()

fun firstTask(input: Sequence<String>): String {
    val inputList = input.toList()
    val (width, height) = DIMENSIONS_REGEX.find(inputList.first())!!.destructured.let { (w, h) -> w.toInt() to h.toInt() }
    val robotSequence =
        inputList
            .drop(1)
            .map { it.parsed() }
            .map { it.after(100, width, height) }
    val aux = robotSequence.groupBy { it.position }.mapValues { (_, v) -> v.size }
    return robotSequence
        .groupBy { it.quadrant(width, height) }
        .map { (quadrant, robots) -> if (quadrant != 0) robots.size else 1 }
        .fold(1) { acc, count -> acc * count }
        .toString()
}

fun secondTask(input: Sequence<String>): String {
    val (map, robots) = parseInput(input)
    val limit = robots.size * 90 / 100
    val winner =
        generateSequence(0 to robots) { (generation, robots) -> generation + 1 to robots.next(map.width, map.height) }
            .takeWhile { (generation, robots) ->
                println("Generation: $generation")
                val aligned = map.areAligned(robots.map { it.position })
                if (aligned) {
                    val alignedRobots =
                        robots
                            .groupBy { it.position }
                            .mapValues { (_, v) -> v.size }
                    println(map.dump { point, _ -> alignedRobots[point]?.toString() ?: "." })
                }
                !aligned
            }.last()
    return winner.first.toString()
}

fun parseInput(input: Sequence<String>): Pair<TypedTwoDimensionalMap<Int>, List<Robot>> {
    val inputList = input.toList()
    val (width, height) = DIMENSIONS_REGEX.find(inputList.first())!!.destructured.let { (w, h) -> w.toInt() to h.toInt() }

    val map = TypedTwoDimensionalMap.from(0, width, height)
    val robots =
        inputList
            .drop(1)
            .map { it.parsed() }
    return Pair(map, robots)
}

fun List<Robot>.next(
    width: Int,
    height: Int,
): List<Robot> = map { it.next(width, height) }

fun List<Robot>.previous(
    width: Int,
    height: Int,
): List<Robot> = map { it.previous(width, height) }

private fun TypedTwoDimensionalMap<*>.areAligned(robotPositions: List<Point>): Boolean =
    listOf(
        List(width) { Point.from(it, 0) },
        List(width) { Point.from(it, height - 1) },
        List(height) { Point.from(0, it) },
        List(height) { Point.from(width - 1, it) },
    ).flatten().all { it in robotPositions }

private fun String.parsed(): Robot =
    ROBOT_REGEX.find(this)?.destructured?.let { (x, y, vx, vy) ->
        Robot(Point.from(x.toInt(), y.toInt()), Point.from(vx.toInt(), vy.toInt()))
    } ?: throw IllegalArgumentException("Invalid input")

data class Robot(
    val position: Point,
    val velocity: Point,
) {
    fun after(
        seconds: Int,
        width: Int,
        height: Int,
    ): Robot = generateSequence(this) { it.next(width, height) }.take(seconds + 1).last()

    fun next(
        width: Int,
        height: Int,
    ): Robot = copy(position = (position + velocity).normalized(width, height))

    fun previous(
        width: Int,
        height: Int,
    ): Robot = copy(position = (position - velocity).normalized(width, height))

    fun quadrant(
        width: Int,
        height: Int,
    ): Int {
        val quadrantX = quadrantCoord(position.x, width)
        val quadrantY = quadrantCoord(position.y, height)
        return when {
            quadrantX == 1 && quadrantY == 1 -> 1
            quadrantX == 2 && quadrantY == 1 -> 2
            quadrantX == 1 && quadrantY == 2 -> 3
            quadrantX == 2 && quadrantY == 2 -> 4
            else -> 0
        }
    }

    private fun quadrantCoord(
        position: Int,
        dimension: Int,
    ): Int =
        when {
            position < dimension / 2 -> 1
            position == dimension / 2 -> 0
            else -> 2
        }

    private fun Point.normalized(
        width: Int,
        height: Int,
    ): Point = Point.from(x.normalized(width), y.normalized(height))

    private fun Int.normalized(dimension: Int): Int =
        when {
            this < 0 -> (dimension + this) % dimension
            this > dimension - 1 -> this % dimension
            else -> this
        }
}
