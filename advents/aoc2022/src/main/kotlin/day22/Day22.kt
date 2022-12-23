package com.gilpereda.aoc2022.day22

import com.gilpereda.aoc2022.day05.start
import com.gilpereda.aoc2022.day22.Orientation.*
import kotlin.math.absoluteValue

fun firstTask(input: Sequence<String>): String = TODO()

fun secondTask(input: Sequence<String>): String = TODO()

fun Sequence<String>.parsed(): Pair<Maze, List<Movement>> {
    val (maze, movements) = joinToString("\n").split("\n\n")
    return Pair(parseMaze(maze), parseMovements(movements.split("\n").first()))
}

fun parseMaze(lines: String): Maze {
    val lines = lines.split("\n")
        .filter(String::isNotBlank)
    val rows = lines.map(::parseMazeLine)
    val columns = lines.traversed.map(::parseMazeLine)
    return Maze(rows = rows, columns = columns)
}

private fun parseMazeLine(line: String): Line =
    line.foldIndexed(Line(-1, -1, emptyList())) { index, acc, next ->
        when {
            next == '.' && acc.start == -1 -> acc.copy(start = index)
            next == '#' -> acc.copy(
                start = if (acc.start == -1) index else acc.start,
                rocksAt = acc.rocksAt + index
            )
            else -> acc
        }
    }.copy(end = line.length - 1)

private val List<String>.traversed: List<String>
    get() {
        val width = maxOf { it.length }
        return map { it.fillWithWhiteSpace(width) }
            .fold(List(width) { "" }) { acc, next ->
                acc.zip(next.toList())
                    .map { (one, other) -> one + other }
            }.map { it.trimEnd() }
    }

private fun String.fillWithWhiteSpace(length: Int): String =
    this + List(length - this.length) { ' ' }


private fun parseMovements(line: String): List<Movement> {
    tailrec fun go(rest: String, acc: List<Movement>, current: String? = null): List<Movement> =
        when (val next = rest.firstOrNull()) {
            null -> acc + current.toMovement
            'R' -> go(rest.drop(1), acc + current.toMovement + TurnRight, null)
            'L' -> go(rest.drop(1), acc + current.toMovement + TurnLeft, null)
            else -> go(rest.drop(1), acc, current?.let { it + next } ?: "$next")
        }
    return go(line, emptyList())
}

private val String?.toMovement: List<Movement>
    get() = listOfNotNull(this?.let { Go(it.toInt()) })


data class Maze(
    val rows: List<Line>,
    val columns: List<Line>,
)

data class State(
    val maze: Maze,
    val x: Int,
    val y: Int,
    val orientation: Orientation
) {
    val row = maze.rows[y]
    val column = maze.columns[x]

    fun next(movement: Movement): State =
        when (movement) {
            is Turn -> copy(orientation = movement.next(orientation))
            is Go -> when (orientation.direction) {
                Direction.VERTICAL -> moveVertically(orientation.steps(movement))
                Direction.HORIZONTAL -> moveHorizontally(orientation.steps(movement))
            }
        }

    private fun moveVertically(steps: Int): State =
        copy(y = (y + steps).toIndex(column.end + 1))

    private fun moveHorizontally(steps: Int): State =
        copy(x = (x + steps).toIndex(row.end + 1))
}

sealed interface Movement

data class Go(val steps: Int) : Movement {

    override fun toString(): String = "$steps"
}

fun Int.toIndex(length: Int): Int =
    when {
        this in 0 until length -> this
        this >= length -> (this % length)
        else -> ((((this / length).absoluteValue + 1) * length + this) % length)
    }.toInt()


interface Turn : Movement {
    fun next(orientation: Orientation): Orientation
}

object TurnRight : Turn {
    override fun next(orientation: Orientation): Orientation =
        when (orientation) {
            RIGHT -> DOWN
            UP -> RIGHT
            LEFT -> UP
            DOWN -> LEFT
        }

    override fun toString(): String = "R"
}

object TurnLeft : Turn {
    override fun next(orientation: Orientation): Orientation =
        when (orientation) {
            RIGHT -> UP
            UP -> LEFT
            LEFT -> DOWN
            DOWN -> RIGHT
        }

    override fun toString(): String = "L"
}

enum class Orientation(
    val direction: Direction,
    val steps: (movement: Go) -> Int,
) {
    UP(Direction.VERTICAL, { -it.steps }),
    DOWN(Direction.VERTICAL, { it.steps }),
    RIGHT(Direction.HORIZONTAL, { it.steps }),
    LEFT(Direction.HORIZONTAL, { -it.steps }),
}

enum class Direction {
    VERTICAL,
    HORIZONTAL
}

data class Line(
    val start: Int,
    val end: Int,
    val rocksAt: List<Int> = emptyList(),
) {
    val width: Int = end - start + 1
    override fun toString(): String = "$start -- $end : $rocksAt"
}