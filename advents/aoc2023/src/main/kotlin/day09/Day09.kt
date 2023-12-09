package com.gilpereda.aoc2022.day09

fun firstTask(input: Sequence<String>): String =
    input.map { it.split(" ")
        .map(String::toLong) }
        .map { prediction(it) }
        .sum().toString()

fun secondTask(input: Sequence<String>): String =
    input.map { it.split(" ")
        .map(String::toLong) }
        .map { prediction2(it) }
        .sum().toString()

fun prediction(measures: List<Long>): Long {
    fun go(list: List<Long>, acc: List<Long>): List<Long> =
        if (list.all { it == 0L }) {
            acc + 0
        } else {
            go(calculateDifferences(list), acc + list.last())
        }
    return go(measures, listOf()).sum()
}

fun prediction2(measures: List<Long>): Long {
    fun go(list: List<Long>, acc: List<Long>): List<Long> =
        if (list.all { it == 0L }) {
            acc + 0
        } else {
            go(calculateDifferences(list), acc + list.first())
        }
    return go(measures, listOf())
        .reversed()
        .fold(0) { a, b -> b - a}
}

fun calculateDifferences(measures: List<Long>): List<Long> =
    measures.windowed(2, 1).map { (one, other) -> other - one }