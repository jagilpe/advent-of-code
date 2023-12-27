package com.gilpereda.aoc2022.day03

fun rucksack(input: Sequence<String>): String =
    input.map(::findWrongItem)
        .map(::priority)
        .sum().toString()

fun badges(input: Sequence<String>): String =
    input.chunked(3)
        .map {
            findCommon(it)
        }
        .map(::priority)
        .sum().toString()


private fun findWrongItem(line: String): Char {
    val firstCompartment = line.toList().slice(0 until (line.length / 2))
    val secondCompartment = line.toList().slice((line.length / 2) until line.length).toSet()

    return firstCompartment.intersect(secondCompartment.toSet()).first()
}

private fun findCommon(lines: List<String>): Char {
    val (first, second, third) = lines
    return first.toSet().intersect(second.toSet()).intersect(third.toSet()).first()
}

fun priority(item: Char): Int =
    when  {
        item in 'a'..'z' -> item.code - 96
        item in 'A' .. 'Z' -> item.code - 38
        else -> 0
    }
