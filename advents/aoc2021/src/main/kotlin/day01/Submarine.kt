package com.gilpereda.adventsofcode.adventsofcode2021.day01

fun countIncreases(input: Sequence<String>): String =
    input
        .filter { it != "" }
        .map { it.toInt() }.increaseCount().toString()

fun countWindowed(input: Sequence<String>): String =
    input
        .filter { it != "" }
        .map { it.toInt() }
        .windowed(3)
        .map { it.sum() }.increaseCount().toString()

private fun Sequence<Int>.increaseCount(): Int =
    fold(Accumulator()) { acc, next ->
        when {
            acc.previous != null && acc.previous < next -> Accumulator(previous = next, count = acc.count + 1)
            else -> acc.copy(previous = next)
        }
    }.count

private data class Accumulator(
    val previous: Int? = null,
    val count: Int = 0,
) {
    val result: String = count.toString()
}