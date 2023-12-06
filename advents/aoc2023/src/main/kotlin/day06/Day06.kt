package com.gilpereda.aoc2022.day06

fun firstTask(input: List<Race>): Int {
    return input.map { it.winningCount }.fold(1) { a, b -> a * b}
}

fun secondTask(input: Sequence<String>): String =
    TODO()

data class Race(
    val time: Long,
    val distance: Long,
) {
    val winningCount: Int
        get() = (0..time).count { wins(it) }

    private fun wins(initial: Long): Boolean {
        val restTime = time - initial
        val raceDistance = (restTime * initial)
        return raceDistance > distance
    }

}