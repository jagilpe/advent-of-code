package com.gilpereda.aoc2022.day02

val gameRegex = "Game (\\d+):(.*)".toRegex()
val greenRegex = "(\\d+) green".toRegex()
val blueRegex = "(\\d+) blue".toRegex()
val redRegex = "(\\d+) red".toRegex()

val maxTurn1 = Turn(
    green = 13,
    blue = 14,
    red = 12,
)

fun firstTask(input: Sequence<String>): String =
    input.mapNotNull { parseLine(it) }
        .filter { it.isPossible(maxTurn1) }
        .map { it.game }.sum().toString()

fun secondTask(input: Sequence<String>): String =
    input.mapNotNull { parseLine(it) }
        .map { it.power() }.sum().toString()

fun parseLine(line: String): Game? =
    gameRegex.find(line)?.destructured?.let { (gameNum, rest) ->
        val turns = rest.split(";").map { findTurn(it) }
        Game(gameNum.toInt(), turns)
    }

fun findTurn(part: String): Turn {
    val green = greenRegex.find(part)?.destructured?.let { (green) ->
        green.toInt()
    } ?: 0
    val blue = blueRegex.find(part)?.destructured?.let { (blue) ->
        blue.toInt()
    } ?: 0
    val red = redRegex.find(part)?.destructured?.let { (red) ->
        red.toInt()
    } ?: 0
    return Turn(
        green = green,
        blue = blue,
        red = red,
    )
}

data class Game(
    val game: Int,
    val turns: List<Turn>,
) {
    fun isPossible(maxTurn: Turn): Boolean =
        turns.all { it.isPossible(maxTurn) }

    fun power(): Int =
        turns.fold(Turn(0, 0 , 0)) { acc, turn ->
            Turn(
                green = maxOf(acc.green, turn.green),
                blue = maxOf(acc.blue, turn.blue),
                red = maxOf(acc.red, turn.red),
            )
        }.power()

}

data class Turn(
    val green: Int,
    val blue: Int,
    val red: Int,
) {
    fun isPossible(maxTurn: Turn): Boolean =
        green <= maxTurn.green && blue <= maxTurn.blue && red <= maxTurn.red

    fun power(): Int = green * blue * red
}