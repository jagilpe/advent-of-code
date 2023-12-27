package com.gilpereda.aoc2022.day06

fun firstTask(input: Sequence<String>): String =
    findStart(4, 4, input.first()).toString()

fun secondTask( input: Sequence<String>): String =
    findStart(14, 14, input.first()).toString()


private fun findStart(window: Int, add: Int, line: String): Int =
    line.windowed(window, 1, true)
        .mapIndexed { i, s -> i to s }
        .find { (_, s) -> s.toSet().size == window }!!.first + add