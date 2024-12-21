package com.gilpereda.aoc2022.day18

import com.gilpereda.aoc2022.utils.geometry.Orientation
import com.gilpereda.aoc2022.utils.geometry.Point
import com.gilpereda.aoc2022.utils.geometry.Polygon

private val LINE_REGEX = "(.{1}) (\\d+) \\((.+)\\)".toRegex()

/**
 * 68624 too high
 * 47045
 */
fun firstTask(input: Sequence<String>): String {
    val commands = input.map { parseLine(it) }.toList()
    return Game(commands).solve().toString()
}

fun secondTask(input: Sequence<String>): String =
    Game(input.map { parseLine2(it) }.toList()).solve().toString()

fun MutableMap<Int, MutableMap<Int, Boolean>>.dump(): String =
    rangeY().map { y ->
        rangeX().map { x -> if (get(Point.from(x, y))) "#" else "." }.joinToString("")
    }.joinToString("\n")

fun parseLine(line: String): Command =
    LINE_REGEX.find(line)?.destructured?.let { (o, d, c) ->
        Command(o.toOrientation(), d.toInt(), c)
    } ?: throw IllegalArgumentException("Could not parse line $line")

fun parseLine2(line: String): Command =
    LINE_REGEX.find(line)?.destructured?.let { (_, _, code) ->
        val orientation = when(code.last()) {
            '0' -> "R"
            '1' -> "D"
            '2' -> "L"
            '3' -> "U"
            else -> throw IllegalArgumentException("Not possible")
        }.toOrientation()
        val distance = code.drop(1).dropLast(1).toInt(radix = 16)
        Command(orientation, distance, code)
    } ?: throw IllegalArgumentException("Could not parse line $line")

fun String.toOrientation(): Orientation =
    when (this) {
        "U" -> Orientation.NORTH
        "D" -> Orientation.SOUTH
        "R" -> Orientation.EAST
        "L" -> Orientation.WEST
        else -> throw IllegalArgumentException("Not possible")
    }

data class Command(
    val orientation: Orientation,
    val length: Int,
    val color: String,
)

class Game(
    private val commands: List<Command>
) {
    fun solve(): Long {
        val start = Point.from(0, 0)
        val points = commands.fold(listOf(start)) { acc, command ->
            acc + acc.last().move(command.orientation, command.length)
        }
        return Polygon(points).external().area.toLong()
    }
}

fun MutableMap<Int, MutableMap<Int, Boolean>>.get(point: Point): Boolean =
    get(point.y)?.get(point.x) ?: false

fun MutableMap<Int, MutableMap<Int, Boolean>>.set(point: Point): MutableMap<Int, MutableMap<Int, Boolean>> {
    computeIfAbsent(point.y) { mutableMapOf() }.computeIfAbsent(point.x) { true }
    return this
}

fun MutableMap<Int, MutableMap<Int, Boolean>>.rangeX(): IntRange =
    values.fold(0 .. 0) { acc, line ->
        val newMin = minOf(acc.min(), line.keys.min())
        val newMax = maxOf(acc.max(), line.keys.max())
        newMin .. newMax
    }

fun MutableMap<Int, MutableMap<Int, Boolean>>.rangeY(): IntRange =
    keys.min() .. keys.max()
