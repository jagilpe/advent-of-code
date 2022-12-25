package com.gilpereda.aoc2022.day24

import com.gilpereda.aoc2022.day24.Cell.Companion.D
import com.gilpereda.aoc2022.day24.Cell.Companion.L
import com.gilpereda.aoc2022.day24.Cell.Companion.R
import com.gilpereda.aoc2022.day24.Cell.Companion.U
import com.gilpereda.aoc2022.day24.Orientation.*
import java.time.LocalDateTime
import kotlin.math.abs

fun firstTask(input: Sequence<String>): String =
    Route.initial(input.toList().parsed()).findBestRoute()
        .also {
            println(it)
        }
        .length.toString()

fun secondTask(input: Sequence<String>): String = TODO()


fun List<String>.parsed(): Maze =
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
        }.maze

typealias MazeMap = List<List<Cell>>

val MazeMap.maze
    get() = Maze(this)


data class Route(
    private val maze: Maze,
    private val start: Coordinate,
    private val end: Coordinate,
    private val path: List<Coordinate> = listOf(start)
) : Comparable<Route> {
    val length: Int = path.size - 1

    private val finished: Boolean by lazy { path.last() == end }

    private val current: Coordinate by lazy { path.last() }

    private val distanceToEnd: Int by lazy {
        abs(end.x - current.x) + abs(end.y - current.y)
    }

    private val potentialFinalLength: Int by lazy { length + distanceToEnd }

    private infix fun wins(other: Route?): Boolean =
        this.finished && (other == null || path.size < other.path.size)

    private infix fun canNotWin(other: Route?): Boolean =
        !(this canWin other)

    private infix fun canWin(other: Route?): Boolean =
        other == null || potentialFinalLength < other.potentialFinalLength

    fun findBestRoute(): Route {
        var lastStart = System.currentTimeMillis()
        val start = lastStart
        tailrec fun go(current: Route, bestRoute: Route?, open: List<Route>, iter: Int): Route {
            if (iter % 10_000 == 0) {
                val ellapsed = System.currentTimeMillis() - lastStart
                println("${LocalDateTime.now()} - iter: $iter, current: ${current.length}, best: ${bestRoute?.length ?: 0}, open: ${open.size}, in: $ellapsed")
                lastStart = System.currentTimeMillis()
            }

            val nextBest =
                if (current wins bestRoute)
                    current.also {
                        println("******** Found new best in ${System.currentTimeMillis() - start} and $iter iterations - still ${open.size} open routes ************")
                        println(current.path)
                    }
                else bestRoute
            val nextOpen = if (!current.finished) (open + current.nextRoutes(nextBest)).sorted() else open.prune(nextBest)

            return if (nextOpen.isNotEmpty()) {
                val next = nextOpen.first()
                go(next, nextBest, nextOpen.drop(1), iter + 1)
            } else {
                println("Found in $iter iterations")
                nextBest!!
            }
        }

        return go(this, null, emptyList(), 1)
    }

    private fun List<Route>.prune(newBest: Route?): List<Route> =
        filter { it canWin newBest }

    override fun compareTo(other: Route): Int =
//        distanceToEnd.compareTo(other.distanceToEnd)
        when (val dist = distanceToEnd.compareTo(other.distanceToEnd)) {
            0 -> length.compareTo(other.length)
            else -> dist
        }

    private fun nextRoutes(bestRoute: Route?): List<Route> {
        val nextMaze = maze.next
        return current.movements
            .filter { nextMaze.isEmpty(it) }
            .map { copy(maze = nextMaze, path = path + it) }
            .filter { it canWin bestRoute }
    }

    override fun toString(): String = maze.printWithExpedition(current)

    companion object {
        fun initial(maze: Maze): Route =
            Route(
                maze = maze,
                start = maze.start,
                end = maze.end,
            )
    }
}

data class Maze(
    private val mazeMap: MazeMap,
) {
    private val width: Int = mazeMap.first().size
    private val height: Int = mazeMap.size

    fun printWithExpedition(coordinate: Coordinate): String =
        mazeMap.mapIndexed { y, line ->
            line.mapIndexed { x, cell ->
                if (coordinate == Coordinate(x, y)) "E" else cell.toString()
            }.joinToString("")
        }.joinToString("\n")

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