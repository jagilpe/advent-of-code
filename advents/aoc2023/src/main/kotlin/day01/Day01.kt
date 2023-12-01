package com.gilpereda.aoc2022.day01

import java.lang.Exception

fun firstTask(input: Sequence<String>): String =
    input.map(::findNumber).sum().toString()

fun secondTask(input: Sequence<String>): String =
    input.map { line ->
        "${line.firstDigit2}${line.lastDigit2}".toInt()
    }.sum().toString()


fun findNumber(line: String): Int =
    "${line.firstDigit}${line.lastDigit}".toInt()

val String.firstDigit: Char
    get() = first { it.isDigit() }

val String.lastDigit: Char
    get() = reversed().first { it.isDigit() }

val String.firstDigit2: Int
    get() {
        fun go(rest: String): Int =
            when (val first = wordToDigitStart(rest)) {
                null -> go(rest.drop(1))
                else -> first
            }
        return go(this)
    }

val String.lastDigit2: Int
    get() {
        fun go(rest: String): Int =
            try {
                when (val last = wordToDigitEnd(rest)) {
                    null -> go(rest.dropLast(1))
                    else -> last
                }
            } catch (ex: Exception) {
                println(this)
                println(rest)
                throw ex
            }
        return go(this)
    }

fun wordToDigitStart(line: String): Int? {
    val lower = line.lowercase()
    return when {
        lower.first().isDigit() -> lower.first().digitToInt()
        lower.startsWith("zero") -> 0
        lower.startsWith("one") -> 1
        lower.startsWith("two") -> 2
        lower.startsWith("three") -> 3
        lower.startsWith("four") -> 4
        lower.startsWith("five") -> 5
        lower.startsWith("six") -> 6
        lower.startsWith("seven") -> 7
        lower.startsWith("eight") -> 8
        lower.startsWith("nine") -> 9
        else -> null
    }
}

fun wordToDigitEnd(line: String): Int? {
    val lower = line.lowercase()
    return when {
        lower.last().isDigit() -> lower.last().digitToInt()
        lower.endsWith("zero") -> 0
        lower.endsWith("one") -> 1
        lower.endsWith("two") -> 2
        lower.endsWith("three") -> 3
        lower.endsWith("four") -> 4
        lower.endsWith("five") -> 5
        lower.endsWith("six") -> 6
        lower.endsWith("seven") -> 7
        lower.endsWith("eight") -> 8
        lower.endsWith("nine") -> 9
        else -> null
    }
}