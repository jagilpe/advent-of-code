package com.gilpereda.aoc2024.day03

private val MUL_REGEX = "(mul\\(\\d+,\\d+\\))".toRegex()
private val MUL_REGEX2 = "\\((\\d+),(\\d+)\\)".toRegex()
private val DO_REGEX = "((mul\\(\\d+,\\d+\\))|(do(n't)?\\(\\)))".toRegex()

fun firstTask(input: Sequence<String>): String =
    input
        .map { calculateLine(it) }
        .sum()
        .toString()

private fun calculateLine(line: String): Int =
    MUL_REGEX
        .findAll(line)
        .map { it.destructured.let { (first) -> first } }
        .map { MUL_REGEX2.find(it)!!.destructured.let { (first, second) -> first.toInt() to second.toInt() } }
        .map { (first, second) -> first * second }
        .sum()

fun secondTask(input: Sequence<String>): String {
    val line = input.reduce { one, other -> one + other }
    val result =
        DO_REGEX
            .findAll(line)
            .map { it.destructured.let { (first) -> first } }
            .fold(Result()) { acc, next ->
                when (next) {
                    "do()" -> {
                        println("enabled")
                        acc.copy(enabled = true)
                    }
                    "don't()" -> {
                        println("disabled")
                        acc.copy(enabled = false)
                    }
                    else -> if (acc.enabled) acc.copy(acc = acc.acc + next.value) else acc
                }
            }
    return result.acc.toString()
}

private val String.value: Int
    get() = MUL_REGEX2.find(this)!!.destructured.let { (first, second) -> first.toInt() * second.toInt() }

data class Result(
    val enabled: Boolean = true,
    val acc: Int = 0,
)
