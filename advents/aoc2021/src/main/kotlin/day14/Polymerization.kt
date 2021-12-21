package com.gilpereda.adventsofcode.adventsofcode2021.day14

fun polymerization1(input: Sequence<String>): String = polymerization(input, 5)

fun polymerization2(input: Sequence<String>): String = polymerization(input, 20)

fun polymerization(input: Sequence<String>, steps: Int): String {
    val (initial, dict) = parseInput(input)
    val translateMap = calculateMap(dict, steps)

    val firstChar = initial.first()
    val distrib = initial.windowed(2).asSequence()
        .flatMap {
            val first = it.first()
            (first + translateMap[it]!!.chain).windowed(2).asSequence()
        }
        .flatMap { translateMap[it]!!.distribution.asSequence() }
        .fold(mapOf<Char, Long>()) { acc, (char, count) ->
            val previous = acc[char] ?: 0
            acc + mapOf(char to previous + count)
        }.let { it + mapOf(firstChar to it[firstChar]!! + 1) }

    val min = distrib.minOf { (_, value) -> value }
    val max = distrib.maxOf { (_, value) -> value }
    return (max - min).toString()
}

fun calculateMap(dict: Map<String, Char>, steps: Int): Map<String, Polymer> =
    dict.mapValues { (key, _) ->
        val elements = elements(sequenceOf(key), steps, dict)
        val distribution = elements.groupBy { it }.mapValues { (_, values) -> values.size.toLong() }
        val chain = elements.joinToString("")
        Polymer(chain, distribution)
    }

data class Polymer(val chain: String, val distribution: Map<Char, Long>)

tailrec fun elements(input: Sequence<String>, steps: Int, dict: Map<String, Char>): Sequence<Char> =
    if (steps <= 0) input.map { it.last() }
    else elements(input.flatMap { item -> next(item, dict) }, steps - 1, dict)


fun next(item: String, dict: Map<String, Char>): Sequence<String> {
    val next = dict[item]!!
    val (first, second) = item.toList()
    return sequenceOf("$first$next", "$next$second")
}

fun parseInput(input: Sequence<String>): Pair<String, Map<String, Char>> =
    input.fold(Pair("", emptyMap())) { acc, next ->
        when {
            next.contains(" -> ") -> next.split(" -> ")
                .let { (key, value) -> acc.copy(second = acc.second + mapOf(key to value.first())) }
            next.isNotBlank() -> acc.copy(first = next)
            else -> acc
        }
    }

fun nextGen(current: List<Char>, dict: Map<String, Char>): List<Char> =
    current.windowed(2)
        .map { it.joinToString("") }
        .fold(listOf(current.first())) { acc, pair -> acc + listOfNotNull(dict[pair], pair.last()) }