package com.gilpereda.aoc2022.day01


fun elvesFood01(input: Sequence<String>): String =
    input.asElves.maxOfOrNull(Elf::totalCals).toString()

fun elvesFood02(input: Sequence<String>): String =
    input.asElves.sortedByDescending { it.totalCals }.take(3).fold(0) { acc, elf -> acc + elf.totalCals}.toString()


private val Sequence<String>.asElves: List<Elf>
    get() = fold(emptyList<Elf>()) { acc, item ->
    if (item.isNotBlank()) {
        acc.addToLast(item)
    } else {
        acc + Elf(listOf())
    }
}
data class Elf(val items: List<Int>) {
    val totalCals = items.sum()
}

fun List<Elf>.addToLast(item: String): List<Elf> {
    return lastOrNull()
        ?.let { elf ->
            val lastElf = elf.copy(elf.items + item.toInt())
            dropLast(1) + lastElf
        }
        ?: listOf(Elf(listOf(item.toInt())))
}
