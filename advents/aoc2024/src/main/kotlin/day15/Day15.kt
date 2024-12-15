package com.gilpereda.aoc2024.day15

import com.gilpereda.adventofcode.commons.geometry.Orientation
import com.gilpereda.adventofcode.commons.geometry.Point
import com.gilpereda.adventofcode.commons.map.TypedTwoDimensionalMap
import com.gilpereda.adventofcode.commons.map.parseToMap

fun firstTask(input: Sequence<String>): String = resolve(input.toList().joinToString("\n"))

fun secondTask(input: Sequence<String>): String =
    resolve(
        input
            .toList()
            .joinToString("\n")
            .replace("#", "##")
            .replace(".", "..")
            .replace("O", "[]")
            .replace("@", "@."),
    )

private fun resolve(input: String): String {
    val (mapString, commandsString) = input.split("\n\n")
    val initialGame = renderGame(mapString)
    val commands =
        commandsString
            .split("\n")
            .joinToString("")
            .map {
                when (it) {
                    '<' -> Orientation.WEST
                    '>' -> Orientation.EAST
                    '^' -> Orientation.NORTH
                    else -> Orientation.SOUTH
                }
            }

    println(initialGame)
    val game =
        commands.foldIndexed(initialGame) { index, game, orientation ->
            println("$index $orientation")
            game.move(orientation)
        }
    println(game)
    return game.result().toString()
}

fun renderGame(string: String): Game {
    lateinit var robot: Point
    val map =
        string.split("\n").parseToMap { point, c ->
            if (c == '@') {
                robot = point
            }
            Cell.from(c)
        }
    return Game(map, robot)
}

enum class Cell(
    val s: String,
) {
    Wall("#"),
    Box("O"),
    BoxLeft("["),
    BoxRight("]"),
    Empty("."),
    ;

    override fun toString(): String = s

    companion object {
        fun from(c: Char): Cell =
            when (c) {
                '#' -> Wall
                'O' -> Box
                '[' -> BoxLeft
                ']' -> BoxRight
                else -> Empty
            }
    }
}

class Game(
    private val map: TypedTwoDimensionalMap<Cell>,
    initialRobot: Point,
) {
    private var robot = initialRobot

    override fun toString(): String = map.dumpWithIndex { point, cell -> if (point == robot) "@" else cell.toString() }

    override fun equals(other: Any?): Boolean = other is Game && robot == other.robot && map == other.map

    fun move(orientation: Orientation): Game {
        val destination = robot.move(orientation)
        val moved = mutableSetOf<Point>()
        robot =
            if (canMoveTo(destination, orientation)) {
                moved.add(destination)
                val destinationCell = map[destination]
                if (orientation.isVertical) {
                    when (destinationCell) {
                        Cell.BoxLeft -> {
                            val rightBoxDestination = destination.move(Orientation.EAST)
                            moved.add(rightBoxDestination)
                            move(moved, rightBoxDestination, orientation)
                            map[rightBoxDestination] = Cell.Empty
                        }
                        Cell.BoxRight -> {
                            val leftBoxDestination = destination.move(Orientation.WEST)
                            moved.add(leftBoxDestination)
                            move(moved, leftBoxDestination, orientation)
                            map[leftBoxDestination] = Cell.Empty
                        }
                        else -> {}
                    }
                }
                move(moved, destination, orientation)
                map[destination] = Cell.Empty
                destination
            } else {
                robot
            }
        return this
    }

    private fun canMoveTo(
        destination: Point,
        orientation: Orientation,
    ): Boolean =
        when (map[destination]) {
            Cell.Wall -> false
            Cell.Box -> canMoveTo(destination.move(orientation), orientation)
            Cell.BoxLeft ->
                when (orientation) {
                    Orientation.EAST, Orientation.WEST -> canMoveTo(destination.move(orientation), orientation)
                    Orientation.SOUTH, Orientation.NORTH ->
                        canMoveTo(destination.move(orientation), orientation) &&
                            canMoveTo(destination.move(Orientation.EAST).move(orientation), orientation)
                }

            Cell.BoxRight ->
                when (orientation) {
                    Orientation.EAST, Orientation.WEST -> canMoveTo(destination.move(orientation), orientation)
                    Orientation.SOUTH, Orientation.NORTH ->
                        canMoveTo(destination.move(orientation), orientation) &&
                            canMoveTo(destination.move(Orientation.WEST).move(orientation), orientation)
                }

            Cell.Empty -> true
        }

    private fun move(
        moved: MutableSet<Point>,
        point: Point,
        orientation: Orientation,
    ) {
        val destination = point.move(orientation)
        val cell = map[point]
        println("moving from $point to $destination")
        if (destination!in moved) {
            moved.add(destination)
            when (map[destination]) {
                Cell.Wall -> throw IllegalArgumentException("Not allowed to move to a wall")
                Cell.Empty -> {
                    map[destination] = cell
                }
                Cell.Box -> {
                    move(moved, destination, orientation)
                    map[destination] = cell
                }

                Cell.BoxLeft -> {
                    if (orientation.isVertical) {
                        val rightBox = point.move(Orientation.EAST)
                        if (rightBox !in moved) {
                            moved.add(rightBox)
                            map[rightBox] = Cell.Empty
                        }
                        val rightBoxDestination = destination.move(Orientation.EAST)
                        moved.add(rightBoxDestination)
                        move(moved, rightBoxDestination, orientation)
                    }
                    move(moved, destination, orientation)
                    map[destination] = cell
                }

                Cell.BoxRight -> {
                    if (orientation.isVertical) {
                        val leftBox = point.move(Orientation.WEST)
                        if (leftBox !in moved) {
                            moved.add(leftBox)
                            map[leftBox] = Cell.Empty
                        }
                        val leftBoxDestination = destination.move(Orientation.WEST)
                        moved.add(leftBoxDestination)
                        move(moved, leftBoxDestination, orientation)
                    }
                    move(moved, destination, orientation)
                    map[destination] = cell
                }
            }
        }
    }

    fun result(): Long =
        map.valuesIndexed().sumOf { (point, cell) ->
            when (cell) {
                Cell.Box, Cell.BoxLeft -> 100L * point.y + point.x
                else -> 0
            }
        }
}
