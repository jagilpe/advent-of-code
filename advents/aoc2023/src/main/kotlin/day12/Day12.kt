package com.gilpereda.aoc2022.day12

import java.time.Duration
import java.time.Instant

fun firstTask(input: Sequence<String>): String =
    input.parsed(1)
        .sumOf { it.matching() }.toString()

fun secondTask(input: Sequence<String>): String {
    val start = Instant.now()
    return input
        .parsed(5)
        .mapIndexed { index, springConditions ->
            val ellapsed = Duration.between(start, Instant.now())
            springConditions.matching().also { println("$index - $ellapsed - matching: $it") }
        }
        .sum()
        .toString()
}

fun unfold(line: String, count: Int): String =
    try {
        val (conditions, arrangements) = line.split(" ")
        val newConditions = List(count) { conditions }.joinToString("?")
        val newArrangements = List(count) { arrangements }.joinToString(",")
        "$newConditions $newArrangements"
    } catch (ex: Exception) {
        throw ex
    }

fun Sequence<String>.parsed(unfold: Int): Sequence<SpringConditions> =
    map { line ->
        List(unfold) { it + 1 }
            .fold(SpringConditions(line, 1)) { springConditions, i ->
                springConditions.nextLevel()
            }
        SpringConditions(line, unfold) }

data class SpringConditions(
    val line: String,
    val unfold: Int,
    val previousMatches: Map<Int, Long> = mapOf()
) {
    private val unfoldedSprings: String
    private val unfoldedExpected: List<Int>

    private val originalSprings: String
    private val originalExpected: List<Int>
    private val originalSpringsLength: Int
    private val originalExpectedSize: Int

    init {
        val (originalSprings, originalExpected) = line.split(" ")
        this.originalSprings = originalSprings
        this.originalSpringsLength = originalSprings.length
        this.originalExpected = originalExpected.split(",").mapNotNull { it.toIntOrNull() }
        this.originalExpectedSize = this.originalExpected.size
        val (springs, expectedStr) = unfold(line, unfold).split(" ")
        this.unfoldedSprings = springs
        this.unfoldedExpected = expectedStr.split(",").map { it.toInt() }
    }

    private val expectedCount = unfoldedExpected.size
    private val expectedSprings = unfoldedExpected.sum()

    private val starts = List(unfoldedExpected.size) { i -> unfoldedExpected.take(i + 1) }

    private val previousCount: Long by lazy {
        SpringConditions(line, unfold - 1).matching()
    }

    fun matching(): Long =
        sequence {
            findMatching(Conditions(unfoldedSprings))
        }.fold(0L) { acc, next -> acc + next }
//            .also { println("Matching: $it for unfold: $unfold") }

    private suspend fun SequenceScope<Long>.findMatching(base: Conditions) {
        base.next().forEach { next ->
            if (next.notFinished) {
                if (next.isPartlyFinishedMatching) {
//                println("Matched partly finished: $unfold")
                    yield(previousCount)
                } else if (next.canMatch()) {
                    findMatching(next)
                }
            } else {
                if (next.matches(unfoldedExpected)) yield(1L)
            }
        }
    }

    inner class Conditions(val conditions: String, ) {
        val summary = Summary.from(conditions)

        val rawConditions: List<String> = conditions.split(".").filter { it.isNotBlank() }
        val condition: List<Int> = rawConditions.map { it.count() }

        val knownConditions: List<Int> by lazy { rawConditions.takeWhile { !it.contains("?") }.map { it.count() } }

        val notFinished: Boolean = conditions.contains("?")

        val isPartlyFinishedMatching: Boolean by lazy {
            unfold > 1 &&
                    !conditions.substring(0..originalSpringsLength - 1).contains("?") &&
                    conditions[originalSpringsLength] == '.' &&
                    knownConditions == originalExpected
        }

        fun canMatch(): Boolean {
            return doesNotExceedExpectedSprings() &&
                    doesNotExceedExpectedCount() &&
                    canReachTheExpectedCount() &&
                    knownConditionsStartMatches()
        }

        private fun knownConditionsStartMatches(): Boolean =
            knownConditions.isEmpty() || knownConditions == starts[knownConditions.size - 1]

        private fun doesNotExceedExpectedSprings(): Boolean =
            summary.filled <= expectedSprings

        private fun canReachTheExpectedCount(): Boolean =
            summary.filled + summary.pending >= expectedSprings

        private fun doesNotExceedExpectedCount(): Boolean =
            rawConditions.count { it.contains('#') } <= expectedCount

        fun next(): List<Conditions> =
            try {
                listOf(
                    Conditions(conditions.replaceFirst("?", "#")),
                    Conditions(conditions.replaceFirst("?", "."))
                )
            } catch (ex: Exception) {
                throw ex
            }

        fun matches(expected: List<Int>): Boolean = condition == expected
    }
}

data class Summary(
    val empty: Int = 0,
    val filled: Int = 0,
    val pending: Int = 0,
) {
    companion object {
        fun from(line: String): Summary =
            line.fold(Summary()) { acc, next ->
                when (next) {
                    '.' -> acc.copy(empty = acc.empty + 1)
                    '#' -> acc.copy(filled = acc.filled + 1)
                    else -> acc.copy(pending = acc.pending + 1)
                }
            }
    }
}


