package com.gilpereda.aoc2024.day06

import com.gilpereda.adventofcode.commons.geometry.Orientation
import com.gilpereda.adventofcode.commons.geometry.Point
import com.gilpereda.adventofcode.commons.map.TypedTwoDimensionalMap
import com.gilpereda.adventofcode.commons.map.parseToMap

fun firstTask(input: Sequence<String>): String {
    val lines = input.toList()
    val map = lines.parseToMap(Cell::from)
    val guard = lines.findGuard()
    val visited = visitedCells(map, guard, setOf(guard)).map { it.point }.toSet()
    return (visited.count()).toString()
}

private tailrec fun visitedCells(
    map: TypedTwoDimensionalMap<Cell>,
    guard: Guard,
    visited: Set<Guard>,
): Set<Guard> =
    when (val next = guard.next(map)) {
        null -> visited
        else -> visitedCells(map, next, visited + next)
    }

fun secondTask(input: Sequence<String>): String {
    val lines = input.toList()
    val map = lines.parseToMap(Cell::from)
    val guard = lines.findGuard()
    val potential = visitedCells(map, guard, setOf(guard)).map { it.point }
    return sequence {
        var current = Point.from(0, 0)
        while (current.y < map.height) {
            if (current.x < map.width) {
                if (current in potential) {
                    when (map.get(current)) {
                        Cell.Free -> yield(map.mapIndexed { p, c -> if (p == current) Cell.Wall else c })
                        else -> {}
                    }
                }
                current = Point.from(current.x + 1, current.y)
            } else {
                current = Point.from(0, current.y + 1)
            }
            println(current)
        }
    }.count { it.entersLoop(guard) }.toString()
}

private fun TypedTwoDimensionalMap<Cell>.entersLoop(guard: Guard): Boolean {
    fun go(
        guard: Guard,
        visited: Set<Guard>,
    ): Boolean =
        when (val next = guard.next(this)) {
            null -> false
            in visited -> true
            else -> go(next, visited + guard)
        }

    return go(guard, setOf())
}

private fun List<String>.findGuard(): Guard =
    flatMapIndexed { y, line ->
        line.mapIndexed { x, cell ->
            when (cell) {
                '^' -> Guard(Point.from(x, y), Orientation.NORTH)
                '<' -> Guard(Point.from(x, y), Orientation.WEST)
                '>' -> Guard(Point.from(x, y), Orientation.EAST)
                'v' -> Guard(Point.from(x, y), Orientation.SOUTH)
                else -> null
            }
        }
    }.firstNotNullOf { it }

data class Guard(
    val point: Point,
    val orientation: Orientation,
) {
    fun next(map: TypedTwoDimensionalMap<Cell>): Guard? {
        val next = point.move(orientation, 1)
        return when (map.getNullable(next)) {
            null -> null
            Cell.Free -> Guard(next, orientation)
            Cell.Wall -> {
                val newOrientation = orientation.turnRight()
                Guard(point, newOrientation).next(map)
            }
        }
    }
}

sealed interface Cell {
    data object Free : Cell {
        override fun toString(): String = "."
    }

    data object Wall : Cell {
        override fun toString(): String = "#"
    }

    companion object {
        fun from(char: Char): Cell =
            when (char) {
                '#' -> Wall
                else -> Free
            }
    }
}
