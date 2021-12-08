package com.gilpereda.adventsofcode.adventsofcode2021.day04

private typealias Board = Array<Line>

private typealias Line = Array<Int>

tailrec fun bingo(numbers: List<Int>, boards: List<Board>): Int {
    val first = numbers.first()
    val nextBoards = boards.map { it.next(first) }
    val winning = nextBoards.lastOrNull { it.wins }
    return if (winning != null) {
        first * winning.sumUp
    } else {
        bingo(numbers.drop(1), nextBoards)
    }
}

data class Acc(val boards: List<Board2>, val winning: Board2? = null, val number: Int? = null) {
    companion object {
        fun from(boards: List<Board2>) = Acc(boards = boards)
    }
}

fun bingo2(numbers: List<Int>, boards: List<Board2>): Int =
    numbers.fold(Acc.from(boards)) { acc, nextNumber ->
        val nextBoards = acc.boards.map { it.next(nextNumber) }
        val winning = nextBoards.lastOrNull { it.wins }
        if (winning != null) {
            Acc(nextBoards.filter { !it.board.wins }, winning, nextNumber)
        } else {
            acc.copy(boards = nextBoards)
        }
    }.let { it.number!! * it.winning!!.board.sumUp }

data class Board2(val index: Int,val board: Board) {
    fun next(number: Int): Board2 = copy(board = board.next(number))

    val wins: Boolean
        get() = board.wins
}

fun Board.next(value: Int): Board =
    map { it.next(value) }.toTypedArray()

fun Line.next(value: Int): Line =
    map { if (it == value) -1 else it }.toTypedArray()

fun String.boards() =
    split("\n\n")
        .map { it.board() }

fun String.boards2() =
    split("\n\n")
        .mapIndexed { i, board -> Board2(i,board.board()) }

fun String.board(): Board =
    split("\n")
        .filter { it.isNotBlank() }
        .map { it.line() }.toTypedArray()

fun String.line(): Line =
    trim().split("\\s+".toRegex())
        .map(String::toInt)
        .toTypedArray()

val Board.wins: Boolean
    get() = horWins || verWins

val Board.horWins: Boolean
    get() = any { it.sum() == (-1 * it.size) }

val Board.verWins: Boolean
    get() = cols.any { it.sum() == (-1 * it.size) }

val Board.cols: List<List<Int>>
    get() = List(first().size) { i -> this.map { it[i] } }

val Board.sumUp: Int
    get() = map { it.sumUp }.sum()

val Line.sumUp: Int
    get() = filter { it != -1 }.sum()
