package com.gilpereda.aoc2022.day17

import com.gilpereda.aoc2022.utils.geometry.Orientation
import com.gilpereda.aoc2022.utils.geometry.Point
import com.gilpereda.aoc2022.utils.map.IntTwoDimensionalMap
import com.gilpereda.aoc2022.utils.map.parseToIntArrayMap

fun firstTask(input: Sequence<String>): String =
    input.toList().parsed(0, 4).solve().toString()

/**
 * 945 too high
 * 943 too high
 */
fun secondTask(input: Sequence<String>): String {
    println("${Runtime.getRuntime().maxMemory()}")
    return input.toList().parsed(4, 11).solve().toString()
}

fun List<String>.parsed(minBlocks: Int, maxBlocks: Int): Game =
    Game(map = parseToIntArrayMap(), minBlocks = minBlocks, maxBlocks = maxBlocks)

class Game(
    private val map: IntTwoDimensionalMap,
    private val minBlocks: Int,
    private val maxBlocks: Int,
    private val start: Point = Point.from(0, 0),
    private val finish: Point = Point.from(map.width - 1,  map.height - 1)
) {
    private val initial = listOf(State(start, Orientation.SOUTH, 0), State(start, Orientation.EAST, 0))
    private val queue = PriorityQueue(initial)
    private val paths = StatePaths()
    private val gScore = GScores(initial)
    private val fScore = FScores(initial)

    fun solve(): Int {
        val minHeatLossPath = findMinHeatLossPath().reversed()
        println(dumpRoute(minHeatLossPath))
        return totalHeatLoss(minHeatLossPath)
    }

    private fun findMinHeatLossPath(): List<Point> {
        var i = 0

        while (queue.isNotEmpty()) {
            i += 1
            if (i % 10_000 == 0) {
                println("Queue items: ${queue.size}, paths items: ${paths.size}, gScore items: ${gScore.size}, fScore items: ${fScore.size}")
            }
            val current = queue.next()
            if (current.point == finish)
                return paths.pathTo(current).map { it.point }

            val neighbours = current.neighbours(minBlocks, maxBlocks)
            neighbours
                .filter { it.isValid }
                .forEach { neighbour ->
                    val tentativeGScore = gScore[current] + d(neighbour)
                    val currentScore = gScore[neighbour]
                    if (tentativeGScore < currentScore) {
                        paths[neighbour] = current
                        gScore[neighbour] = tentativeGScore
                        fScore.add(neighbour, tentativeGScore)
                        queue.add(neighbour)
                    }
                }
        }
        throw Exception("No path found")
    }

    private fun d(state: State): Double =
        map[state.point].toDouble()

    private fun dumpRoute(path: List<Point>): String {
        val route = path.windowed(2)
            .associate { (from, to) -> from to orientation(from, to) }
        return map.dump { point, value ->
            when (val orientation = route[point]) {
                null -> "."
                Orientation.NORTH -> "^"
                Orientation.SOUTH -> "v"
                Orientation.EAST -> ">"
                Orientation.WEST -> "<"
            }.let { "$it$value " }
        }
    }

    private fun orientation(source: Point, destination: Point): Orientation =
        when {
            source.x == destination.x -> if (source.y < destination.y) Orientation.SOUTH else Orientation.NORTH
            else -> if (source.x < destination.x) Orientation.EAST else Orientation.WEST
        }

    private fun totalHeatLoss(path: List<Point>): Int =
        path.filter { it != start }.sumOf { map[it] }

    private val State.isValid: Boolean
        get() = map.withinMap(point)

    private fun State.distance(): Double =
        point.distanceTo(finish).toDouble()

    inner class PriorityQueue(
        initial: List<State>
    ) {
        private val items: MutableSet<State> = mutableSetOf<State>()
            .apply {
                addAll(initial)
            }

        val size: Int
            get() = items.size

        fun isNotEmpty(): Boolean = items.isNotEmpty()

        fun add(state: State) {
            items.add(state)
        }

        fun next(): State =
            items.minBy { fScore[it] }
                .also { items.remove(it) }
    }

    inner class StatePaths {
        private val map = mutableMapOf<State, State>()

        val size: Int
            get() = map.size

        operator fun set(to: State, from: State): StatePaths {
            map[to] = from
            return this
        }

        fun pathTo(state: State): List<State> =
            generateSequence(state) { map[it] }.toList()

    }

    class GScores(initial: List<State>) {
        private val scores = mutableMapOf<State, Double>()
            .apply {
                initial.forEach { put(it, 0.0) }
            }

        val size: Int
            get() = scores.size

        operator fun set(state: State, score: Double) {
            scores[state] = score
        }

        operator fun get(state: State): Double = scores[state] ?: Double.MAX_VALUE
    }

    inner class FScores(
        initial: List<State>,
    ) {
        private val scores = mutableMapOf<State, Double>()
            .apply {
                initial.forEach { put(it, 0.0) }
            }

        val size: Int
            get() = scores.size

        fun add(state: State, score: Double) {
            scores[state] = score + state.distance()
        }

        operator fun get(state: State): Double = scores[state] ?: Double.MAX_VALUE
    }
}

data class State(
    val point: Point,
    val orientation: Orientation,
    val steps: Int,
) {
    fun neighbours(minBlocks: Int, maxBlocks: Int): List<State> =
        point.neighbours
            .map { (o, p) ->
                val newSteps = if (orientation == o) steps + 1 else 1
                State(p, o, newSteps)
            }
            .filter { state ->
                state.orientation != orientation.opposite &&
                        state.steps < maxBlocks &&
                        (orientation == state.orientation || steps >= minBlocks)
            }
}

fun Map<Point, Point>.followPath(point: Point, hops: Int? = null): List<Point> {
    val path = mutableListOf(point)
    var current = point
    while (containsKey(current) && (hops == null || path.size < hops)) {
        current = get(current)!!
        path.add(current)
    }
    return path
}

private fun reconstructPath(cameFrom: Map<Point, Point>, start: Point): List<Point> =
    cameFrom.followPath(start)

