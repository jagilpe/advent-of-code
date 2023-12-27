package com.gilpereda.aoc2022.day04

import kotlin.math.pow

fun firstTask(input: Sequence<String>): String =
    input.map { parseLine(it).value }.sum().toString()

fun secondTask(input: Sequence<String>): String {
    val cards = input.map { parseLine(it) }.toList()
    val fold = cards.fold(cards.associate { it.id to 1 }) { acc, card ->
        val winning = card.winningCount
        val cardCount = acc[card.id]!!
        ((card.id + 1)..(card.id + winning))
            .fold(acc) { acc2, next -> acc2 + (next to (acc2[next]!!+ cardCount)) }
    }
    return fold.values.sum().toString()
}

fun parseLine(line: String): Card {
    val (first, rest) = line.split(": ")
    val id = first.split(" ").firstNotNullOf { it.toIntOrNull() }
    val (winning, numbers) = rest.split(" | ")
    return Card(
        id = id,
        winning = findNumbers(winning),
        numbers = findNumbers(numbers),
    )
}

fun findNumbers(line: String): List<Int> =
    line.split(" ").mapNotNull { it.toIntOrNull() }

data class Card(
    val id: Int,
    val winning: List<Int>,
    val numbers: List<Int>,
    val count: Int = 1,
) {
    val value: Long
        get() =
            when (winningCount) {
                0 -> 0
                else -> 2.0.pow(winningCount - 1).toLong()
            }

    val winningCount: Int
        get() = numbers.count { it in winning }
}