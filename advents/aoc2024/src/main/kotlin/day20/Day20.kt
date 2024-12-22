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

    return game.solve(2, savings).toString()
}

fun secondTask(input: Sequence<String>): String {
    val inputList = input.toList()
    val savings = inputList.first().split(",")[1].toInt()
    val game = Game(inputList.drop(1), savings)

    return game.solve2(20, savings).toString()
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

    fun solve(
        maxCheat: Int,
        savings: Int,
    ): Int {
        val originalBestTimeTrack = TrackFinder().solve()
        warmupCache(originalBestTimeTrack, maxCheat)
        return findBetterPaths(originalBestTimeTrack, maxCheat)
            .count { it >= savings }
    }

    fun solve2(
        maxCheat: Int,
        savings: Int,
    ): Int {
        val originalBestTimeTrack = TrackFinder().solve()
        warmupCache2(originalBestTimeTrack, maxCheat)
        return findBetterPaths(originalBestTimeTrack, maxCheat)
            .count { it >= savings }
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

    private fun warmupCache(
        path: List<Point>,
        maxLength: Int,
    ) {
        path
            .asSequence()
            .map { it to findCheatsFrom(it, maxLength) }
            .forEach { (startPoint, cheats) ->
                val startIndex = path.indexOf(startPoint)
                cheatsCache[startIndex] = cheats.mapKeys { (p, _) -> path.indexOf(p) }
            }
    }

    private fun findCheatsFrom(
        start: Point,
        limit: Int,
    ): Map<Point, Set<Int>> {
        tailrec fun go(
            open: List<List<Point>>,
            acc: MutableMap<Point, MutableSet<Int>>,
        ): Map<Point, Set<Int>> =
            if (open.isEmpty()) {
                acc
            } else {
                val current = open.first()
                if (current.size <= limit) {
                    val currentEnd = current.last()
                    val (openShortCuts, finishedShortCuts) =
                        currentEnd.neighbours
                            .map { it.value }
                            .filter { it.isValid() }
                            .filter { it !in current }
                            .partition { map[it] == Cell.Wall }

                    if (current.size > 1) {
                        finishedShortCuts.forEach { finish ->
                            acc.computeIfAbsent(finish) { mutableSetOf() }.add(current.size)
                        }
                    }
                    val newOpen = openShortCuts.map { point -> current + point }
                    go(open.drop(1) + newOpen, acc)
                } else {
                    go(open.drop(1), acc)
                }
            }

        return go(listOf(listOf(start)), mutableMapOf())
    }

    private fun warmupCache2(
        path: List<Point>,
        maxLength: Int,
    ) {
        (1..<path.size).forEach { window ->
            (0..<path.size - window).forEach { from ->
                val startPoint = path[from]
                val to = from + window
                val endPoint = path[to]
                if (startPoint.distanceTo(endPoint) < window && endPoint.distanceTo(startPoint) < maxLength + 1) {
                    val cheats = findCheatsBetween(startPoint, endPoint, path)
                    if (cheats.isNotEmpty()) {
                        cheatsCache.addCheats(from, to, cheats.min())
                    }
                }
            }
        }
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

    private fun Point.isValid(): Boolean = x in 1..(map.width - 2) && y in 1..(map.height - 2)

    private fun findBetterPaths(
        path: List<Point>,
        maxCheat: Int,
    ): List<Int> {
        tailrec fun go(
            currentPosition: Int,
            openCheats: List<ShortCut>,
            acc: List<Int>,
        ): List<Int> =
            if (currentPosition >= path.size) {
                acc
            } else {
                if (openCheats.isEmpty()) {
                    val next = currentPosition + 1
                    val nextCheats =
                        cheatsCache
                            .getCheatsFor(next)
                            .flatMap { (to, lengths) ->
                                lengths
                                    .map { length ->
                                        ShortCut(
                                            start = next,
                                            end = to,
                                            length = length,
                                            win = to - next - length,
                                        )
                                    }.filter { it.length <= maxCheat && it.win > 0 }
                            }
                    go(next, nextCheats, acc + nextCheats.map { it.win })
                } else {
                    val current = openCheats.first()
                    val nextStart = current.end
                    val nextCheats =
                        cheatsCache
                            .getCheatsFor(nextStart)
                            .flatMap { (to, lengths) ->
                                lengths
                                    .map { it + current.length + 1 }
                                    .filter { it <= maxCheat }
                                    .map { totalLength ->
                                        ShortCut(
                                            start = current.start,
                                            end = to,
                                            length = totalLength,
                                            win = to - current.start - totalLength,
                                        )
                                    }
                            }
                    go(currentPosition, openCheats.drop(1) + nextCheats, acc + nextCheats.map { it.win })
                }
            }

        return go(-1, emptyList(), emptyList())
    }

    data class ShortCut(
        val start: Int,
        val end: Int,
        val win: Int,
        val length: Int,
    )

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
    private val cheatsCache = ConcurrentHashMap<Int, MutableMap<Int, Set<Int>>>()

    operator fun get(pair: Int): Map<Int, Set<Int>>? = cheatsCache[pair]

    fun getCheatsFor(from: Int): Map<Int, Set<Int>> = cheatsCache[from] ?: emptyMap()

    fun addCheats(
        from: Int,
        to: Int,
        cheat: Int,
    ) {
        cheatsCache.computeIfAbsent(from) { mutableMapOf() }[to] = setOf(cheat)
    }

    operator fun set(
        pair: Int,
        value: Map<Int, Set<Int>>,
    ) {
        cheatsCache[pair] = value.toMutableMap()
    }
}

enum class Cell(
    private val s: String,
) {
    Track("."),
    Wall("#"),
    ;

    override fun toString(): String = s
}
