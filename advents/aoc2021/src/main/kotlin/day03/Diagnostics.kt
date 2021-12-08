package com.gilpereda.adventsofcode.adventsofcode2021.day03

import kotlin.math.pow


fun runDiagnostics(input: Sequence<String>): String =
    input.map(::parse)
        .fold(emptyArray(), ::next)
        .result


private enum class Digit(val value: Int) {
    ONE(1),
    ZERO(0)
}

private typealias Digits = List<Digit>

private fun Digits.toLong(): Long = reversed().mapIndexed { i, digit -> (2.0.pow(i) * digit.value).toLong() }.sum()

private fun parse(line: String): List<Digit> =
    line.map { when (it) {
        '0' -> Digit.ZERO
        '1' -> Digit.ONE
        else -> throw IllegalArgumentException()
    } }

private typealias DigitsCount = Array<Int>

private val DigitsCount.result: String
    get() {
        val digits = binary(this)
        val epsilon = digits.toLong()
        val gamma = inverse(digits).toLong()
        return "${gamma * epsilon}"
    }

private fun binary(digitsCount: DigitsCount): Digits =
    digitsCount.map { if (it < 0) Digit.ONE else Digit.ZERO }

private fun inverse(digits: Digits): Digits =
    digits.map { if (it == Digit.ONE) Digit.ZERO else Digit.ONE }

private fun next(count: DigitsCount, digits: Digits): DigitsCount {
    val initCount = if (count.isNotEmpty()) count else Array(digits.size) { 0 }

    return initCount.zip(digits)
        .map { (c, next) -> when (next) {
            Digit.ONE -> c + 1
            Digit.ZERO -> c - 1
        } }.toTypedArray()
}

