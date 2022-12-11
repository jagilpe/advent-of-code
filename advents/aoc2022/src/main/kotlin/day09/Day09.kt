package com.gilpereda.aoc2022.day09

import kotlin.math.abs

fun firstTask(input: Sequence<String>): String =
    task(input, 2)

fun secondTask(input: Sequence<String>): String =
    task(input, 10)

private fun task(input: Sequence<String>, knots: Int): String =
    input.flatMap(::parseLine)
        .fold(State.forKnots(knots)) { acc, next ->
            val nextStringPos = acc.current.move(next)
//            println(nextStringPos)
//            println("-------------------------")
            State(
                current = nextStringPos,
                visited = acc.visited.updateCount(nextStringPos.tail)
            )
        }
        .visited.keys.size.toString()

fun parseLine(line: String): Sequence<Command> =
    line.split(" ").let { (move, steps) ->
        List(steps.toInt()) { Command.of(move) }
    }.asSequence()

data class Position(
    val x: Int,
    val y: Int,
) {
    fun follow(other: Position): Position =
        when {
            isAdjacent(other) -> this
            isRightTwo(other) && isUpTwo(other)-> copy(x = x - 1, y = y - 1)
            isRightTwo(other) && isDownTwo(other)-> copy(x = x - 1, y = y + 1)
            isLeftTwo(other) && isUpTwo(other)-> copy(x = x + 1, y = y - 1)
            isLeftTwo(other) && isDownTwo(other)-> copy(x = x + 1, y = y + 1)
            isDownTwo(other) -> copy(x = other.x, y = y + 1)
            isUpTwo(other) -> copy(x = other.x, y = y - 1)
            isRightTwo(other) -> copy(x = x - 1, y = other.y)
            isLeftTwo(other) -> copy(x = x + 1, y = other.y)
            else -> throw Exception("Illegal position")
        }

    private fun isAdjacent(other: Position): Boolean =
        abs(x - other.x) <= 1 && abs(y - other.y) <= 1

    private fun isUpTwo(other: Position): Boolean =
        (y - other.y) == 2

    private fun isDownTwo(other: Position): Boolean =
        (other.y - y) == 2

    private fun isRightTwo(other: Position): Boolean =
        (x - other.x) == 2

    private fun isLeftTwo(other: Position): Boolean =
        (other.x - x) == 2
}

data class State(
    val current: StringPos,
    val visited: Map<Position, Int> = mapOf(Position(0, 0) to 1)
) {
    companion object {
        fun forKnots(num: Int): State = State(StringPos.forKnots(num))
    }
}

fun Map<Position, Int>.updateCount(position: Position): Map<Position, Int> =
    this + mapOf(position to ((this[position] ?: 0) + 1))

data class StringPos(
    private val knots: List<Position>,
) {
    companion object {
        fun forKnots(num: Int): StringPos = StringPos(List(num) { Position(0, 0) })
    }

    override fun toString(): String {
        return (0..5).map { y ->
            (0..5).map { x ->
                when (val index = knots.indexOf(Position(x, y))) {
                    0 -> "H"
                    -1 -> "."
                    knots.size - 1 -> "T"
                    else -> index
                }
            }.joinToString(" ")
        }.reversed().joinToString("\n")
    }

    val tail: Position
        get() = knots.last()

    fun move(command: Command): StringPos {
        val newHead = command.move(knots.first())
        return StringPos((updateKnots(listOf(newHead) + knots.drop(1))))
    }

    private fun updateKnots(knots: List<Position>): List<Position> =
        knots.foldIndexed(knots) { i, acc, _ ->
            if (i == 0) acc
            else acc.mapIndexed { j, current ->
                if (j == i) current.follow(acc[j - 1])
                else current
            }
        }
}

sealed interface Command {
    fun move(position: Position): Position

    companion object {
        fun of(move: String): Command =
            when (move) {
                "U" -> Up
                "R" -> Right
                "L" -> Left
                "D" -> Down
                else -> throw Exception("Unknown direction")
            }
    }
}

object Up : Command {
    override fun move(position: Position): Position =
        position.copy(y = position.y + 1)
}

object Down : Command {
    override fun move(position: Position): Position =
        position.copy(y = position.y - 1)
}

object Right : Command {
    override fun move(position: Position): Position =
        position.copy(x = position.x + 1)
}

object Left : Command {
    override fun move(position: Position): Position =
        position.copy(x = position.x - 1)
}