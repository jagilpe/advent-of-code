package com.gilpereda.adventsofcode.adventsofcode2021.day21


fun part1(player1Start: Int, player2Start: Int): Int {

    val game = Game(Player(1, player1Start, 0), Player(2, player2Start, 0))
    var dice = 1
    var rolls = 0
    while (!game.finished) {
        val rollsPlayer1 = rollDice(dice)
        dice = rollsPlayer1.first
        game.player1 = game.player1.move(rollsPlayer1.second)
        rolls += 3
        if (game.finished) break

        val rollsPlayer2 = rollDice(dice)
        dice = rollsPlayer2.first
        game.player2 = game.player2.move(rollsPlayer2.second)
        rolls += 3
        if (game.finished) break
    }

    return game.looser.score * (rolls)
}

fun rollDice(dice: Int): Pair<Int, Int> {
    val rolls = generateSequence(dice) { if (it >= 100) 1 else it + 1 }.take(3).toList()
    val newDice = if (rolls.last() >= 100) 1 else rolls.last() + 1
    return Pair(newDice, rolls.sum())
}

data class Game(var player1: Player, var player2: Player) {
    val finished: Boolean
        get() = player1.score >= 1000 || player2.score >= 1000

    val looser: Player
        get() = if (player1.score >= 1000) player2 else player1
}

data class Player(val id: Int, val position: Int, val score: Int = 0) {
    fun move(moves: Int): Player {
        val endPosition = ((position + moves - 1) % 10) + 1
        return copy(
            position = endPosition,
            score = score + endPosition
        )
    }
}

fun part2(player1: Int, player2: Int): Int {
    TODO()
}