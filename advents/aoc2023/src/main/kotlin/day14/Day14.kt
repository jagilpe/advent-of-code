package com.gilpereda.aoc2022.day14

import com.gilpereda.aoc2022.utils.*

fun firstTask(input: Sequence<String>): String =
    input.toList().parsed()
        .tilt(Orientation.NORTH)
        .load.toString()

typealias Iteration = Triple<Int, Surface, Triple<Int, Int, Index>?>

private const val ITERATIONS = 1000000000

fun secondTask(input: Sequence<String>): String {
    val cycleDetector = CycleDetector(ITERATIONS)
    val seed: Iteration = Triple(1, input.toList().parsed(), null)
    return generateSequence(seed) { (iteration, surface, _) ->
        val next = surface.cycle()
        Triple(iteration + 1, next, cycleDetector.record(next, iteration))
    }
        .first { (_, _, cycle) -> cycle != null }
        .third!!.third.toString()
}

data class Surface(
    val map: TypedTwoDimensionalMap<Cell>
) {
    val key: String = map.values().joinToString("")
    val load: Int
        get() = map.valuesIndexed()
            .sumOf { (point, cell) ->
                if (cell is Cell.RoundRock)
                    map.height - point.y
                else 0
            }

    fun cycle(): Surface =
        tilt(Orientation.NORTH)
            .tilt(Orientation.WEST)
            .tilt(Orientation.SOUTH)
            .tilt(Orientation.EAST)

    fun tilt(orientation: Orientation): Surface =
        Surface(
            map.transform(orientation)
                .mapLines { line: List<Cell> -> tiltLine(line) }.transformBack(orientation)
        )

    private fun tiltLine(line: List<Cell>): List<Cell> =
        generateSequence(line) {
            val firstEmpty: Int? = null
            val seed = line.mapIndexed { x, cell -> x to cell }.toMap()
            it.foldIndexed(Pair(firstEmpty, seed)) { x, (first, acc), cell ->
                when {
                    x == 0 -> Pair(if (cell is Cell.Empty) 0 else null, acc)
                    first == null -> when (cell) {
                        is Cell.Empty -> Pair(x, acc)
                        else -> Pair(null, acc)
                    }

                    else -> when (cell) {
                        is Cell.Empty -> Pair(first, acc)
                        is Cell.RoundRock -> Pair(
                            first + 1, acc + mapOf(
                                first to Cell.RoundRock,
                                x to Cell.Empty
                            )
                        )

                        is Cell.CubeRock -> Pair(null, acc)
                    }
                }
            }.second.values.toList()
        }
            .first { !canTilt(it) }

    private fun canTilt(line: List<Cell>): Boolean =
        line.windowed(2, 1).any { it == listOf(Cell.Empty, Cell.RoundRock) }
}

class CycleDetector(
    private val targetIteration: Int,
) {
    private val hashToIteration = mutableMapOf<String, Int>()
    private val iterationToLoad = mutableMapOf<Int, Index>()

    fun record(surface: Surface, iteration: Int): Triple<Int, Int, Index>? {
        iterationToLoad[iteration] = surface.load
        val key = surface.key
        val result = hashToIteration[key]
            ?.let { first ->
                Triple(first, iteration, getTargetLoad(first, iteration))
            }
        hashToIteration[key] = iteration
        return result
    }

    private fun getTargetLoad(first: Int, second: Int): Index {
        val target = (targetIteration - first) % (second - first) + first
        return iterationToLoad[target]!!
    }

}

fun List<String>.parsed(): Surface =
    Surface(
        parseToMap { c ->
            when (c) {
                'O' -> Cell.RoundRock
                '#' -> Cell.CubeRock
                else -> Cell.Empty
            }
        })

sealed interface Cell {
    data object Empty : Cell {
        override fun toString(): String = "."
    }

    data object RoundRock : Cell {
        override fun toString(): String = "O"
    }

    data object CubeRock : Cell {
        override fun toString(): String = "#"
    }
}