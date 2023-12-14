package com.gilpereda.aoc2022.day13

import com.gilpereda.aoc2022.utils.*

/**
 * 26580 -> too low
 * 32932 -> too low
 * 63090 -> too high
 * 62400 -> not right
 */
fun firstTask(input: Sequence<String>): String =
    input.toList().joinToString("\n")
        .split("\n\n")
        .map { Block.parsed(it) }
        .sumOf { it.totalPoints }.toString()

/**
 * 44200 -> too high
 */
fun secondTask(input: Sequence<String>): String =
    input.toList().joinToString("\n")
        .split("\n\n")
        .map { Block.parsed(it) }
        .sumOf { it.alternative }.toString()

data class Block(
    val map: TwoDimensionalMap<Char>
) {
    val totalPoints: Int by lazy {
        horizontalMirrors.sum() * 100 + verticalMirrors.sum()
    }

    val horizontalMirrors: List<Int> by lazy {
        findFold(map)
    }

    val verticalMirrors: List<Int> by lazy {
        findFold(map.transpose())
    }

    val alternative: Int by lazy {
        map.indices()
            .map { it to Block(map.set(it, map.getNotNullable(it).inversed)) }
            .firstNotNullOfOrNull { (point, it) ->
                val newHorizontal = it.horizontalMirrors - horizontalMirrors
                val newVertical = it.verticalMirrors - verticalMirrors
                if (newHorizontal.isNotEmpty() || newVertical.isNotEmpty()) {
                    Pair(newHorizontal, newVertical)
                } else null
            }
            ?.let { (horizontalMirror, verticalMirror) -> horizontalMirror.sum() * 100 + verticalMirror.sum() } ?: throw IllegalStateException("Could not find alternative")
    }


    private val Char.inversed: Char
        get() = when (this) {
            '.' -> '#'
            else -> '.'
        }

    private fun findFold(map: TwoDimensionalMap<Char>): List<Int> {
        val rows = map.rows { it.joinToString("") }
        return rows
            .asSequence()
            .mapIndexed { index, s -> index to s }
            .windowed(2, 1)
            .filter { (one, other) -> one.second == other.second }
            .map { it[0].first }
            .mapNotNull { if (rows.isFoldedOn(it)) it + 1 else null }
            .toList()
//            .firstOrNull { rows.isFoldedOn(it) }?.let { it + 1 } ?: 0
    }

    private fun List<String>.isFoldedOn(index: Int): Boolean =
        subList(index + 1, size).zip(subList(0, index + 1).reversed())
            .all { (one, other) -> one == other }

    companion object {
        fun parsed(block: String): Block =
            Block(block.split("\n").toTwoDimensionalMap { it })
    }
}

