package com.gilpereda.aoc2022.day04

fun firstTask(input: Sequence<String>): String =
    input.map(::toSections)
        .filter(::completelyOverlap)
        .count().toString()

fun secondTask(input: Sequence<String>): String =
    input.map(::toSections)
        .filter(::overlap)
        .count().toString()

private fun overlap(sections: Pair<List<Int>, List<Int>>): Boolean =
    with (sections) {
        first.any { it in second } || second.all {it in first }
    }

private fun toSections(line: String): Pair<List<Int>, List<Int>> {
    val (first, second) = line.split(",")
    return getSection(first) to getSection(second)
}

private fun getSection(sectionString: String): List<Int> {
    val (from, to) = sectionString.split("-")
    return (from.toInt() .. to.toInt()).toList()
}


private fun completelyOverlap(sections: Pair<List<Int>, List<Int>>): Boolean =
    with (sections) {
        first.all { it in second } || second.all {it in first }
    }
