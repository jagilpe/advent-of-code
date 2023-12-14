package com.gilpereda.aoc2022.day14

import com.gilpereda.aoc2022.utils.*

fun firstTask(input: Sequence<String>): String =
    input.toList().parsed()
        .tilt(Orientation.NORTH)
        .load.toString()

fun secondTask(input: Sequence<String>): String {
    val result = generateSequence(input.toList().parsed()) { it.cycle() }
        .take(1)
        .map { it.map.dump { "$it" } }
        .toList().joinToString("\n\n")
    return ""
}

data class Surface(
    val map: TwoDimensionalMap<Cell>
) {
    val load: Long
        get() = map.valuesToListIndexed()
            .sumOf { (point, cell) ->
                if (cell is Cell.RoundRock)
                    map.height - point.y
                else 0L
            }

    fun cycle(): Surface =
        tilt(Orientation.NORTH)
            .tilt(Orientation.WEST)
            .tilt(Orientation.SOUTH)
            .tilt(Orientation.EAST)

    fun tilt(orientation: Orientation): Surface {
        val next = Surface(map.transform(orientation).mapValues { (_, line) -> tiltLine(line) }.transformBack(orientation))
        println("After tilting $orientation\n${next.map.dump()}\n")
        return next
    }

    private fun tiltLine(line: Map<Long, Cell>): Map<Long, Cell> =
        generateSequence(line) {
            val firstEmpty: Long? = null
            it.entries.fold(Pair(firstEmpty,it)) { (first, acc), (x, cell) ->
                when {
                    x == 0L -> Pair(if (cell is Cell.Empty) 0 else null, acc)
                    first == null -> when (cell) {
                        is Cell.Empty -> Pair(x, acc)
                        else -> Pair(null, acc)
                    }
                    else -> when (cell) {
                        is Cell.Empty -> Pair(first, acc)
                        is Cell.RoundRock -> Pair(first + 1L, acc + mapOf(
                            first to Cell.RoundRock,
                            x to Cell.Empty
                        ))
                        is Cell.CubeRock -> Pair(null, acc)
                    }
                }
            }.second
        }
            .first { !canTilt(it)}

    private fun canTilt(line: Map<Long, Cell>): Boolean =
        line.values.windowed(2, 1).any { it == listOf(Cell.Empty, Cell.RoundRock) }
}

fun List<String>.parsed(): Surface =
    Surface(
        toTwoDimensionalMap {
            when (it) {
                'O' -> Cell.RoundRock
                '#' -> Cell.CubeRock
                else -> Cell.Empty
            }
        })
sealed interface Cell {
    data object Empty : Cell {
        override fun toString(): String = "."
    }
    data object RoundRock : Cell {
        override fun toString(): String = "O"
    }
    data object CubeRock : Cell {
        override fun toString(): String = "#"
    }
}