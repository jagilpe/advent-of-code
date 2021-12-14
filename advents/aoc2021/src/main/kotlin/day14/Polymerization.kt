package com.gilpereda.adventsofcode.adventsofcode2021.day14

fun polymerization1(input: Sequence<String>): String = polymerization(input, 10)

fun polymerization2(input: Sequence<String>): String = polymerization(input, 40)

//fun polymerization(input: Sequence<String>, steps: Int): String {
//    val (initial, dict) = parseInput(input)
//    val firstChar = initial.first()
//    val distrib = initial.toList().windowed(2).asSequence()
//        .map { current -> distribution(current, steps, dict) }
//        .reduce { a, b -> (a.toList() + b.toList()).groupBy({ it.first }, { it.second }).mapValues { (_, values) -> values.sum() } }
//        .let { it +  mapOf(firstChar to it[firstChar]!! + 1)}
//
//    val min = distrib.minOf { (_, value) -> value }
//    val max = distrib.maxOf { (_, value) -> value }
//    return (max - min).toString()
//}

fun polymerization(input: Sequence<String>, steps: Int): String {
    val (initial, dict) = parseInput(input)
    val firstChar = initial.first()
    val distrib = elements(initial.toList().windowed(2).map { it.joinToString("") }.asSequence(), steps, dict)
        .fold(mapOf<Char, Long>()) { acc: Map<Char, Long>, char: Char  ->
        val count = acc[char] ?: 0
        acc + mapOf(char to count + 1)
    }.let { it + mapOf(firstChar to it[firstChar]!! + 1)}

    val min = distrib.minOf { (_, value) -> value }
    val max = distrib.maxOf { (_, value) -> value }
    return (max - min).toString()
}

tailrec fun elements(input: Sequence<String>, steps: Int, dict: Map<String, Char>): Sequence<Char> =
    if (steps <= 0) input.map { it.last() }
    else elements(input.flatMap { item -> next(item, dict)}, steps - 1, dict)


fun next(item: String, dict: Map<String, Char>): Sequence<String> {
    val next = dict[item]!!
    val (first, second) = item.toList()
    return sequenceOf("$first$next", "$next$second")
}

fun distribution(current: List<Char>, steps: Int, dict: Map<String, Char>): Map<Char, Int> =
    generateSequence(current) { next -> nextGen(next, dict) }
        .take(steps)
        .last()
        .drop(1)
        .groupBy { it }.mapValues { (_, value) -> value.size }

fun parseInput(input: Sequence<String>): Pair<String, Map<String, Char>> =
    input.fold(Pair("", emptyMap())) { acc, next ->
        when {
            next.contains(" -> ") -> next.split(" -> ").let { (key, value) -> acc.copy(second = acc.second + mapOf(key to value.first())) }
            next.isNotBlank() -> acc.copy(first = next)
            else -> acc
        }
    }

fun nextGen(current: List<Char>, dict: Map<String, Char>): List<Char> =
    current.windowed(2)
        .map { it.joinToString("")}
        .fold(listOf(current.first())) { acc, pair -> acc + listOfNotNull(dict[pair], pair.last()) }