package com.gilpereda.aoc2024.day11

typealias Cache = MutableMap<Long, Game.Element>

fun firstTask(input: Sequence<String>): String = task(input, 25)

fun secondTask(input: Sequence<String>): String = task(input, 75)

private fun task(
    input: Sequence<String>,
    repetitions: Int,
): String =
    Game(input.first().split(" ").map { it.toLong() })
        .elementsAfter(repetitions)
        .toString()

class Game(
    private val firstGen: List<Long>,
) {
    private val cache: Cache = mutableMapOf()

    fun elementsAfter(generations: Int): Long =
        firstGen.sumOf { value ->
            cache.computeIfAbsent(value) { Element(it) }.childrenForGeneration(generations)
        }

    inner class Element(
        value: Long,
    ) {
        private val children: List<Element> by lazy {
            when {
                value == 0L -> listOf(cache.computeIfAbsent(1) { Element(it) })
                "$value".length % 2 == 0 -> {
                    val center = ("$value".length / 2)
                    val first = "$value".substring(0..<center).toLong()
                    val second = "$value".substring(center..<"$value".length).toLong()
                    listOf(
                        cache.computeIfAbsent(first) { Element(it) },
                        cache.computeIfAbsent(second) { Element(it) },
                    )
                }
                else -> listOf(cache.computeIfAbsent(value * 2024) { Element(it) })
            }
        }

        private val generationToChildren: MutableMap<Int, Long> = mutableMapOf()

        fun childrenForGeneration(generation: Int): Long =
            if (generation == 0) {
                1
            } else {
                when (val cached = generationToChildren[generation]) {
                    null ->
                        children
                            .sumOf { it.childrenForGeneration(generation - 1) }
                            .also { generationToChildren[generation] = it }
                    else -> cached
                }
            }
    }
}
