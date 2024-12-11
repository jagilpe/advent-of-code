package com.gilpereda.aoc2024.day11

fun firstTask(input: Sequence<String>): String = task(input, 25)

private fun List<Long>.nextGen(): List<Long> = flatMap { it.nextGen() }

fun secondTask(input: Sequence<String>): String = task(input, 75)

private fun task(
    input: Sequence<String>,
    repetitions: Int,
): String {
    val firstGen = input.first().split(" ").map { it.toLong() }

    return generateSequence(1 to firstGen) { (iter: Int, gen: List<Long>) ->
        println("Iteration: $iter")
        (iter + 1) to gen.nextGen()
    }.take(repetitions + 1)
        .last()
        .second
        .size
        .toString()
}

private fun Long.nextGen(): List<Long> =
    when {
        this == 0L -> listOf(1L)
        ("$this".length) % 2 == 0 ->
            "$this".let {
                val center = (it.length / 2)
                val first = it.substring(0..<center).toLongOrNull()
                val second = it.substring(center..<it.length).toLongOrNull()
                listOfNotNull(first, second)
            }
        else -> listOf(this * 2024L)
    }
