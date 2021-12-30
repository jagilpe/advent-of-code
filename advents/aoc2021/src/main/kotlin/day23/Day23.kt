package com.gilpereda.adventsofcode.adventsofcode2021.day23

import com.gilpereda.adventsofcode.adventsofcode2021.Executable
import com.gilpereda.adventsofcode.adventsofcode2021.day23.Amphipod.*

/*
#############
#12.3.4.5.67#
###0#0#0#0###
  #1#1#1#1#
  #2#2#2#2#
  #3#3#3#3#
  #########
 */

val initial: Board = Board(
    hall = List(7) { Free },
    rooms = mapOf(
        AMBER to listOf(Occupied(BRONZE), Occupied(AMBER, false)),
        BRONZE to listOf(Occupied(COPPER), Occupied(DESERT)),
        COPPER to listOf(Occupied(BRONZE), Occupied(COPPER, false)),
        DESERT to listOf(Occupied(DESERT), Occupied(AMBER)),
    )
)

val part1: (Board) -> Int = { board ->

    val winners = generateSequence(Round.firstRound(board), Round::next)
        .mapIndexed { index, round ->
            if (index % 10_000 == 0) println("open: ${round.open.size}, finished: ${round.finished.size}, visited: ${round.visited.size}, discarded: ${round.discarded}")
            round
        }
        .last()

    val game = winners.finished.minByOrNull { it.cost }

    println("cost: ${game!!.cost}")
    game!!.cost
}

val part2: Executable = { TODO() }


data class Round(
    val open: List<Game> = emptyList(),
    val finished: List<Game> = emptyList(),
    val visited: Map<Int, Int> = emptyMap(),
    val discarded: Int = 0,
    val minCost: Int = Int.MAX_VALUE
) {

    fun next(): Round? {
        if (open.isEmpty()) return null

        val game = open.last()
        return if (game.finished) {
            val newFinished = finished + game
            val newMinCost = newFinished.minOfOrNull { it.cost } ?: Int.MAX_VALUE
            println("found one with cost: ${game.cost}")
            println("current minimum cost: $minCost")
            copy(open = open.dropLast(1), finished = newFinished, visited = visited + mapOf(game.board.hashCode() to game.cost), minCost = newMinCost)
        } else {
            val (next, newDiscarded) = game.next().partition { newGame ->
                val board = newGame.board
                val visitedCost = visited[board.hashCode()]
                board.finished || !visited.containsKey(board.hashCode()) || visitedCost == null || newGame.cost < visitedCost
            }
            copy(
                open = open.dropLast(1) + next,
                visited = visited + mapOf(game.board.hashCode() to game.cost),
                discarded = discarded + newDiscarded.size
            )
        }
    }

    companion object {
        fun firstRound(board: Board): Round = Round(open = listOf(Game(board)))
    }
}

enum class Amphipod(val consume: Int) {
    AMBER(1),
    BRONZE(10),
    COPPER(100),
    DESERT(1000)
}

sealed interface CellContent

object Free : CellContent {
    override fun toString(): String = "."
}

data class Occupied(val amphipod: Amphipod, val unblocked: Boolean = true) : CellContent {
    override fun toString(): String = "${amphipod.name.first()}"
}

data class Game(
    val board: Board,
    val moves: List<Move> = emptyList()
) {
    val finished: Boolean
        get() = board.finished

    val cost: Int
        get() = moves.sumOf { it.cost }

    fun next(): List<Game> =
        (nextFromHall() + nextFromRooms())
//            .sortedBy { it.cost }
            .sortedByDescending { it.cost }

    private fun nextFromHall(): List<Game> =
        board.hall
            .flatMapIndexed { hallCell, content ->
                if (content is Occupied) {
                    hallToPath[hallCell]?.filter {
                        it.isNotBlocked && it.room == content.amphipod && roomIsFree(it.room)
                    } ?: emptyList()
                } else {
                    emptyList()
                }
            }
            .mapNotNull(this::moveFromHallToRoom)

    private fun nextFromRooms(): List<Game> =
        board.rooms.filter { (_, content) ->
            content.filterIsInstance<Occupied>()
                .firstOrNull()?.unblocked ?: false
        }.flatMap { (room, _) ->
            roomToPath[room]!!.filter { it.isNotBlocked && hallIsFree(it.hall) }
        }
            .mapNotNull(this::moveFromRoomToHall)

    private fun moveFromHallToRoom(path: Path): Game? {
        val roomIndex = board.rooms[path.room]!!.lastIndexOf(Free)
        return if (roomIndex != -1) {
            val hall = board.hall.mapIndexed { id, cell ->
                if (id == path.hall) Free
                else cell
            }
            val rooms = board.rooms.mapValues { (room, cells) ->
                if (room == path.room) {
                    cells.mapIndexed { i, cell -> if (i == roomIndex) Occupied(room, false) else cell }
                } else {
                    cells
                }
            }
            val amphipod = path.room
            val steps = path.steps + roomIndex
            val move = Move(amphipod, from = "H${path.hall}", to = "R-$amphipod-$roomIndex", cost = steps * amphipod.consume, steps = steps)
            Game(Board(hall, rooms), moves = moves + move)
        } else {
            null
        }
    }

    private fun moveFromRoomToHall(path: Path): Game? {
        val cellContent = board.rooms[path.room]!!
            .mapIndexed { index, cell -> index to cell  }
            .firstOrNull { (_, cell) -> cell is Occupied }
        return if (cellContent != null) {
            val (roomIndex, content) = cellContent
            val amphipod = (content as Occupied).amphipod
            val hall = board.hall.mapIndexed { id, cell ->
                if (id == path.hall) Occupied(amphipod)
                else cell
            }
            val rooms = board.rooms.mapValues { (room, cells) ->
                if (room == path.room) {
                    cells.mapIndexed { i, cell -> if (i == roomIndex) Free else cell }
                } else {
                    cells
                }
            }
            val steps = path.steps + roomIndex
            val move = Move(amphipod, from = "R-${path.room}-$roomIndex", to = "H${path.hall}", cost = steps * amphipod.consume, steps = steps)
            Game(Board(hall, rooms), moves = moves + move)
        } else {
            null
        }
    }

    private val Path.isNotBlocked: Boolean
        get() = board.hall.filterIndexed { i, cell -> i in cellsBetween && cell is Occupied }.isEmpty()

    private fun roomIsFree(amphipod: Amphipod): Boolean =
        board.rooms[amphipod]!!.all { it is Free || (it is Occupied && it.amphipod == amphipod) }

    private fun hallIsFree(hall: Int): Boolean =
        board.hall[hall] is Free
}

data class Move(
    val amphipod: Amphipod,
    val from: String,
    val to: String,
    val cost: Int,
    val steps: Int,
)

data class Board(
    val hall: List<CellContent>,
    val rooms: Map<Amphipod, List<CellContent>>
) {
    val finished: Boolean
        get() = hall.all { it is Free } && rooms.all { (amphipod, room) -> room.all { it is Occupied && it.amphipod == amphipod } }

    override fun toString(): String = """
        ##############
        #${hall[0]}${hall[1]}.${hall[2]}.${hall[3]}.${hall[4]}.${hall[5]}${hall[6]}#
        ###${rooms[AMBER]!![0]}#${rooms[BRONZE]!![0]}#${rooms[COPPER]!![0]}#${rooms[DESERT]!![0]}###
        ###${rooms[AMBER]!![1]}#${rooms[BRONZE]!![1]}#${rooms[COPPER]!![1]}#${rooms[DESERT]!![1]}###
          ##########
    """.trimIndent()
}

val paths: List<Path> =
    listOf(
        Path(0, AMBER, 3, listOf(1)),
        Path(0, BRONZE, 5, listOf(1, 2)),
        Path(0, COPPER, 7, listOf(1, 2, 3)),
        Path(0, DESERT, 9, listOf(1, 2, 3, 4)),
        Path(1, AMBER, 2, emptyList()),
        Path(1, BRONZE, 4, listOf(2)),
        Path(1, COPPER, 6, listOf(2, 3)),
        Path(1, DESERT, 8, listOf(2, 3, 4)),
        Path(2, AMBER, 2, emptyList()),
        Path(2, BRONZE, 2, emptyList()),
        Path(2, COPPER, 4, listOf(3)),
        Path(2, DESERT, 6, listOf(3, 4)),
        Path(3, AMBER, 4, listOf(2)),
        Path(3, BRONZE, 2, emptyList()),
        Path(3, COPPER, 2, emptyList()),
        Path(3, DESERT, 4, listOf(4)),
        Path(4, AMBER, 6, listOf(2, 3)),
        Path(4, BRONZE, 4, listOf(3)),
        Path(4, COPPER, 2, emptyList()),
        Path(4, DESERT, 2, emptyList()),
        Path(5, AMBER, 8, listOf(2, 3, 4)),
        Path(5, BRONZE, 6, listOf(3, 4)),
        Path(5, COPPER, 4, listOf(4)),
        Path(5, DESERT, 2, emptyList()),
        Path(6, AMBER, 9, listOf(2, 3, 4, 5)),
        Path(6, BRONZE, 7, listOf(3, 4, 5)),
        Path(6, COPPER, 5, listOf(4, 5)),
        Path(6, DESERT, 3, listOf(5)),
    )

data class Path(
    val hall: Int,
    val room: Amphipod,
    val steps: Int,
    val cellsBetween: List<Int>,
)

val hallToPath: Map<Int, List<Path>> = paths.groupBy { it.hall }

val roomToPath: Map<Amphipod, List<Path>> = paths.groupBy { it.room }
