package com.gilpereda.aoc2022.day10

fun firstTask(input: Sequence<String>): String {
    val result = input.opsResult()
        .mapIndexed { index, i ->  i * index }
        .filterIndexed { index, _ -> index in listOf(20, 60, 100, 140, 180, 220) }
        .sum()

    return result.toString()
}


fun secondTask(input: Sequence<String>): String {
    val pixels = input.opsResult().drop(1).mapIndexed { index, value ->
        val pixel = if ((index % 40) in value -1 .. value + 1) '#' else '.'
        Triple(pixel, index, value)
    }.toList()
    val result = pixels.map { it.first }.chunked(40).joinToString("\n") { it.joinToString("") }
    println(result)
    return result
}

private fun Sequence<String>.opsResult(): Sequence<Int> =
    map(::parseLine)
        .fold(State(1, sequenceOf(1, 1))) { acc, op -> op.exec(acc) }.seq

private fun parseLine(line: String): Op =
    if (line == "noop") Noop else line.split(" ").let { (_, value) -> AddX(value.toInt()) }


data class State(
    val last: Int,
    val seq: Sequence<Int>,
)

sealed interface Op {
    fun exec(state: State): State
}

object Noop : Op {
    override fun exec(state: State): State = state.copy(seq = state.seq + state.last)
}

data class AddX(
    val x: Int
) : Op {
    override fun exec(state: State): State {
        val newValue = state.last + x
        return State(newValue, seq = state.seq + sequenceOf(state.last, newValue))
    }
}

