@file:OptIn(ExperimentalStdlibApi::class)

package com.gilpereda.adventsofcode.adventsofcode2021.day23

import com.gilpereda.adventsofcode.adventsofcode2021.Executable


val part1Old: Executable = { input ->
    val game = parse(input)

    winner(listOf(game))
    TODO()
}

val part2Old: Executable = { TODO() }

val played = mutableListOf<List<List<OldCell>>>()
val winner: DeepRecursiveFunction<List<OldGame>, List<OldGame>> = DeepRecursiveFunction { games ->
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

data class OldGame(val cellMap: List<List<OldCell>>, val moves: List<OldMove> = emptyList()) {
    override fun toString(): String =
        cellMap.joinToString("\n") { it.joinToString("") }

    private fun OldRoom.accepts(amphipod: Amphipod): Boolean =
        free && amphipod == accepted
                && cellMap.flatten().filter { it is OldRoom && it.accepted == amphipod }
            .all { it.free || it.amphipod == amphipod }

    fun canMoveTo(from: Point, to: Point): Boolean {
        val origin = cell(from)
        val amphipod = origin.amphipod
        return if (amphipod != null && (origin !is OldRoom || !origin.revisited )) {
            when (val destination = cell(to)) {
                is OldHall -> destination.free && origin is OldRoom
                is OldRoom -> destination.accepts(amphipod)
                else -> false
            }
        } else {
            false
        }
    }

    fun canMoveThrough(through: Point): Boolean = cell(through).free

    private fun makeMove(move: OldMove): OldGame {
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

    fun nextMoves(): Sequence<OldGame> =
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

    private fun nextMovesFrom(amphipod: Amphipod, point: Point): List<OldMove> {
        tailrec fun go(visited: Set<Point>, neighbours: List<Point>, consumed: Int, acc: List<OldMove>): List<OldMove> {
            val moves = neighbours.filter { it !in visited && canMoveTo(point, it) }
                .map { OldMove(amphipod, point, it, consumed + amphipod.consume) }
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
        get() = cellMap.flatten().filterIsInstance<OldRoom>().all { it.amphipod == it.accepted }

    private fun cell(point: Point): OldCell = cellMap[point.y][point.x]
}

val Point.neighbours: List<Point>
    get() = listOf(
        Point(x - 1, y),
        Point(x, y - 1),
        Point(x + 1, y),
        Point(x, y + 1),
    )

data class OldMove(val amphipod: Amphipod, val from: Point, val to: Point, val consumed: Int)

fun parse(input: Sequence<String>): OldGame =
    input.mapIndexed { y, row ->
        row.mapIndexed { x, char ->
            when (char) {
                '#' -> Wall
                '.' -> parseEmptyCell(x, y)
                else -> parseOccupiedCell(char, x, y)
            }
        }
    }.toList().let(::OldGame)

fun parseEmptyCell(x: Int, y: Int): OldCell =
    when (y) {
        1 -> when (x) {
            3, 5, 7, 9 -> Door
            else -> OldHall()
        }
        else -> when (x) {
            3 -> OldRoom(Amphipod.AMBER)
            5 -> OldRoom(Amphipod.BRONZE)
            7 -> OldRoom(Amphipod.COPPER)
            9 -> OldRoom(Amphipod.DESERT)
            else -> throw Exception("illegal room point $x, $y")
        }
    }

fun parseOccupiedCell(char: Char, x: Int, y: Int): OldCell {
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
            else -> OldHall(amphipod)
        }
        else -> when (x) {
            3 -> OldRoom(Amphipod.AMBER, amphipod)
            5 -> OldRoom(Amphipod.BRONZE, amphipod)
            7 -> OldRoom(Amphipod.COPPER, amphipod)
            9 -> OldRoom(Amphipod.DESERT, amphipod)
            else -> throw Exception("illegal room point $x, $y")
        }
    }
}


sealed interface OldCell {
    val free: Boolean
    val amphipod: Amphipod?
    fun empty(): OldCell
    fun fill(amphipod: Amphipod?): OldCell
}

object Wall : OldCell {
    override val free: Boolean = false
    override val amphipod: Amphipod? = null
    override fun empty(): OldCell = throw UnsupportedOperationException()
    override fun fill(amphipod: Amphipod?): OldCell = throw UnsupportedOperationException()

    override fun toString(): String = "#"
}

data class OldHall(override val amphipod: Amphipod? = null) : OldCell {
    override val free: Boolean = amphipod == null
    override fun empty(): OldCell = copy(amphipod = null)
    override fun fill(amphipod: Amphipod?): OldCell = copy(amphipod = amphipod)

    override fun toString(): String = if (amphipod != null) "${amphipod.name.first()}" else "."
}

object Door : OldCell {
    override val free: Boolean = true
    override val amphipod: Amphipod? = null
    override fun empty(): OldCell = throw UnsupportedOperationException()
    override fun fill(amphipod: Amphipod?): OldCell = throw UnsupportedOperationException()

    override fun toString(): String = "x"
}

data class OldRoom(val accepted: Amphipod, override val amphipod: Amphipod? = null) : OldCell {
    var revisited: Boolean = false
    override val free: Boolean = amphipod == null
    override fun empty(): OldCell = copy(amphipod = null)
    override fun fill(amphipod: Amphipod?): OldCell = copy(amphipod = amphipod).apply { revisited = true }

    override fun toString(): String = if (amphipod != null) "${amphipod.name.first()}" else "o"
}

data class Point(val x: Int, val y: Int) {
    override fun toString(): String = "($x, $y)"
}