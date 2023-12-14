package com.gilpereda.aoc2022.day12

import java.time.Duration
import java.time.Instant

fun firstTaskOriginal(input: Sequence<String>): String =
    input.parsed(1)
        .sumOf { it.matching() }.toString()

fun firstTask(input: Sequence<String>): String =
    input.parsed2(1)
        .sumOf { it.matches() }.toString()

fun secondTaskOriginal(input: Sequence<String>): String {
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

fun secondTask(input: Sequence<String>): String =
    input
        .parsed2(5)
        .sumOf { it.matches() }
        .toString()

fun Sequence<String>.parsed(unfold: Int): Sequence<SpringConditions> =
    map { line -> SpringConditions.forLine(line, unfold) }

data class SpringConditions(
    private val line: String,
    private val conditionsLevel: Int,
    private val previousMatches: Map<Int, Long> = mapOf()
) {
    private val originalSprings: String
    private val originalSpringsLength: Int
    private val unfoldedSprings: String
    private val levelToExpected: Map<Int, List<Int>>
    private val matchingStarts: List<List<Int>>
    private val unfoldedExpected: List<Int>

//    private val originalExpected: List<Int>
//    private val originalExpectedSize: Int

    init {
        val (springs, expectedStr) = line.split(" ")
        this.originalSprings = springs
        this.originalSpringsLength = springs.length
        val expected = expectedStr.split(",").map { it.toInt() }
        this.levelToExpected = List(conditionsLevel) { it + 1 }
            .associateWith { List(it) { expected }.flatten() }

        this.unfoldedSprings = List(conditionsLevel) { springs }.joinToString("?")

        this.unfoldedExpected = levelToExpected[conditionsLevel]!!
        this.matchingStarts = List(unfoldedExpected.size) { i -> unfoldedExpected.take(i + 1) }

//        this.originalSprings = originalSprings
//        this.originalSpringsLength = originalSprings.length
//        this.originalExpected = originalExpected.split(",").map { it.toInt() }
//        this.originalExpectedSize = this.originalExpected.size
//        val (springs, expectedStr) = unfold(line, level).split(" ")
//        this.unfoldedSprings = springs
//        this.unfoldedExpected = expectedStr.split(",").map { it.toInt() }
    }

    private val expectedCount = unfoldedExpected.size
    private val expectedSprings = unfoldedExpected.sum()
//
//    private val starts = List(unfoldedExpected.size) { i -> unfoldedExpected.take(i + 1) }

//    private val previousCount: Long by lazy {
//        SpringConditions(line, conditionsLevel - 1).matching()
//    }

    fun nextLevel(): SpringConditions {
        val previousMatches = this.previousMatches + mapOf(conditionsLevel to matching())

        return SpringConditions(line, conditionsLevel + 1, previousMatches)
    }

    fun matching(): Long =
        sequence {
            findMatching(Conditions(unfoldedSprings))
        }.fold(0L) { acc, next -> acc + next }
//            .also { println("Matching: $it for unfold: $unfold") }

    private suspend fun SequenceScope<Long>.findMatching(base: Conditions) {
        base.next().forEach { next ->
            val matches = next.matches()
            if (matches != null) {
                yield(matches)
            } else if (next.canMatch()) {
                findMatching(next)
            }
        }
    }

    inner class Conditions(private val conditions: String) {
        private val summary = Summary.from(conditions)

        private val rawConditions: List<String> = conditions.split(".").filter { it.isNotBlank() }

        private val knownConditions: List<Int> by lazy {
            rawConditions.takeWhile { !it.contains("?") }.map { it.count() }
        }

        fun matches(): Long? =
            generateSequence(1) { it + 1 }
                .take(conditionsLevel)
                .firstNotNullOfOrNull {
                    if (it == conditionsLevel) fullMatches()
                    else matchesForPreviousLevel(it)
                }

        private fun fullMatches(): Long? =
            if (!conditions.contains('?')) {
                if (knownConditions == unfoldedExpected) 1 else 0
            } else {
                null
            }

        private fun matchesForPreviousLevel(level: Int): Long? =
            if (isFinishedForLevel(level)) {
                previousMatches[conditionsLevel - level]!!
            } else {
                null
            }

        private fun isFinishedForLevel(level: Int): Boolean {
            val limit = originalSpringsLength * level + level - 1
            return !conditions.substring(0 until limit).contains("?") &&
                    conditions[limit] == '.' &&
                    knownConditions == levelToExpected[level]!!
        }

        fun canMatch(): Boolean {
            return doesNotExceedExpectedSprings() &&
                    doesNotExceedExpectedCount() &&
                    canReachTheExpectedCount() &&
                    knownConditionsStartMatches()
        }

        private fun knownConditionsStartMatches(): Boolean =
            knownConditions.isEmpty() || knownConditions == matchingStarts[knownConditions.size - 1]

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
    }

    companion object {
        fun forLine(line: String, fold: Int): SpringConditions =
            List(fold - 1) { it + 1 }
                .fold(SpringConditions(line, 1)) { springConditions, _ -> springConditions.nextLevel() }
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

typealias Index = Int
typealias RemainingGroups = Int
typealias Cache = MutableMap<Index, MutableMap<RemainingGroups, Long>>


fun Sequence<String>.parsed2(unfold: Int): Sequence<Springs> =
    map { line -> Springs.forLine(line, unfold) }


class Springs(
    private val springs: String,
    private val groups: List<Int>,
) {
    private val cache: Cache = mutableMapOf()
    private val springsLength = springs.length
    private val notEmptyIndices = springs.mapIndexedNotNull { index, c -> if (c == '.') null else index }

    fun matches(): Long =
        solve(groups, 0)

    private fun solve(groups: List<Int>, index: Int): Long {
        if (groups.isEmpty()) {
            return if (index < springsLength && springs.substring(index until springsLength).contains('#')) 0L else 1L
        }
        val current = nextNotEmpty(index)
        if (current >= springsLength) {
            return 0L
        }

        val cached = cache[current]?.get(groups.size)
        if (cached != null) {
            return cached
        }

        val resultOne = tryFillSprings(groups, current)

        val resultTwo = if (springs[current] == '?') {
            solve(groups, current + 1)
        } else {
            0L
        }

        val result = resultOne + resultTwo
        cache.computeIfAbsent(current) { mutableMapOf() }[groups.size] = result

        return result
    }

    private fun tryFillSprings(groups: List<Int>, index: Int): Long {
        val group = groups.first()
        return if (springs.canContainGroupFromIndex(group, index)) {
            solve(groups.drop(1), index + group + 1)
        } else {
            0L
        }
    }

    private fun String.canContainGroupFromIndex(group: Int, index: Int): Boolean =
        index + group <= length &&
                !substring(index until index + group).contains('.') &&
                (index + group == length || get(index + group) != '#')

    private fun nextNotEmpty(index: Int): Int =
        notEmptyIndices.firstOrNull { it >= index } ?: (springsLength + 1)

    companion object {
        fun forLine(line: String, fold: Int): Springs {
            val (springs, groupsStr) = line.split(" ")
            val unfoldedSpring = List(fold) { springs }.joinToString("?")
            val groups = groupsStr.split(",").map { it.toInt() }
            val unfoldedGroups = List(fold) { groups }.flatten()

            return Springs(unfoldedSpring, unfoldedGroups)
        }
    }
}



