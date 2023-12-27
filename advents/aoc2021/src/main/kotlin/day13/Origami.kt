package com.gilpereda.adventsofcode.adventsofcode2021.day13

import arrow.core.Either
import arrow.core.left
import arrow.core.right

fun origamiOneFold(input: Sequence<String>): String =
    parseInput(input)
        .let { (points, folds) ->
            folds.first().foldAlong(points).size
        }.toString()

fun parseInput(input: Sequence<String>): Pair<Set<Point>, List<Fold>> =
    input
        .mapNotNull(::parseLine)
        .fold(Pair(emptySet(), emptyList())) { acc, next ->
            next.fold(
                { point -> acc.copy(first = acc.first + point) },
                { fold -> acc.copy(second = acc.second + fold) }
            )
        }

fun getCode(input: Sequence<String>): String =
    parseInput(input)
        .let { (points, fold) ->
            fold.fold(points) { acc, next ->
                next.foldAlong(acc)
            }
        }
        .asString()


fun Set<Point>.asString(): String {
    val height = this.maxOf(Point::y)
    val width = this.maxOf(Point::x)
    val pointsMap = this.groupBy(Point::y, Point::x)

    return (0..height).joinToString("\n") { y ->
        (0..width).map { x -> pointsMap[y]?.let { if (it.contains(x)) '#' else '.' } ?: '.' }.joinToString("")
    }
}

private fun parseLine(line: String): Either<Point, Fold>? =
    when {
        line.contains(",") -> Point.of(line.split(",")).left()
        line.startsWith("fold along y=") -> VerticalFold(line.split("fold along y=")[1].toInt()).right()
        line.startsWith("fold along x=") -> HorizontalFold(line.split("fold along x=")[1].toInt()).right()
        else -> null
    }

data class Point(val x: Int, val y: Int) {
    companion object {
        fun of(coord: List<String>): Point {
            val (x, y) = coord
            return Point(x.toInt(), y.toInt())
        }
    }
}

sealed interface Fold {
    fun foldAlong(points: Set<Point>): Set<Point>
}

data class VerticalFold(val row: Int) : Fold {
    override fun foldAlong(points: Set<Point>): Set<Point> =
        points.mapNotNull { point ->
            val y = point.y
            when {
                y == row -> null
                y > row -> point.copy(y = 2 * row - y)
                else -> point
            }
        }.toSet()
}

data class HorizontalFold(val column: Int) : Fold {
    override fun foldAlong(points: Set<Point>): Set<Point> =
        points.mapNotNull { point ->
            val x = point.x
            when {
                x == column -> null
                x > column -> point.copy(x = 2 * column - x)
                else -> point
            }
        }.toSet()
}