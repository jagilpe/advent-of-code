package com.gilpereda.adventsofcode.adventsofcode2021.day10

import arrow.core.Either
import arrow.core.left
import arrow.core.right

fun checkSyntax(input: Sequence<String>): String =
    input.map { it.toList() }
        .map(::checkLine)
        .map { result -> result.fold({ 0 }, { it })}
        .sum().toString()

fun checkIncomplete(input: Sequence<String>): String =
    input.map { it.toList() }
        .map(::checkLineCompletion)
        .filter { it != 0L }
        .toList()
        .pickScore().toString()

private val startingChars = listOf('(', '{', '<', '[')
private val endingChars = listOf(')', '}', '>', ']')

private fun checkLine(line: List<Char>): Either<List<Char>, Long> {
    tailrec fun go(chars: List<Char>, acc: List<Char>): Either<List<Char>, Long> {
        return when (val head = chars.firstOrNull()) {
            null -> acc.left()
            in startingChars -> go(chars.drop(1), listOf(head) + acc)
            in endingChars -> {
                if (head matchesOpening acc.first()) {
                    go(chars.drop(1), acc.drop(1))
                } else {
                    head.points.right()
                }
            }
            else -> throw IllegalArgumentException()
        }
    }
    return go(line, emptyList())
}

private fun checkLineCompletion(line: List<Char>): Long =
    checkLine(line).fold({ it.points }, { 0 })

private infix fun Char.matchesOpening(opening: Char): Boolean =
    (this == ']' && opening == '[') || (this == '}' && opening == '{') || (this == ')' && opening == '(') || (this == '>' && opening == '<')

private fun List<Long>.pickScore(): Long =
    sorted()[(size / 2)]


private val Char.points: Long
    get() = when (this) {
        ')' -> 3
        ']' -> 57
        '}' -> 1197
        '>' -> 25137
        '(' -> 1
        '[' -> 2
        '{' -> 3
        '<' -> 4
        else -> throw IllegalArgumentException()
    }

val List<Char>.points: Long
    get() = fold(0) { acc, c ->
        (acc * 5) + c.points
    }