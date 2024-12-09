package com.gilpereda.aoc2024.day07

import com.gilpereda.adventofcode.commons.concurrency.LongSumSequenceCollector
import com.gilpereda.adventofcode.commons.concurrency.transformAndCollect

fun firstTask(input: Sequence<String>): String = task(input, listOf(Operation.Sum, Operation.Multiply))

fun secondTask(input: Sequence<String>): String = task(input, listOf(Operation.Sum, Operation.Multiply, Operation.Concat))

private fun task(
    input: Sequence<String>,
    operations: List<Operation>,
): String {
    val collector = LongSumSequenceCollector.instance()
    input.transformAndCollect({ parsed(it).calculate(operations) }, collector)
    return collector.get().toString()
}

fun parsed(input: String): Test =
    input.split(": ").let { (result, operators) ->
        Test(
            result = result.toLong(),
            operators = operators.split(" ").mapNotNull { it.toLongOrNull() },
        )
    }

data class Test(
    val result: Long,
    val operators: List<Long>,
) {
    private fun canMatch(operations: List<Operation>): Boolean {
        tailrec fun go(acc: List<Partial>): Boolean =
            if (acc.isEmpty()) {
                false
            } else {
                val (value, rest) = acc.first()
                if (rest.isEmpty()) {
                    if (value == result) {
                        true
                    } else {
                        go(acc.drop(1))
                    }
                } else {
                    val next = rest.first()
                    val nextOperators = rest.drop(1)
                    val nextPartials =
                        operations
                            .map { it.calculate(value, next) }
                            .filter { it <= result }
                            .map {
                                Partial(it, nextOperators)
                            }
                    go(acc.drop(1) + nextPartials)
                }
            }
        return go(listOf(Partial(operators.first(), operators.drop(1))))
    }

    fun calculate(operations: List<Operation>): Long = if (canMatch(operations)) result else 0
}

sealed interface Operation {
    fun calculate(
        one: Long,
        other: Long,
    ): Long

    data object Sum : Operation {
        override fun calculate(
            one: Long,
            other: Long,
        ): Long = one + other
    }

    data object Multiply : Operation {
        override fun calculate(
            one: Long,
            other: Long,
        ): Long = one * other
    }

    data object Concat : Operation {
        override fun calculate(
            one: Long,
            other: Long,
        ): Long = "$one$other".toLong()
    }
}

data class Partial(
    val value: Long,
    val rest: List<Long>,
)
