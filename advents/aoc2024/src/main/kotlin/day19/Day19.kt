package com.gilpereda.aoc2024.day19

import com.gilpereda.adventofcode.commons.concurrency.SequenceCollector
import com.gilpereda.adventofcode.commons.concurrency.transformAndCollect
import java.util.concurrent.atomic.AtomicInteger

fun firstTask(input: Sequence<String>): String {
    val inputList = input.toList()
    val patterns =
        inputList
            .first()
            .split(", ")
            .map { Pattern(it) }
            .sortedByDescending { it.pattern.length }

    val counter = AtomicInteger(0)
    val collector = PatternsCollector()
    val designs =
        inputList
            .drop(2)
            .filter { it.canBeBuiltWithPatterns(patterns) }
    println("Possible designs: ${designs.size}")
    designs
        .asSequence()
        .transformAndCollect(
            transform = {
                println("processed: ${counter.getAndIncrement()}, valid: ${collector.count()}")
                Design(it).isDesignPossible(patterns)
            },
            collector = collector,
        )

    return collector.count().toString()
}

class PatternsCollector : SequenceCollector<Boolean, Int> {
    private val counter = AtomicInteger(0)

    fun count(): Int = counter.get()

    override fun emit(value: Boolean) {
        if (value) counter.incrementAndGet()
    }
}

fun secondTask(input: Sequence<String>): String = TODO()

data class Pattern(
    val pattern: String,
)

data class Design(
    val design: String,
) {
    fun isDesignPossible(patterns: List<Pattern>): Boolean {
        tailrec fun go(open: List<Combination>): Boolean =
            if (open.isEmpty()) {
                false
            } else {
                val next = open.minBy { it.score }
                if (next.finished) {
                    true
                } else {
                    go(open + next.newCombinations() - next)
                }
            }

        return go(listOf(Combination(listOf(design), emptyList(), patterns.areContainedIn(design))))
    }

    private fun List<Pattern>.areContainedIn(design: String): List<Pattern> = filter { it.pattern in design }
}

data class Combination(
    val rest: List<String>,
    val patterns: List<Pattern>,
    val restPatterns: List<Pattern>,
) {
    val score = rest.sumOf { it.length }

    private val canSucceed: Boolean
        get() = rest.all { it.canBeBuiltWithPatterns(restPatterns) }

    val finished: Boolean = rest.isEmpty() || rest.all { it.isBlank() }

    fun newCombinations(): List<Combination> =
        restPatterns
            .map { pattern ->
                val newRest = rest.flatMap { design -> design.split(pattern.pattern) }.filter { it.isNotBlank() }
                Combination(
                    newRest,
                    patterns + pattern,
                    (restPatterns - pattern).filter { newPattern -> newRest.any { newPattern.pattern in it } },
                )
            }.filter { it.canSucceed }
}

private fun String.canBeBuiltWithPatterns(patterns: List<Pattern>): Boolean =
    patterns
        .fold(this) { acc, next ->
            acc.replace(next.pattern, "")
        }.isBlank() ||
        patterns
            .sortedBy { it.pattern.length }
            .fold(this) { acc, next ->
                acc.replace(next.pattern, "")
            }.isBlank()
