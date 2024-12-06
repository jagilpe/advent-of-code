package com.gilpereda.aoc2024.day04

import com.gilpereda.adventofcode.commons.geometry.Point
import com.gilpereda.adventofcode.commons.map.TypedTwoDimensionalMap
import com.gilpereda.adventofcode.commons.map.parseToMap

private val XMAS_REGEX = "(XMAS)".toRegex()

fun firstTask(input: Sequence<String>): String {
    val list = input.toList()
    val map = list.parseToMap { c -> c }

    val horizontal = map.rows { it.joinToString("") }.findXmasInLines()
    val vertical = map.columns { it.joinToString("") }.findXmasInLines()
    val diagonal = map.diagonal()
    val diagonal2 = map.diagonal2()
    val diagonalCount = diagonal.findXmasInLines()
    val diagonalCount2 = diagonal2.findXmasInLines()
    return (horizontal + vertical + diagonalCount + diagonalCount2).toString()
}

private fun List<String>.findXmasInLines(): Int = map(::findXmasInLine).sum()

private fun findXmasInLine(line: String): Int = countMatches(line) + countMatches(line.reversed())

private fun countMatches(line: String): Int = XMAS_REGEX.findAll(line).count()

private fun TypedTwoDimensionalMap<Char>.diagonal(): List<String> = diagonalLines().map { it.joinToString("") }

private fun TypedTwoDimensionalMap<Char>.diagonal2(): List<String> = diagonalLines2().map { it.joinToString("") }

fun secondTask(input: Sequence<String>): String {
    val map = input.toList().parseToMap { c -> c }

    return sequence {
        var x = 0
        var y = 0
        while (y < map.height) {
            val centerIsA = map.centerIsA(x, y)
            val diagonal1Matches = map.diagonal1Matches(x, y)
            val diagonal2Matches = map.diagonal2Matches(x, y)
            yield(centerIsA && diagonal1Matches && diagonal2Matches)
            if (x == map.width - 1) {
                x = 0
                y += 1
            } else {
                x += 1
            }
        }
    }.count { it }.toString()
}

private fun TypedTwoDimensionalMap<Char>.centerIsA(
    x: Int,
    y: Int,
): Boolean = getNullable(Point.from(x + 1, y + 1)) == 'A'

private fun TypedTwoDimensionalMap<Char>.diagonal1Matches(
    x: Int,
    y: Int,
): Boolean =
    getNullable(Point.from(x, y))
        ?.let { one ->
            getNullable(Point.from(x + 2, y + 2))
                ?.let { other ->
                    (one == 'M' && other == 'S') || (one == 'S' && other == 'M')
                }
        } ?: false

private fun TypedTwoDimensionalMap<Char>.diagonal2Matches(
    x: Int,
    y: Int,
): Boolean =
    getNullable(Point.from(x + 2, y))
        ?.let { one ->
            getNullable(Point.from(x, y + 2))
                ?.let { other ->
                    (one == 'M' && other == 'S') || (one == 'S' && other == 'M')
                }
        } ?: false
