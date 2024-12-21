package com.gilpereda.aoc2024.day19

fun firstTask(input: Sequence<String>): String = resolve(input).count { it != 0L }.toString()

fun secondTask(input: Sequence<String>): String = resolve(input).sumOf { it }.toString()

private fun resolve(input: Sequence<String>): List<Long> {
    val inputList = input.toList()
    val patterns =
        inputList
            .first()
            .split(", ")
            .map { it }
            .sortedByDescending { it.length }

    var counter = 0
    return inputList
        .drop(2)
        .map {
            println(counter++)
            Design(it, patterns).solve()
        }
}

class Design(
    private val design: String,
    private val patterns: List<String>,
) {
    private val cache: MutableMap<Int, Long> = mutableMapOf()

    fun solve(): Long {
        val findPatterns = findPatterns(design.length - 1)
        return findPatterns
    }

    private fun findPatterns(index: Int): Long {
        val cached = cache[index]
        if (cached != null) {
            println("hit cache in index $index")
            return cached
        }

        val patterns = tryFillPatterns(index)

        cache[index] = patterns
        return patterns
    }

    private fun tryFillPatterns(index: Int): Long {
        val next = design.substring(0..index)
        return patterns
            .filter { next.endsWith(it) }
            .sortedBy { it.length }
            .sumOf { pattern ->
                val nextIndex = index - pattern.length
                when {
                    nextIndex == -1 -> 1
                    nextIndex >= 0 -> findPatterns(index - pattern.length)
                    else -> 0
                }
            }
    }
}
