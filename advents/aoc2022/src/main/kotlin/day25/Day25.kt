package com.gilpereda.aoc2022.day25

import kotlin.math.pow

fun firstTask(input: Sequence<String>): String =
    input.map { it.snafuToDecimal }.sum().decimalToSnafu

fun secondTask(input: Sequence<String>): String = TODO()

val Long.decimalToSnafu: String
    get() {
        val base5 = base10toBase5
        val firstCandidate = (List(base5.length + 1) { '2' }.joinToString("").base5ToBase10 + this).base10toBase5

        return if (base5.length < firstCandidate.length) {
            firstCandidate.base5ToSnafu
        } else {
            (List(base5.length) { '2' }.joinToString("").base5ToBase10 + this).base10toBase5
        }
    }

val String.base5ToSnafu: String
    get() = map {
        when (it) {
            '0' -> '='
            '1' -> '-'
            '2' -> '0'
            '3' -> '1'
            '4' -> '2'
            else -> IllegalArgumentException("Digit is not base 5: $it")
        }
    }.joinToString("").removePrefix("0")


infix fun Long.quotientAndRemainder(other: Long): Pair<Long, Long> =
    Pair(
        this / other,
        this % other,
    )

val String.snafuToDecimal: Long
    get() = fold("") { acc, next ->
        when (next) {
            '=' -> acc + "0"
            '-' -> acc + "1"
            '0' -> acc + "2"
            '1' -> acc + "3"
            '2' -> acc + "4"
            else -> throw IllegalArgumentException()
        }
    }.baseSnafuToBase10

val String.baseSnafuToBase10: Long
    get() = reversed().foldIndexed(0) { i, acc, next ->
        acc + (5.0.pow(i) * (next.code - 50)).toLong()
    }

val String.base5ToBase10: Long
    get() = reversed().foldIndexed(0L) { i, acc, next ->
        acc + (5.0.pow(i) * (next.code - 48)).toLong()
    }

val Long.base10toBase5: String
    get() {
        tailrec fun go(rest: Long, acc: String): String {
            val (quotient, remainder) = rest quotientAndRemainder 5L
            return when (quotient) {
                0L -> "$remainder$acc"
                else -> go(quotient, "$remainder$acc")
            }
        }

        return go(this, "")
    }