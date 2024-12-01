package com.gilpereda.aoc2024.day01

import kotlin.math.abs

fun firstTask(input: Sequence<String>): String {
    val (one, other) = input
        .map { it.split("  ") }
        .map { (first, second) -> first.toInt() to second.trimStart().toInt() }
        .unzip()
    return one.sorted().zip(other.sorted()).map { (one, two) -> abs(one - two) }.sum().toString()
}


fun secondTask(input: Sequence<String>): String {
    val (one, other) = input
        .map { it.split("  ") }
        .map { (first, second) -> first.toInt() to second.trimStart().toInt() }
        .unzip()
    return one.sumOf { number -> number * other.count { it == number } }.toString()
}
