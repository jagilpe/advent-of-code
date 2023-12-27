package com.gilpereda.aoc2022.day03

import kotlin.math.abs

private val NUMBER_REGEX = "(\\d+)".toRegex()

fun firstTask(input: Sequence<String>): String {
    val lines = input.toList()
    val symbols = findSymbols(lines)
    return findNumbers(lines)
        .filter { number -> symbols.any { number.isAdjacentTo(it) } }
        .sumOf { it.value }.toString()
}

fun secondTask(input: Sequence<String>): String {
    val lines = input.toList()
    val numbers = findNumbers(lines)
    return findSymbols(lines)
        .map { it.gearRatio(numbers) }
        .sum().toString()
}

fun findNumbers(input: List<String>): List<N> =
    input.flatMapIndexed { index, line -> findNumbers(index, line) }

fun findNumbers(line: Int, input: String): List<N> =
    NUMBER_REGEX.findAll(input).toList().map { N.fromMatch(it, line) }

fun findSymbols(input: List<String>): List<Symbol> =
    input.toList().flatMapIndexed { index, line -> findSymbols(index, line) }

fun findSymbols(line: Int, text: String): List<Symbol> =
    text.mapIndexedNotNull { column, c ->
            if (c.isDigit() || c == '.') {
                null
            } else {
                Symbol(symbol = c, line = line, column = column)
            }
        }

data class N(
    val value: Int,
    val line: Int,
    val position: IntRange,
) {
    fun isAdjacentTo(symbol: Symbol): Boolean =
        abs(line - symbol.line) < 2 && symbol.column in position.extended

    private val IntRange.extended: IntRange
        get() = (position.first - 1) .. (position.last + 1)

    companion object {
        fun fromMatch(result: MatchResult, line: Int): N =
            N(value = result.value.toInt(), line = line, position = result.range)
    }
}

data class Symbol(
    val symbol: Char,
    val line: Int,
    val column: Int,
) {
    fun gearRatio(numbers: List<N>): Int {
        val adjacent = numbers.filter { it.isAdjacentTo(this) }
        return if (adjacent.size == 2) {
            adjacent.fold(1) { acc, next -> acc * next.value }
        } else {
            0
        }
    }
}