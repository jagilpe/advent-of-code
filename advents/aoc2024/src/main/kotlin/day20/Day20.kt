package com.gilpereda.aoc2024.day20

import com.gilpereda.adventofcode.commons.geometry.Point
import com.gilpereda.adventofcode.commons.map.parseToMap
import java.util.concurrent.ConcurrentHashMap

fun firstTask(input: Sequence<String>): String {
    val inputList = input.toList()
    val savings =
        inputList
            .first()
            .split(",")
            .first()
            .toInt()
    val game = Game(inputList.drop(1), savings)

    return game.solve(2).toString()
}

fun secondTask(input: Sequence<String>): String {
    val inputList = input.toList()
    val savings = inputList.first().split(",")[1].toInt()
    val game = Game(inputList.drop(1), savings)

    return game.solve(20).toString()
}

class Game(
    input: List<String>,
    private val savingGoal: Int,
) {
    private lateinit var start: Point
    private lateinit var target: Point

    private val cheatsCache = CheatsCache()
    private val map =
        input.parseToMap { point, cell ->
            when (cell) {
                '#' -> Cell.Wall
                'S' -> {
                    start = point
                    Cell.Track
                }

                'E' -> {
                    target = point
                    Cell.Track
                }

                else -> Cell.Track
            }
        }

    fun solve(maxCheat: Int): Long {
        val originalBestTimeTrack = TrackFinder().solve()
        val lengthToBeat = originalBestTimeTrack.size
        warmupCache(originalBestTimeTrack)
        println(dumpPath(originalBestTimeTrack))
        findBetterPaths(originalBestTimeTrack, maxCheat)
        TODO()
    }

    private fun dumpCheats(cheats: List<List<Point>>): String {
        val flattenCheats = cheats.flatten()
        return map.dumpWithIndex { point, c ->
            when (point) {
                in flattenCheats -> "x"
                else -> c.toString()
            }
        }
    }

    private fun warmupCache(path: List<Point>) {
        (1..<path.size - 1).forEach { window ->
            (0..<path.size - window).forEach { from ->
                val startPoint = path[from]
                val to = from + window
                val endPoint = path[to]
                if (startPoint.distanceTo(endPoint) < window && endPoint.distanceTo(startPoint) < 6) {
                    val cheats = findCheatsBetween(startPoint, endPoint, path)
                    if (cheats.isNotEmpty()) {
                        cheatsCache[Pair(from, to)] = cheats
                    }
                }
            }
        }
    }

    private fun findBetterPaths(
        path: List<Point>,
        maxCheat: Int,
    ): List<Int> {
        tailrec fun go(
            open: List<ShortCut>,
            acc: List<Int>,
        ): List<Int> =
            if (open.isEmpty()) {
                acc
            } else {
                TODO()
//                val currentPath = open.first()
//                val (currentIndex, length, active, shortcutUsed) = currentPath
//                if (currentIndex == path.lastIndex) {
//                    go(open.drop(1), acc + length + 1)
//                } else {
//                    if (!active && shortcutUsed) {
//                        go(open.drop(1), acc + length + path.size - currentIndex)
//                    } else {
//                        val newOpen =
// //                            cheatsCache
// //                                .getCheatsFor(currentIndex)
// //                                .flatMap { (to, lengths) ->
// //                                    if (active) {
// //                                        lengths
// //                                            .
// //                                            .map { length -> ShortCut(path.subList(to, path.size - 1), currentLength + length, true) }
// //                                    } else {
// //                                        TODO()
// //                                    }
// //                                }
//                        TODO()
//                    }
            }

        return go(listOf(ShortCut(0)), emptyList())
    }

    data class ShortCut(
        val current: Int,
        val length: Int = 0,
        val active: Boolean = false,
        val shortcutUsed: Boolean = false,
    )

    private fun findCheats(
        path: List<Point>,
        maxCheat: Int,
    ): Set<Set<Point>> {
        fun go(
            from: Int,
            to: Int,
        ): Set<Set<Point>> {
//            val cached = cheatsCache[Pair(from, to)]
//            if (cached != null) {
//                return emptySet()
//            }
//
//            val pathLength = to - from
//            val pointTo = path[to]
//            val pointFrom = path[from]
//
//            val cheats =
//                when {
//                    pathLength <= pointFrom.distanceTo(pointTo) + 1 -> emptySet()
//                    pointFrom.distanceTo(pointTo) < 4 -> findCheatsBetween(pointFrom, pointTo)
//                    else ->
//                        (to - 1 downTo (from + 1))
//                            .flatMap { index -> mergePaths(go(index, to), go(from, index), maxCheat) }
//                            .toSet()
//                }
//            cheatsCache[Pair(from, to)] = cheats
            TODO()
//            return cheats
        }

        return go(0, path.size - 1)
    }

    private fun mergePaths(
        one: Set<Set<Point>>,
        other: Set<Set<Point>>,
        maxCheat: Int,
    ): Set<Set<Point>> =
        when {
            one.isEmpty() -> other
            other.isEmpty() -> one
            else -> one.flatMap { initial -> other.map { final -> initial + final }.filter { it.size <= maxCheat } }.toSet()
        }

    private fun findCheatsBetween(
        source: Point,
        destination: Point,
        path: List<Point>,
    ): Set<Int> =
        source
            .pathsTo(destination)
            .filter { shortcut -> shortcut.none { it in path } }
            .map { it.size }
            .toSet()

    private fun dumpPath(path: List<Point>): String =
        map.dumpWithIndex { point, cell ->
            when (point) {
                start -> "S"
                target -> "E"
                in path -> "O"
                else -> cell.toString()
            }
        }

    inner class TrackFinder(
        private val cheats: Set<Point> = emptySet(),
    ) {
        fun solve(): List<Point> {
            val open = mutableSetOf(start)
            val cameFrom = mutableMapOf<Point, Point>()
            val gScore = GScores()

            val fScore = FScores()
            while (open.isNotEmpty()) {
                val current = open.minByOrNull { fScore[it] }!!
                if (current == target) {
                    return reconstructPath(cameFrom, current).reversed() + target
                }

                open.remove(current)
                current.neighbours
                    .map { it.value }
                    .filter { it.isValid }
                    .forEach { neighbour ->
                        val tentativeGScore = gScore[current] + 1.0
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

        private fun reconstructPath(
            cameFrom: Map<Point, Point>,
            start: Point,
        ): List<Point> {
            val path = mutableListOf<Point>()
            var current = start
            while (cameFrom.containsKey(current)) {
                current = cameFrom[current]!!
                path.add(current)
            }
            return path
        }

        private val Point.isValid: Boolean
            get() = this in cheats || map[this] == Cell.Track
    }

    inner class GScores {
        private val scores = mutableMapOf(start to 0.0)

        operator fun set(
            position: Point,
            score: Double,
        ) {
            scores[position] = score
        }

        operator fun get(position: Point): Double = scores[position] ?: Double.MAX_VALUE
    }

    inner class FScores {
        private fun h(position: Point): Double = target.euclideanDistanceTo(position)

        private val scores = mutableMapOf(start to 0.0)

        fun add(
            point: Point,
            score: Double,
        ) {
            scores[point] = score + h(point)
        }

        operator fun get(point: Point): Double = scores[point] ?: Double.MAX_VALUE
    }
}

class CheatsCache {
    private val cheatsCache = ConcurrentHashMap<Pair<Int, Int>, Set<Int>>()

    operator fun get(pair: Pair<Int, Int>): Set<Int>? = cheatsCache[pair]

    operator fun set(
        pair: Pair<Int, Int>,
        value: Set<Int>,
    ) {
        cheatsCache[pair] = value
    }

    fun getCheatsFor(int: Int): Set<Pair<Int, Set<Int>>> =
        cheatsCache.entries
            .filter { it.key.first == int }
            .map { (points, length) -> points.second to length }
            .toSet()
}

enum class Cell(
    private val s: String,
) {
    Track("."),
    Wall("#"),
    ;

    override fun toString(): String = s
}
