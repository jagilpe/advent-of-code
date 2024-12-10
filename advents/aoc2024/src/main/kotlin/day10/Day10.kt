package com.gilpereda.aoc2024.day10

import com.gilpereda.adventofcode.commons.geometry.Orientation
import com.gilpereda.adventofcode.commons.geometry.Point
import com.gilpereda.adventofcode.commons.map.TypedTwoDimensionalMap
import com.gilpereda.adventofcode.commons.map.parseToMap

typealias Mountain = TypedTwoDimensionalMap<Int>

fun firstTask(input: Sequence<String>): String {
    val mountain =
        input.toList().parseToMap { c: Char -> "$c".toInt() }
    val starts = mountain.findStarts()
    return starts.sumOf { mountain.findTrails(it) }.toString()
}

private fun Mountain.findStarts(): List<Point> = valuesIndexed().filter { it.second == 0 }.map { it.first }

private fun Mountain.findTrails(start: Point): Int {
    fun go(acc: Set<TrailPoint>): Int =
        if (acc.isEmpty() || acc.all { it.height == 9 }) {
            acc.size
        } else {
            go(acc.flatMap { next(it) }.toSet())
        }
    return go(setOf(TrailPoint(start, get(start))))
}

private fun Mountain.next(trailPoint: TrailPoint): List<TrailPoint> =
    if (trailPoint.height == 9) {
        listOf(trailPoint)
    } else {
        listOfNotNull(
            move(trailPoint, Orientation.NORTH),
            move(trailPoint, Orientation.SOUTH),
            move(trailPoint, Orientation.EAST),
            move(trailPoint, Orientation.WEST),
        )
    }

private fun Mountain.move(
    trailPoint: TrailPoint,
    orientation: Orientation,
): TrailPoint? {
    val nextPoint = trailPoint.point.move(orientation)
    return getNullable(nextPoint)?.let {
        if (it == trailPoint.height + 1) TrailPoint(point = nextPoint, height = it) else null
    }
}

fun secondTask(input: Sequence<String>): String {
    val mountain =
        input.toList().parseToMap { c: Char -> "$c".toInt() }
    val starts = mountain.findStarts()
    return starts.sumOf { mountain.findTrails2(it) }.toString()
}

private fun Mountain.findTrails2(start: Point): Int {
    fun go(acc: List<TrailPoint>): Int =
        if (acc.isEmpty() || acc.all { it.height == 9 }) {
            acc.size
        } else {
            go(acc.flatMap { next(it) })
        }
    return go(listOf(TrailPoint(start, get(start))))
}

data class TrailPoint(
    val point: Point,
    val height: Int,
)
