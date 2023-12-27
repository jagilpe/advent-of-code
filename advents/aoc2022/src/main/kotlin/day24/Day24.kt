package com.gilpereda.aoc2022.day24

import com.gilpereda.aoc2022.day24.Cell.Companion.D
import com.gilpereda.aoc2022.day24.Cell.Companion.L
import com.gilpereda.aoc2022.day24.Cell.Companion.R
import com.gilpereda.aoc2022.day24.Cell.Companion.U
import com.gilpereda.aoc2022.day24.Orientation.*
import java.time.LocalDateTime
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

fun firstTask(input: Sequence<String>): String {
    val maze = Maze(input.toList().parsed())
    return Expedition.init(maze).findBestPath(maze.start, maze.end, 0).time.toString()
}

fun secondTask(input: Sequence<String>): String {
    val maze = Maze(input.toList().parsed())
    val start = maze.start
    val end = maze.end
    val expedition = Expedition.init(maze)
    val there = expedition.findBestPath(start, end, 0)
    println("Found way there in ${there.time} min")
    val back = expedition.findBestPath(end, start, there.time)
    println("Found way back in ${back.time} min")
    return expedition.findBestPath(start, end, back.time).time.toString()
}


fun List<String>.parsed(): MazeMap =
    filter { it.isNotBlank() }
        .map { line ->
            line.map {
                when (it) {
                    '#' -> Wall
                    '>' -> R
                    '^' -> U
                    'v' -> D
                    '<' -> L
                    else -> Empty
                }
            }
        }

typealias MazeMap = List<List<Cell>>

val MazeMap.maze
    get() = Maze(this)


data class Expedition(
    private val mazeList: MutableList<Maze>,
) {
    fun mazeAt(time: Int): Maze =
        when (val maze = mazeList.getOrNull(time)) {
            null -> mazeAt(time - 1).next.also { mazeList.add(it) }
            else -> maze
        }

    fun findBestPath(start: Coordinate, goal: Coordinate, time: Int): Step {
        tailrec fun go(current: Step, open: Set<Step>, iter: Int = 1): Step {
            if (iter % 1_000_000 == 0) {
                println("${LocalDateTime.now()} - iteration $iter - current: ${current.distanceToGoal} - open: ${open.size} - goal: $goal.")
            }
            return if (current.coordinate == goal) {
                current
            } else {
                val nextOpen: Set<Step> = open + current.nextSteps
                val next = nextOpen.minBy { it.fScore }
                go(next, nextOpen - next, iter + 1)
            }
        }

        return go(Step(coordinate = start, goal = goal, time = time), setOf())
    }

    private fun Coordinate.isFree(time: Int): Boolean =
        mazeAt(time).isEmpty(this)


    inner class Step(
        val coordinate: Coordinate,
        val goal: Coordinate,
        val time: Int = 0,
    ) {
        val distanceToGoal: Int = coordinate.distanceTo(goal)
        val fScore: Int = time + distanceToGoal

        val nextSteps: List<Step>
            get() = coordinate.movements
                .filter { it.isFree(time + 1) }
                .map { Step(coordinate = it, time = time + 1, goal = goal) }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Step

            if (coordinate != other.coordinate) return false
            if (time != other.time) return false
            if (goal != other.goal) return false

            return true
        }

        override fun hashCode(): Int {
            var result = coordinate.hashCode()
            result = 31 * result + time
            result = 31 * result + goal.hashCode()
            return result
        }
    }

    companion object {
        fun init(maze: Maze): Expedition =
            generateSequence(maze) { it.next }.take(500).toMutableList().let { Expedition(it) }
    }
}

fun MazeMap.evolution(rounds: Int): Expedition =
    generateSequence(Maze(this)) { it.next }.take(rounds + 1).toMutableList().let { Expedition(it) }

data class Maze(
    private val mazeMap: MazeMap,
) {
    private val width: Int = mazeMap.first().size
    private val height: Int = mazeMap.size

    val start: Coordinate by lazy {
        Coordinate(x = mazeMap.first().firstNotEmpty, 0)
    }

    val end: Coordinate by lazy {
        Coordinate(x = mazeMap.last().firstNotEmpty, height - 1)
    }

    private val List<Cell>.firstNotEmpty: Int
        get() = mapIndexed { index, cell -> index to cell }.first { it.second is Empty }.first

    fun isEmpty(coordinate: Coordinate): Boolean =
        coordinate.inMap && mazeMap[coordinate.y][coordinate.x] == Empty

    private val Coordinate.inMap: Boolean
        get() = x in 0 until width && y in 0 until height

    val next: Maze by lazy {
        mazeMap.flatMapIndexed { y, line ->
            line.flatMapIndexed { x, cell ->
                cell.next(Coordinate(x, y)).fix()
            }
        }.groupBy({ it.first }, { it.second })
            .mapValues { (_, cells) ->
                when {
                    cells.isEmpty() -> throw Exception("Cannot be empty")
                    cells.size > 1 -> cells.filterIsInstance<Blizzard>().reduce { acc, t -> acc + t }
                    else -> cells.first()
                }
            }
            .let { coordToCell ->
                (mazeMap.indices).map { y ->
                    (0 until width).map { x ->
                        coordToCell[Coordinate(x, y)] ?: Empty
                    }
                }
            }.maze
    }

    override fun toString(): String =
        mazeMap.joinToString("\n") { line -> line.joinToString("") }

    private fun List<Pair<Coordinate, Cell>>.fix(): List<Pair<Coordinate, Cell>> =
        map { it.copy(it.first.fix(it.second)) }

    private fun Coordinate.fix(cell: Cell): Coordinate =
        when (cell) {
            is Blizzard -> when {
                y == 0 -> copy(y = height - 2)
                y == height - 1 -> copy(y = 1)
                x == 0 -> copy(x = width - 2)
                x == width - 1 -> copy(x = 1)
                else -> this
            }

            else -> this
        }
}

data class Coordinate(
    val x: Int,
    val y: Int,
) {
    override fun toString(): String = "($x, $y)"

    fun distanceTo(other: Coordinate): Int =
        abs(x - other.x) + abs(y - other.y)

    val movements: List<Coordinate> by lazy {
        listOf(up, down, right, left, this)
    }

    private val up: Coordinate
        get() = copy(y = y - 1)

    private val down: Coordinate
        get() = copy(y = y + 1)

    private val right: Coordinate
        get() = copy(x = x + 1)

    private val left: Coordinate
        get() = copy(x = x - 1)
}

sealed interface Cell {

    fun next(coordinate: Coordinate): List<Pair<Coordinate, Cell>>

    companion object {
        val W = Wall

        val o = Empty

        val U: Blizzard = Blizzard(UP)

        val D: Blizzard = Blizzard(DOWN)

        val R: Blizzard = Blizzard(RIGHT)

        val L: Blizzard = Blizzard(LEFT)
    }
}

object Wall : Cell {
    override fun toString(): String = "#"

    override fun next(coordinate: Coordinate): List<Pair<Coordinate, Cell>> = listOf(coordinate to this)
}

object Empty : Cell {
    override fun toString(): String = "."

    override fun next(coordinate: Coordinate): List<Pair<Coordinate, Cell>> = emptyList()
}

data class Blizzard(
    val orientations: Set<Orientation>,
) : Cell {
    constructor(orientation: Orientation) : this(setOf(orientation))

    override fun next(coordinate: Coordinate): List<Pair<Coordinate, Cell>> =
        orientations.map { it.move(coordinate) to Blizzard(it) }

    override fun toString(): String = when (orientations.size) {
        1 -> when (orientations.first()) {
            UP -> "^"
            DOWN -> "v"
            RIGHT -> ">"
            LEFT -> "<"
        }

        else -> orientations.size.toString()
    }

    operator fun plus(other: Blizzard): Blizzard =
        copy(orientations = orientations + other.orientations)
}

enum class Orientation(val move: (Coordinate) -> Coordinate) {
    UP({ it.copy(y = it.y - 1) }),
    DOWN({ it.copy(y = it.y + 1) }),
    RIGHT({ it.copy(x = it.x + 1) }),
    LEFT({ it.copy(x = it.x - 1) }),
}