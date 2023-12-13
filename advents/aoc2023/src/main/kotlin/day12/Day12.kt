package com.gilpereda.aoc2022.day12

fun firstTask(input: Sequence<String>): String =
    input.parsed(1)
        .sumOf { it.matching() }.toString()

fun secondTask(input: Sequence<String>): String =
    input
        .parsed(5)
        .sumOf { it.matching().also { println("matching: $it") } }
        .toString()


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
    map { line -> SpringConditions(line, unfold) }

data class SpringConditions(
    val line: String,
    val unfold: Int,
) {
    private val springs: String
    private val expected: List<Int>

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
        this.springs = springs
        this.expected = expectedStr.split(",").map { it.toInt() }
    }

    private val maxExpected = expected.max()
    private val expectedCount = expected.size
    private val expectedSprings = expected.sum()

    private val starts = List(expected.size) { i -> expected.take(i + 1) }

    private val previousCount: Long by lazy {
        SpringConditions(line, unfold - 1).matching()
    }

    fun matching(): Long =
        sequence {
            findMatching(
                Conditions(springs)
            )
        }.fold(0L) { acc, next -> acc + next }
//            .also { println("Matching: $it for unfold: $unfold") }

    private suspend fun SequenceScope<Long>.findMatching(base: Conditions) {
        base.next().forEach { yieldNext(it) }
    }

    private suspend fun SequenceScope<Long>.yieldNext(next: Conditions) {
        if (next.notFinished) {
            if (next.isPartlyFinished) {
                yield(previousCount)
            } else if (next.canMatch()) {
                findMatching(next)
            }
        } else {
            if (next.matches(expected)) yield(1L)
        }
    }

    private fun Conditions.canMatch(): Boolean {
        return notFullConditions.noneNotExceedMaxExpected(maxExpected) &&
                rawConditions.doesNotExceedExpectedCount(expectedCount) &&
                rawConditions.reachesTheExpectedCount(expectedSprings) &&
                (knownConditions.isEmpty() || knownConditions.startMatches())
    }

    private fun List<Int>.startMatches(): Boolean =
        this == starts[size - 1]

    private fun List<Int>.noneNotExceedMaxExpected(maxExpected: Int): Boolean =
        none { it > maxExpected }

    private fun List<String>.reachesTheExpectedCount(expectedCount: Int): Boolean =
        sumOf { it.count() } >= expectedCount

    fun List<Int>.doesNotHaveTooManyMax(maxExpected: Int, maxCount: Int): Boolean =
        count { it == maxExpected } <= maxCount

    private fun List<String>.doesNotExceedExpectedCount(expectedCount: Int): Boolean =
        count { it.contains('#') } <= expectedCount

    inner class Conditions(val conditions: String, ) {
        val rawConditions: List<String> by lazy { conditions.split(".").filter { it.isNotBlank() } }
        val notFullConditions: List<Int> by lazy {
            rawConditions.flatMap { it.split("?") }.filter { it.isNotBlank() }.map { it.count() }
        }
        val knownConditions: List<Int> by lazy { rawConditions.takeWhile { !it.contains("?") }.map { it.count() } }

        val condition: List<Int> by lazy {
            conditions.split(".")
                .filter { it.isNotBlank() }
                .map { it.count() }
        }

        val notFinished: Boolean by lazy { conditions.contains("?") }

        val isPartlyFinished: Boolean by lazy {
            val conditionsLength = conditions.length
    //        false
            unfold > 1 &&
                    !conditions.substring(0..originalSpringsLength - 1).contains("?") &&
                    conditions[originalSpringsLength] == '.' &&
                    knownConditions == originalExpected
        }

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


