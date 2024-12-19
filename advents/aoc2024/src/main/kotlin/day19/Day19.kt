package com.gilpereda.aoc2024.day19

/**
 * 363 is not the answer
 * 366 is not the answer (forward and backwards)
 * 350 is not
 * 305 is not right
 * 283 is not right
 * 369 is not right
 */
fun firstTask(input: Sequence<String>): String {
    val inputList = input.toList()
    val patterns =
        inputList
            .first()
            .split(", ")
            .map { it }
            .sortedByDescending { it.length }

    val designs =
        inputList
            .drop(2)

    println("Possible designs: ${designs.size}")
    val result = Game(designs, patterns).solve()

    return result.count { it.value != null }.toString()
}

class Game(
    private val designs: List<String>,
    private val patterns: List<String>,
) {
    private var sortedPatterns = patterns
    private val patternToDecomposition =
        patterns
            .associateWith { pattern -> listOf(pattern) }
            .toMutableMap()

    fun solve(): Map<String, List<String>?> =
        designs.associateWith { design ->
            val decomposition = decomposition(design)
            if (decomposition.finished) {
                decomposition.acc
            } else if (decomposition.failed) {
                null
            } else {
                findAlternative(decomposition.current, decomposition.rest)
            }
        }

    private fun decomposition(design: String): Decomposition {
        tailrec fun go(decomposition: Decomposition): Decomposition =
            if (decomposition.finished) {
                decomposition
            } else {
                val next = findMatching(decomposition.rest)
                if (next == null) {
                    decomposition
                } else {
                    val (pattern, list) = next
                    val newAcc = decomposition.acc + list
                    val newRest = decomposition.rest.removePrefix(pattern)
                    val newCurrent = decomposition.current + pattern
                    sortedPatterns = (sortedPatterns + pattern).sortedByDescending { it.length }
                    patternToDecomposition[newCurrent] = newAcc
                    go(Decomposition(newRest, newCurrent, newAcc))
                }
            }
        return go(Decomposition(design, "", emptyList()))
    }

    private fun findAlternative(
        initial: String,
        final: String,
    ): List<String>? {
        var newInitial = initial
        var newFinal = final
        do {
            newFinal = newInitial.last() + newFinal
            newInitial = newInitial.dropLast(1)

            val finalDecomposition = decomposition(newFinal)
            if (finalDecomposition.finished) {
                val initialDecomposition = decomposition(newInitial)
                if (initialDecomposition.finished) {
                    return initialDecomposition.acc + finalDecomposition.acc
                }
            }
        } while (newInitial.isNotBlank())
        return null
    }

    private fun findMatching(design: String): Pair<String, List<String>>? =
        sortedPatterns
            .firstOrNull { design.startsWith(it) }
            ?.let { p -> patternToDecomposition[p]?.let { d -> p to d } }
}

data class Decomposition(
    val rest: String,
    val current: String,
    val acc: List<String>,
) {
    val finished: Boolean = rest.isBlank()
    val failed: Boolean = current.isBlank()
}

fun secondTask(input: Sequence<String>): String = TODO()

private fun String.canBeBuiltWithPatterns(patterns: List<String>): Boolean =
    patterns
        .fold(this) { acc, next ->
            acc.replace(next, "")
        }.isBlank() ||
        patterns
            .sortedBy { it.length }
            .fold(this) { acc, next ->
                acc.replace(next, "")
            }.isBlank()
