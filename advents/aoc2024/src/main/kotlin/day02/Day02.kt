package com.gilpereda.aoc2024.day02

import kotlin.math.abs

fun firstTask(input: Sequence<String>): String = input.count { parseLine(it).isSafe() }.toString()

private fun parseLine(line: String): List<Int> =
    line
        .split(" ")
        .map { it.toInt() }

fun secondTask(input: Sequence<String>): String = input.count { parseLine(it).isSafe2() }.toString()

private fun List<Int>.isSafe(): Boolean {
    val list =
        windowed(2, 1)
            .map { (one, two) -> one - two }
    return (list.all { it > 0 } || list.all { it < 0 }) && list.all { abs(it) in 1..3 }
}

private fun List<Int>.isSafe2(): Boolean = isSafe() || List(size) { i -> filterIndexed { index, _ -> index != i } }.any { it.isSafe() }
