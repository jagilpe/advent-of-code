package com.gilpereda.adventsofcode.adventsofcode2021.day21


const val PLAYER_1 = 1
const val PLAYER_2 = 2

fun part1(player1Start: Int, player2Start: Int): Int {
    val game = generateSequence(Game.forStart(player1Start, player2Start), Game::next).first(Game::finished)
    return game.looser!!.score * (game.rolls)
}

data class Game(
    val player1: Player,
    val player2: Player,
    val winner: Int? = null,
    private val dice: Int = 0,
    val rolls: Int = 0,
    private val limit: Int = 1000,
) {
    fun next(): Game = next(roll())

    fun next(dice: Int): Game {
        if (finished) throw IllegalStateException("Game already finished")

        val next = if (inTurn == PLAYER_1) {
            val newPlayer1 = player1.move(dice, lastRollInTurn)
            val winner = if ((finished(newPlayer1)) && lastRollInTurn) player1.id else null
            copy(player1 = newPlayer1, winner = winner, dice = dice, rolls = rolls + 1)
        } else {
            val newPlayer2 = player2.move(dice, lastRollInTurn)
            val winner = if ((finished(newPlayer2)) && lastRollInTurn) player2.id else null
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
        fun forStart(player1Start: Int, player2Start: Int): Game =
            Game(Player(1, player1Start), Player(2, player2Start))
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

fun part2(player1: Int, player2: Int): Int {
    TODO()
}