package com.gilpereda.aoc2022.day22

import com.gilpereda.aoc2022.day22.Orientation.*
import kotlin.math.absoluteValue

fun firstTask(input: Sequence<String>): String {
    val (maze, movements) = input.toList().parsed()

    val state = movements
        .fold(State.init(maze)) { acc, movement ->
        acc.next(movement)
    }
    println(state)
    return state.result.toString()
}



fun List<String>.parsed(): Pair<Maze, List<Movement>> {
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

val List<String>.traversed: List<String>
    get() {
        val width = maxOf { it.length }
        return map { it.fillWithWhiteSpace(width) }
            .fold(List(width) { "" }) { acc, next ->
                acc.zip(next.toList())
                    .map { (one, other) -> one + other }
            }.map { it.trimEnd() }.filter { it.isNotBlank() }
    }

private fun String.fillWithWhiteSpace(length: Int): String =
    this + List(length - this.length) { ' ' }.joinToString("")


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
) {
    override fun toString(): String = rows.joinToString("\n")
}

data class State(
    val maze: Maze,
    val x: Int,
    val y: Int,
    val orientation: Orientation,
    val path: Map<Position, Orientation> = mapOf(Position(x, y) to orientation),
) {
    private val row = maze.rows[y]
    private val column = maze.columns[x]

    override fun toString(): String =
        maze.rows.mapIndexed { y, row ->
            row.toString().mapIndexed { x, cell ->
                path[Position(x, y)] ?: cell
            }.joinToString("")
        }.joinToString("\n")

    val result: Int by lazy {
        1000 * (y + 1) + 4 * (x + 1) + orientation.value
    }

    private val currentPosition: Position = Position(x, y)

    fun next(movement: Movement): State =
        when (movement) {
            is Turn -> movement.next(orientation).let { copy(orientation = it, path = path + mapOf(currentPosition to it)) }
            is Go -> when (orientation.direction) {
                Direction.VERTICAL -> moveVertically(orientation.steps(movement))
                Direction.HORIZONTAL -> moveHorizontally(orientation.steps(movement))
            }
        }

    private fun moveVertically(steps: Int): State =
        move(y, steps, column) { newY, passed -> copy(y = newY, path = path + passed.associate { Position(x = x, y = it) to orientation }.toMap()) }

    private fun moveHorizontally(steps: Int): State =
        move(x, steps, row) { newX, passed -> copy(x = newX, path = path + passed.associate { Position(x = it, y = y) to orientation }.toMap()) }

    private fun move(current: Int, steps: Int, line: Line, update: (Int, List<Int>) -> State): State {
        tailrec fun go(acc: Pair<Int, List<Int>>, rest: Int): Pair<Int, List<Int>> =
            when {
                rest == 0 -> acc
                rest > 0 -> {
                    val new = line.indexOf(acc.first + 1)
                    if (line.canMoveTo(new)) {
                        go(new to (acc.second + new), rest - 1)
                    } else {
                        acc
                    }
                }
                else -> {
                    val new = line.indexOf(acc.first - 1)
                    if (line.canMoveTo(new)) {
                        go(new to (acc.second + new), rest + 1)
                    } else {
                        acc
                    }
                }
            }

        val (next, path) = go(current to listOf(current), steps)
        return update(next, path)
    }

    companion object {
        fun init(maze: Maze): State =
            State(
                maze = maze,
                x = maze.rows.first().start,
                y = 0,
                orientation = RIGHT
            )
    }
}

data class Position(val x: Int, val y: Int)



sealed interface Movement

data class Go(val steps: Int) : Movement {

    override fun toString(): String = "$steps"
}



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
    val value: Int,
    val symbol: String,
) {
    UP(Direction.VERTICAL, { -it.steps }, 3, "^"),
    DOWN(Direction.VERTICAL, { it.steps }, 1, "v"),
    RIGHT(Direction.HORIZONTAL, { it.steps }, 0, ">"),
    LEFT(Direction.HORIZONTAL, { -it.steps }, 2, "<");

    override fun toString(): String = symbol
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
    private val width: Int = end - start + 1

    override fun toString(): String =
        (0 .. end).map {
            when {
                it < start -> ' '
                it in rocksAt -> '#'
                else -> '.'
            }
        }.joinToString("")

    fun canMoveTo(position: Int): Boolean = position !in rocksAt

    fun indexOf(position: Int): Int =
        (position- start).toIndex() + start

    private fun Int.toIndex(): Int =
        when {
            this in 0 until width -> this
            this >= width -> (this % width)
            else -> ((((this / width).absoluteValue + 1) * width + this) % width)
        }.toInt()

}