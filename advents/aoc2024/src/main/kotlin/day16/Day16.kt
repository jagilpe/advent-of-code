package com.gilpereda.aoc2024.day16

import com.gilpereda.adventofcode.commons.geometry.Orientation
import com.gilpereda.adventofcode.commons.geometry.Point
import com.gilpereda.adventofcode.commons.map.TypedTwoDimensionalMap
import com.gilpereda.adventofcode.commons.map.parseToMap

fun firstTask(input: Sequence<String>): String =
    input
        .toList()
        .parsed()
        .solve()
        .toString()

fun secondTask(input: Sequence<String>): String {
    val (maze, bestScore) = solveFirst(input.toList())
    return maze.solve2(bestScore).toString()
}

private fun List<String>.parsed(): Maze {
    lateinit var start: Point
    lateinit var exit: Point

    val map =
        parseToMap { point, c ->
            when (c) {
                '#' -> Cell.WALL
                'E' -> {
                    exit = point
                    Cell.EMPTY
                }
                'S' -> {
                    start = point
                    Cell.EMPTY
                }
                else -> Cell.EMPTY
            }
        }
    return Maze(start, exit, map)
}

private fun solveFirst(inputList: List<String>): Pair<Maze, Int> {
    lateinit var start: Point
    lateinit var exit: Point

    val map =
        inputList.parseToMap { point, c ->
            when (c) {
                '#' -> Cell.WALL
                'E' -> {
                    exit = point
                    Cell.EMPTY
                }
                'S' -> {
                    start = point
                    Cell.EMPTY
                }
                else -> Cell.EMPTY
            }
        }
    val maze = Maze(start, exit, map)
    return maze to maze.solve()
}

class Maze(
    start: Point,
    private val goal: Point,
    private val map: TypedTwoDimensionalMap<Cell>,
) {
    private val startReindeer = Reindeer(start, Orientation.EAST)

    fun solve(): Int {
        val open = mutableSetOf(startReindeer)
        val cameFrom = mutableMapOf<Reindeer, Reindeer>()
        val gScore = GScores(startReindeer)

        val fScore = FScores(startReindeer, goal)
        while (open.isNotEmpty()) {
            val current = open.minByOrNull { fScore[it] }!!
            if (current.position == goal) {
                return reconstructPath(cameFrom, current).result()
            }

            open.remove(current)
            current.neighbours
                .filter { valid(it) }
                .forEach { neighbour ->
                    val tentativeGScore = gScore[current] + d(current, neighbour)
                    if (tentativeGScore < gScore[neighbour]) {
                        cameFrom[neighbour] = current
                        gScore[neighbour] = tentativeGScore
                        fScore.add(neighbour, tentativeGScore)
                        if (neighbour !in open) {
                            open.add(neighbour)
                        }
                    }
                }
        }
        throw Exception("No path found")
    }

    fun solve2(bestScore: Int): Int {
        val open = mutableSetOf(listOf(startReindeer))
        val scores = mutableMapOf(startReindeer to listOf(startReindeer).result())
        val paths = mutableSetOf<List<Reindeer>>()
        while (open.isNotEmpty()) {
            val current = open.first()
            open.remove(current)
            if (current.closed()) {
                paths.add(current)
            } else {
                val lastStep =
                    current
                        .last()
                val nextPaths =
                    lastStep
                        .neighbours
                        .filter { valid(it) && it !in current }
                        .map { current + it }
                        .filter { it.result() <= bestScore + 1 }
                nextPaths
                    .forEach {
                        val newScore = it.result()
                        val lastReindeer = it.last()
                        val previousScore = scores[lastReindeer]
                        if (previousScore == null || newScore <= previousScore) {
                            scores[lastReindeer] = newScore
                            open.add(it)
                        }
                    }
            }
        }
        return paths.flatMap { it.map { it.position } }.toSet().size
    }

    private fun dumpPath(path: List<Reindeer>): String =
        map.dumpWithIndex { point, cell ->
            path.firstOrNull { it.position == point }?.orientation?.toString() ?: cell.toString()
        }

    private fun List<Reindeer>.closed(): Boolean = last().position == goal

    private fun valid(neighbour: Reindeer): Boolean = map.getNullable(neighbour.position) == Cell.EMPTY

    private fun d(
        reindeer: Reindeer,
        neighbour: Reindeer,
    ): Double =
        when {
            reindeer.orientation.isOpposite(reindeer.orientation) || reindeer.distanceTo(neighbour) > 1 ->
                throw IllegalStateException("Wrong neighbour detected")
            reindeer.orientation != neighbour.orientation && reindeer.distanceTo(neighbour) == 0 -> 1000.0
            reindeer.distanceTo(neighbour) == 1 -> 1.0
            else -> throw IllegalStateException("Wrong neighbour detected")
        }

    private fun List<Reindeer>.result(): Int {
        val initial: Pair<Int, Reindeer> = Pair(1, first())
        return drop(1)
            .fold(initial) { (acc, previous), reindeer ->
                val newAcc = acc + if (reindeer.orientation != previous.orientation) 1000 else 1
                newAcc to reindeer
            }.first
    }

    private fun reconstructPath(
        cameFrom: Map<Reindeer, Reindeer>,
        startReindeer: Reindeer,
    ): List<Reindeer> {
        val path = mutableListOf<Reindeer>()
        var current = startReindeer
        while (cameFrom.containsKey(current)) {
            current = cameFrom[current]!!
            path.add(current)
        }
        return path
    }

    class GScores(
        startReindeer: Reindeer,
    ) {
        private val scores = mutableMapOf(startReindeer to 0.0)

        operator fun set(
            reindeer: Reindeer,
            score: Double,
        ) {
            scores[reindeer] = score
        }

        operator fun get(reindeer: Reindeer): Double = scores[reindeer] ?: Double.MAX_VALUE
    }

    class FScores(
        startReindeer: Reindeer,
        private val goal: Point,
    ) {
        private fun h(reindeer: Reindeer): Double = goal.euclideanDistanceTo(reindeer.position)

        private val scores = mutableMapOf(startReindeer to 0.0)

        fun add(
            reindeer: Reindeer,
            score: Double,
        ) {
            scores[reindeer] = score + h(reindeer)
        }

        operator fun get(reindeer: Reindeer): Double = scores[reindeer] ?: Double.MAX_VALUE
    }

    data class Reindeer(
        val position: Point,
        val orientation: Orientation,
    ) {
        fun distanceTo(other: Reindeer): Int = position.distanceTo(other.position)

        val neighbours: List<Reindeer>
            get() =
                listOf(
                    copy(position = position.move(orientation)),
                    copy(orientation = orientation.turnRight()),
                    copy(orientation = orientation.turnLeft()),
                )
    }
}

enum class Cell(
    val s: String,
) {
    WALL("#"),
    EMPTY("."),
    ;

    override fun toString(): String = s
}
