@file:OptIn(ExperimentalStdlibApi::class)

package com.gilpereda.adventsofcode.adventsofcode2021.day23

import com.gilpereda.adventsofcode.adventsofcode2021.Executable


val part1: Executable = { input ->
    val game = parse(input)

    winner(listOf(game))
    TODO()
}

val part2: Executable = { TODO() }

val played = mutableListOf<List<List<Cell>>>()
val winner: DeepRecursiveFunction<List<Game>, List<Game>> = DeepRecursiveFunction { games ->
    println(games.size)
    val (finished, notFinished) = games.partition { it.finished }
    played.addAll(games.map { it.cellMap })
    val next = notFinished.flatMap { it.nextMoves() }.filter { it.cellMap !in played }
    if (notFinished.isEmpty()) {
        finished
    } else {
        finished + callRecursive(next)
    }
}

data class Game(val cellMap: List<List<Cell>>, val moves: List<Move> = emptyList()) {
    override fun toString(): String =
        cellMap.joinToString("\n") { it.joinToString("") }

    private fun Room.accepts(amphipod: Amphipod): Boolean =
        free && amphipod == accepted
                && cellMap.flatten().filter { it is Room && it.accepted == amphipod }
            .all { it.free || it.amphipod == amphipod }

    fun canMoveTo(from: Point, to: Point): Boolean {
        val origin = cell(from)
        val amphipod = origin.amphipod
        return if (amphipod != null && (origin !is Room || !origin.revisited )) {
            when (val destination = cell(to)) {
                is Hall -> destination.free && origin is Room
                is Room -> destination.accepts(amphipod)
                else -> false
            }
        } else {
            false
        }
    }

    fun canMoveThrough(through: Point): Boolean = cell(through).free

    private fun makeMove(move: Move): Game {
        val (amphipod, from, to) = move
        val newCellMap = cellMap.mapIndexed { y, row ->
            row.mapIndexed { x, cell ->
                when {
                    x == from.x && y == from.y -> cell.empty()
                    x == to.x && y == to.y -> cell.fill(amphipod)
                    else -> cell
                }
            }
        }
        return copy(cellMap = newCellMap, moves = moves + move )
    }

    fun nextMoves(): Sequence<Game> =
        cellMap.flatMapIndexed { y, row ->
            row.flatMapIndexed { x, cell ->
                val amphipod = cell.amphipod
                if (amphipod != null) {
                    nextMovesFrom(amphipod, Point(x, y))
                } else {
                    emptyList()
                }
            }
        }.asSequence()
            .map { makeMove(it) }

    private fun nextMovesFrom(amphipod: Amphipod, point: Point): List<Move> {
        tailrec fun go(visited: Set<Point>, neighbours: List<Point>, consumed: Int, acc: List<Move>): List<Move> {
            val moves = neighbours.filter { it !in visited && canMoveTo(point, it) }
                .map { Move(amphipod, point, it, consumed + amphipod.consume) }
            val newNeighbours = neighbours
                .flatMap { it.neighbours }
                .filter { it !in visited && canMoveThrough(it) }
            return if (newNeighbours.isNotEmpty()) {
                go(visited + neighbours, newNeighbours, amphipod.consume + consumed, acc + moves)
            } else {
                acc + moves
            }
        }

        val neighbours = point.neighbours.filter { canMoveThrough(it) }
        return go(setOf(point), neighbours, 0, emptyList())
    }


    val finished: Boolean
        get() = cellMap.flatten().filterIsInstance<Room>().all { it.amphipod == it.accepted }

    private fun cell(point: Point): Cell = cellMap[point.y][point.x]
}

val Point.neighbours: List<Point>
    get() = listOf(
        Point(x - 1, y),
        Point(x, y - 1),
        Point(x + 1, y),
        Point(x, y + 1),
    )

data class Move(val amphipod: Amphipod, val from: Point, val to: Point, val consumed: Int)

fun parse(input: Sequence<String>): Game =
    input.mapIndexed { y, row ->
        row.mapIndexed { x, char ->
            when (char) {
                '#' -> Wall
                '.' -> parseEmptyCell(x, y)
                else -> parseOccupiedCell(char, x, y)
            }
        }
    }.toList().let(::Game)

fun parseEmptyCell(x: Int, y: Int): Cell =
    when (y) {
        1 -> when (x) {
            3, 5, 7, 9 -> Door
            else -> Hall()
        }
        else -> when (x) {
            3 -> Room(Amphipod.AMBER)
            5 -> Room(Amphipod.BRONZE)
            7 -> Room(Amphipod.COPPER)
            9 -> Room(Amphipod.DESERT)
            else -> throw Exception("illegal room point $x, $y")
        }
    }

fun parseOccupiedCell(char: Char, x: Int, y: Int): Cell {
    val amphipod = when (char) {
        'A' -> Amphipod.AMBER
        'B' -> Amphipod.BRONZE
        'C' -> Amphipod.COPPER
        'D' -> Amphipod.DESERT
        else -> throw IllegalArgumentException()
    }
    return when (y) {
        1 -> when (x) {
            3, 5, 7, 9 -> throw IllegalArgumentException("Doors can not be")
            else -> Hall(amphipod)
        }
        else -> when (x) {
            3 -> Room(Amphipod.AMBER, amphipod)
            5 -> Room(Amphipod.BRONZE, amphipod)
            7 -> Room(Amphipod.COPPER, amphipod)
            9 -> Room(Amphipod.DESERT, amphipod)
            else -> throw Exception("illegal room point $x, $y")
        }
    }
}


sealed interface Cell {
    val free: Boolean
    val amphipod: Amphipod?
    fun empty(): Cell
    fun fill(amphipod: Amphipod?): Cell
}

enum class Amphipod(val consume: Int) {
    AMBER(1),
    BRONZE(10),
    COPPER(100),
    DESERT(1000)
}

object Wall : Cell {
    override val free: Boolean = false
    override val amphipod: Amphipod? = null
    override fun empty(): Cell = throw UnsupportedOperationException()
    override fun fill(amphipod: Amphipod?): Cell = throw UnsupportedOperationException()

    override fun toString(): String = "#"
}

data class Hall(override val amphipod: Amphipod? = null) : Cell {
    override val free: Boolean = amphipod == null
    override fun empty(): Cell = copy(amphipod = null)
    override fun fill(amphipod: Amphipod?): Cell = copy(amphipod = amphipod)

    override fun toString(): String = if (amphipod != null) "${amphipod.name.first()}" else "."
}

object Door : Cell {
    override val free: Boolean = true
    override val amphipod: Amphipod? = null
    override fun empty(): Cell = throw UnsupportedOperationException()
    override fun fill(amphipod: Amphipod?): Cell = throw UnsupportedOperationException()

    override fun toString(): String = "x"
}

data class Room(val accepted: Amphipod, override val amphipod: Amphipod? = null) : Cell {
    var revisited: Boolean = false
    override val free: Boolean = amphipod == null
    override fun empty(): Cell = copy(amphipod = null)
    override fun fill(amphipod: Amphipod?): Cell = copy(amphipod = amphipod).apply { revisited = true }

    override fun toString(): String = if (amphipod != null) "${amphipod.name.first()}" else "o"
}

data class Point(val x: Int, val y: Int) {
    override fun toString(): String = "($x, $y)"
}