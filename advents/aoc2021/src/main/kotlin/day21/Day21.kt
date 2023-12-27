@file:OptIn(ExperimentalStdlibApi::class)

package com.gilpereda.adventsofcode.adventsofcode2021.day21

import arrow.core.flatten


const val PLAYER_1 = 1
const val PLAYER_2 = 2

fun part1(player1Start: Int, player2Start: Int): Int {
    val game = generateSequence(Game.forStart(player1Start, player2Start), Game::next).first(Game::finished)
    return game.looser!!.score * (game.rolls)
}

val winners: DeepRecursiveFunction<Game, Triple<Long, Long, Long>> = DeepRecursiveFunction { game ->
    if (game.finished) {
        if (game.winner!! == PLAYER_1) {
            Triple(1, 0, 1)
        } else {
            Triple(0, 1, 1)
        }
    } else {
        possibleCosmicRolls.map { (round, count) ->
            with (callRecursive(game.nextRound(round))) {
                Triple(first * count, second * count, third * count)
            }
        }.reduce { one, other -> Triple(one.first + other.first, one.second + other.second, one.third + other.third) }
    }
}

val possibleCosmicRolls: Map<Int, Int> =
    (1..3).flatMap { first ->
        (1..3).flatMap { second ->
            (1..3).map { third ->
                first + second + third
            }
        }
    }.groupBy { it }.mapValues { (_, values) -> values.size }

fun part2(player1Start: Int, player2Start: Int): Pair<Long, Long> {
    val result = winners(Game.forStart(player1Start, player2Start, 21))
    return Pair(result.first, result.second)
}

private fun cosmicRoll(game: Game): Sequence<Game> = (1..2).mapNotNull { dice -> if (!game.finished) game.next(dice) else null }.asSequence()

data class Game(
    val player1: Player,
    val player2: Player,
    val winner: Int? = null,
    private val dice: Int = 0,
    val rolls: Int = 0,
    private val limit: Int = 1000,
) {
    fun next(): Game = next(roll())

    fun nextRound(result: Int): Game {
        if (finished) throw IllegalStateException("Game already finished")
        val next = if (inTurn == PLAYER_1) {
            val newPlayer1 = player1.move(result, true)
            val winner = if (winner != null || finished(newPlayer1)) player1.id else null
            copy(player1 = newPlayer1, winner = winner, dice = dice, rolls = rolls + 3)
        } else {
            val newPlayer2 = player2.move(result, true)
            val winner = if (winner != null || finished(newPlayer2)) player2.id else null
            copy(player2 = newPlayer2, winner = winner, dice = dice, rolls = rolls + 3)
        }
        return next
    }

    fun next(dice: Int): Game {
        if (finished) throw IllegalStateException("Game already finished")

        val next = if (inTurn == PLAYER_1) {
            val newPlayer1 = player1.move(dice, lastRollInTurn)
            val winner = if (winner != null || ((finished(newPlayer1)) && lastRollInTurn)) player1.id else null
            copy(player1 = newPlayer1, winner = winner, dice = dice, rolls = rolls + 1)
        } else {
            val newPlayer2 = player2.move(dice, lastRollInTurn)
            val winner = if (winner != null || ((finished(newPlayer2)) && lastRollInTurn)) player2.id else null
            copy(player2 = newPlayer2, winner = winner, dice = dice, rolls = rolls + 1)
        }
        return next
    }

    val finished: Boolean = winner != null

    private fun finished(player: Player): Boolean = player.score >= limit

    val looser: Player? =
        when (winner) {
            1 -> player2
            2 -> player1
            else -> null
        }

    private val inTurn: Int
        get() = when (rolls % 6) {
            0, 1, 2 -> PLAYER_1
            else -> PLAYER_2
        }

    private val lastRollInTurn: Boolean = rolls % 3 == 2

    private fun roll(): Int = if (dice >= 100) 1 else dice + 1


    companion object {
        fun forStart(player1Start: Int, player2Start: Int, limit: Int = 1000): Game =
            Game(player1 = Player(1, player1Start), player2 = Player(2, player2Start), limit = limit)
    }
}

data class Player(val id: Int, val position: Int, val score: Int = 0) {
    fun move(moves: Int, lastMoveInTurn: Boolean): Player {
        val endPosition = ((position + moves - 1) % 10) + 1
        return copy(
            position = endPosition,
            score = score + if (lastMoveInTurn) endPosition else 0
        )
    }
}