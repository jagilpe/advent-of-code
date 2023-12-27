package com.gilpereda.adventsofcode.adventsofcode2021.day05

import kotlin.math.abs
import kotlin.math.sign

private val lineRegex = "(\\d+),(\\d+) -> (\\d+),(\\d+)".toRegex()

fun hydrothermal(input: Sequence<String>): String {
    return input.map(::parseLine)
        .filter(Line::relevant)
        .flatMap { it.points }
        .groupBy { it }
        .filter {(_, value) -> value.size > 1 }
        .count().toString()
}

fun hydrothermal2(input: Sequence<String>): String {
    return input.map(::parseLine)
        .filter(Line::relevant2)
        .flatMap { it.points2 }
        .groupBy { it }
        .filter {(_, value) -> value.size > 1 }
        .count().toString()
}


fun parseLine(line: String): Line =
    lineRegex.find(line)?.destructured
        ?.let { (x1, y1, x2, y2) -> Line(Point(x1.toInt(), y1.toInt()), Point(x2.toInt(), y2.toInt())) }
        ?: throw Exception("Could not parse line $line")

data class Line(val point1: Point, val point2: Point) {
    private val horizontal: Boolean = point1.y == point2.y
    private val vertical: Boolean = point1.x == point2.x
    private val lengthX: Int = point1.x - point2.x
    private val lengthY: Int = point1.y - point2.y
    private val diagonalPlus: Boolean = abs(lengthX) == abs(lengthY) && lengthX.sign == lengthY.sign
    private val diagonalMinus: Boolean = abs(lengthX) == abs(lengthY) && lengthX.sign != lengthY.sign

    val relevant: Boolean = horizontal || vertical

    val relevant2: Boolean = relevant || diagonalPlus || diagonalMinus

    val points: List<String>
        get() = when {
            horizontal -> horizontalPoints()
            vertical -> verticalPoints()
            else -> throw Exception("the line is not horizontal or vertical")
        }

    val points2: List<String>
        get() = when {
            diagonalPlus -> diagonalPlusPoints()
            diagonalMinus -> diagonalMinusPoints()
            else -> points
        }

    private fun horizontalPoints(): List<String> {
        val from = minOf(point1.x, point2.x)
        val to = maxOf(point1.x, point2.x)
        return (from..to).map { x -> Point(x, point1.y).coord }
    }

    private fun verticalPoints(): List<String> {
        val from = minOf(point1.y, point2.y)
        val to = maxOf(point1.y, point2.y)
        return (from..to).map { y -> Point(point1.x, y).coord }
    }

    private fun diagonalPlusPoints(): List<String> {
        val from = if (point1.x < point2.x) point1 else point2
        return (0..abs(lengthX))
            .map { steps -> from.copy(x = from.x + steps, y = from.y + steps).coord }
    }

    private fun diagonalMinusPoints(): List<String> {
        val from = if (point1.x < point2.x) point1 else point2
        return (0..abs(lengthX))
            .map { steps -> from.copy(x = from.x + steps, y = from.y - steps).coord }
    }
}

data class Point(val x: Int, val y: Int) {
    val coord: String = "$x,$y"
}