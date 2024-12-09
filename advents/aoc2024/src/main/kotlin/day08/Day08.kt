package com.gilpereda.aoc2024.day08

import com.gilpereda.adventofcode.commons.geometry.Point
import com.gilpereda.adventofcode.commons.map.TypedTwoDimensionalMap
import com.gilpereda.adventofcode.commons.map.parseToMap
import kotlin.math.abs

fun firstTask(input: Sequence<String>): String {
    val map = input.toList().parseToMap(::Antenna)
    return map
        .values()
        .asSequence()
        .filter { it.frequency != '.' }
        .groupBy { it.frequency }
        .flatMap { (_, antennas) -> antennas.antiNodes(map, true) }
        .filter { it in map }
        .toSet()
        .count()
        .toString()
}

fun secondTask(input: Sequence<String>): String {
    val map = input.toList().parseToMap(::Antenna)
    val antiNodes =
        map
            .values()
            .asSequence()
            .filter { it.frequency != '.' }
            .groupBy { it.frequency }
            .flatMap { (_, antennas) -> antennas.antiNodes(map, false) }
            .filter { it in map }
            .toSet()
//    println(map.dump { p, c -> if (p in antiNodes) "#" else c.frequency.toString() })
    return antiNodes
        .count()
        .toString()
}

private fun List<Antenna>.antiNodes(
    map: TypedTwoDimensionalMap<*>,
    limited: Boolean,
): List<Point> {
    tailrec fun go(
        acc: List<Point>,
        rest: List<Antenna>,
    ): List<Point> =
        if (rest.isEmpty()) {
            acc
        } else {
            val next = rest.first()
            val newRest = rest.drop(1)
            val nextAntiNodes = newRest.flatMap { it.antiNodes(next, map, limited) }
            go(acc + nextAntiNodes, newRest)
        }
    return go(emptyList(), this)
}

data class Antenna(
    val point: Point,
    val frequency: Char,
) {
    fun antiNodes(
        other: Antenna,
        map: TypedTwoDimensionalMap<*>,
        limited: Boolean,
    ): List<Point> {
        val xDiff = point.x - other.point.x
        val yDiff = point.y - other.point.y
        return when {
            (xDiff > 0 && yDiff > 0) || (xDiff < 0 && yDiff < 0) -> diagonalUpDown(point, other.point, abs(xDiff), abs(yDiff), map, limited)
            else -> diagonalDownUp(point, other.point, abs(xDiff), abs(yDiff), map, limited)
        }
    }

    private fun diagonalUpDown(
        one: Point,
        other: Point,
        xDiff: Int,
        yDiff: Int,
        map: TypedTwoDimensionalMap<*>,
        limited: Boolean,
    ): List<Point> {
        val upPoint = if (one.y < other.y) one else other
        val downPoint = if (one.y < other.y) other else one
        val upPoints =
            sequence {
                if (limited) {
                    yield(diagonalUpDownUp(upPoint, xDiff, yDiff))
                } else {
                    var point = upPoint
                    while (point in map) {
                        yield(point)
                        point = diagonalUpDownUp(point, xDiff, yDiff)
                    }
                }
            }.toList()
        val downPoints =
            sequence {
                if (limited) {
                    yield(diagonalUpDownDown(downPoint, xDiff, yDiff))
                } else {
                    var point = downPoint
                    while (point in map) {
                        yield(point)
                        point = diagonalUpDownDown(point, xDiff, yDiff)
                    }
                }
            }.toList()
        return upPoints + downPoints
    }

    private fun diagonalUpDownUp(
        point: Point,
        xDiff: Int,
        yDiff: Int,
    ): Point = Point.from(point.x - xDiff, point.y - yDiff)

    private fun diagonalUpDownDown(
        point: Point,
        xDiff: Int,
        yDiff: Int,
    ): Point = Point.from(point.x + xDiff, point.y + yDiff)

    private fun diagonalDownUp(
        one: Point,
        other: Point,
        xDiff: Int,
        yDiff: Int,
        map: TypedTwoDimensionalMap<*>,
        limited: Boolean,
    ): List<Point> {
        val upPoint = if (one.y < other.y) one else other
        val downPoint = if (one.y < other.y) other else one
        val upPoints =
            sequence {
                if (limited) {
                    yield(diagonalDownUpUp(upPoint, xDiff, yDiff))
                } else {
                    var point = upPoint
                    while (point in map) {
                        yield(point)
                        point = diagonalDownUpUp(point, xDiff, yDiff)
                    }
                }
            }.toList()
        val downPoints =
            sequence {
                if (limited) {
                    yield(diagonalDownUpDown(downPoint, xDiff, yDiff))
                } else {
                    var point = downPoint
                    while (point in map) {
                        yield(point)
                        point = diagonalDownUpDown(point, xDiff, yDiff)
                    }
                }
            }.toList()
        return upPoints + downPoints
    }

    private fun diagonalDownUpUp(
        point: Point,
        xDiff: Int,
        yDiff: Int,
    ): Point = Point.from(point.x + xDiff, point.y - yDiff)

    private fun diagonalDownUpDown(
        point: Point,
        xDiff: Int,
        yDiff: Int,
    ): Point = Point.from(point.x - xDiff, point.y + yDiff)
}
