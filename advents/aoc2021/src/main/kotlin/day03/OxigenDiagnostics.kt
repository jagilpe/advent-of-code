package com.gilpereda.adventsofcode.adventsofcode2021.day03

import kotlin.math.pow

fun runOxygenDiagnostics(input: Sequence<String>): String {
    val inputList = input.toList()
    val oxygen = go(inputList, 0, '1') { first, second -> first.size >= second.size }.first()
    val co2 = go(inputList, 0, '0') { first, second -> first.size <= second.size }.first()
    return "${oxygen.binaryToLong() * co2.binaryToLong()}"
}

fun String.binaryToLong(): Long {
    return reversed().mapIndexed { i, c -> if (c == '1') 2.0.pow(i).toLong() else 0 }.sum()
}

tailrec fun go(input: List<String>, index: Int, char: Char, filterLists: (List<*>, List<*>) -> Boolean): List<String> {
    val (charList, notCharList) = input.partition { it[index] == char }
    val result = if (filterLists(charList, notCharList)) charList else notCharList
    return if (result.count() == 1) result else go(result, index + 1, char, filterLists)
}
