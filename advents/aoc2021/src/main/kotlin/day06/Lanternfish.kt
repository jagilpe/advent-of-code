package com.gilpereda.adventsofcode.adventsofcode2021.day06


fun lanternfishes(initial: List<Int>, after: Int): Long {
    val generations = generationsMap(after)[after]!!
    return initial.map { generations[it] }.sum()
}

fun lanternfishes2(initial: List<Int>, after: Int): Int {
    tailrec fun go(fishes: Sequence<Int>, generations: Int): Int =
        if (generations > 0) {
            val nextGen = fishes.fold(Acc()) { acc, next ->
                if (next == 0) {
                    acc.copy(fishes = acc.fishes + 6, newFishes = acc.newFishes + 1)
                } else {
                    acc.copy(fishes = acc.fishes + (next - 1))
                }
            }.let { (fishes, newFishes) -> fishes + List(newFishes) { 8 } }
            go(nextGen.asSequence(), generations - 1)
        } else {
            fishes.toList().size
        }
    return initial.chunked(1).asSequence()
        .map { go(it.asSequence(), after) }
        .toList().sum()
}

fun generationsMap(size: Int): Map<Int, Array<Long>> =
    (1..size).fold(mapOf(0 to Array(9) { 1 })) { acc, gen ->
        val next = (0..9).map { fish ->
            val previous = acc[gen - 1]!!
            when (fish) {
                0 -> previous[6] + previous[8]
                else -> previous[fish - 1]
            }
        }.toTypedArray()
        acc + mapOf(gen to next)
    }


data class Acc(val fishes: List<Int> = emptyList(), val newFishes: Int = 0)