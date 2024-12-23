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
    val game = Game(inputList.drop(1))

    val cheatsCache = CheatsCache()
    return game.solve(cheatsCache, 2, savings).toString()
}

fun secondTask(input: Sequence<String>): String {
    val inputList = input.toList()
    val savings = inputList.first().split(",")[1].toInt()
    val game = Game(inputList.drop(1))

    val cheatsCache = CheatsCache()
    return game.solve2(cheatsCache, 20, savings).toString()
}

class Game(
    input: List<String>,
) {
    private lateinit var start: Point
    private lateinit var target: Point

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
        cheatsCache: CheatsCache,
        maxCheat: Int,
        savings: Int,
    ): Int {
        val originalBestTimeTrack = TrackFinder().solve()
        warmupCache(cheatsCache, originalBestTimeTrack, maxCheat)
        return findBetterPaths(originalBestTimeTrack, maxCheat, cheatsCache)
            .count { it.win >= savings }
    }

    fun solve2(
        cheatsCache: CheatsCache,
        maxCheat: Int,
        savings: Int,
    ): Int {
        val originalBestTimeTrack = TrackFinder().solve()
        warmupCache2(cheatsCache, originalBestTimeTrack, maxCheat)
        val shortCuts =
            findBetterPaths(originalBestTimeTrack, maxCheat, cheatsCache)
                .filter { it.win >= savings }
                .groupBy { Pair(it.start, it.end) }

        val shortCutsByWin =
            shortCuts
                .mapValues { (_, cheats) ->
                    cheats.maxBy { it.win }
                }.entries
                .groupBy { it.value.win }
                .entries
                .sortedByDescending { it.key }

        return shortCuts
            .mapValues { (_, cheats) -> cheats.maxOf { it.win } }
            .size
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
        cheatsCache: CheatsCache,
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
    ): Map<Point, Set<List<Point>>> {
        tailrec fun go(
            open: List<List<Point>>,
            acc: MutableMap<Point, MutableSet<List<Point>>>,
        ): Map<Point, Set<List<Point>>> =
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
                            acc.computeIfAbsent(finish) { mutableSetOf() }.add(current)
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
        cheatsCache: CheatsCache,
        path: List<Point>,
        maxLength: Int,
    ) {
        (1..<path.size).forEach { window ->
            (0..<path.size - window).forEach { from ->
                val startPoint = path[from]
                val to = from + window
                val endPoint = path[to]
                if (startPoint.distanceTo(endPoint) < window && endPoint.distanceTo(startPoint) <= maxLength + 1) {
                    val cheats = findCheatsBetween(startPoint, endPoint, path)
                    if (cheats.isNotEmpty()) {
                        cheatsCache.addCheats(from, to, cheats.minBy { it.size } - startPoint)
                    }
                }
            }
        }
    }

    private fun findCheatsBetween(
        source: Point,
        destination: Point,
        path: List<Point>,
    ): Set<List<Point>> {
        val open = mutableSetOf(source)
        val cameFrom = mutableMapOf<Point, Point>()
        val gScore = GScores(source to 0.0)

        val fScore = FScores(source to 0.0)
        while (open.isNotEmpty()) {
            val current = open.minByOrNull { fScore[it] }!!
            if (current == destination) {
                return setOf(reconstructPath(cameFrom, current).reversed() + target)
            }

            open.remove(current)
            current.neighbours
                .map { it.value }
                .filter { it == destination || it !in path }
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
        return emptySet()
    }

    private fun Point.isValid(): Boolean = x in 1..(map.width - 2) && y in 1..(map.height - 2)

    private fun findBetterPaths(
        path: List<Point>,
        maxCheat: Int,
        cheatsCache: CheatsCache,
    ): List<ShortCut> {
        val pathLength = path.size

        tailrec fun go(
            currentPosition: Int,
            openCheats: List<ShortCut>,
            acc: List<ShortCut>,
        ): List<ShortCut> =
            if (currentPosition >= pathLength) {
                acc
            } else {
                if (openCheats.isEmpty()) {
                    val next = currentPosition + 1
                    val nextCheats =
                        cheatsCache
                            .getCheatsFor(next)
                            .flatMap { (to, paths) ->
                                paths
                                    .map {
                                        ShortCut(
                                            start = next,
                                            end = to,
                                            length = pathLength,
                                            win = to - next - pathLength,
                                        )
                                    }.filter { it.length <= maxCheat && it.win > 0 }
                            }
                    go(next, nextCheats, acc + nextCheats)
                } else {
                    val current = openCheats.first()
                    val nextStart = current.end
                    val nextCheats =
                        cheatsCache
                            .getCheatsFor(nextStart)
                            .flatMap { (to, paths) ->
                                paths
                                    .map { it.size + current.length + 1 }
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
                    go(currentPosition, openCheats.drop(1) + nextCheats, acc + nextCheats)
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

    inner class TrackFinder {
        fun solve(): List<Point> {
            val open = mutableSetOf(start)
            val cameFrom = mutableMapOf<Point, Point>()
            val gScore = GScores(start to 0.0)

            val fScore = FScores(start to 0.0)
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

        private val Point.isValid: Boolean
            get() = map[this] == Cell.Track
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

    inner class GScores(
        initial: Pair<Point, Double>,
    ) {
        private val scores = mutableMapOf(initial)

        operator fun set(
            position: Point,
            score: Double,
        ) {
            scores[position] = score
        }

        operator fun get(position: Point): Double = scores[position] ?: Double.MAX_VALUE
    }

    inner class FScores(
        initial: Pair<Point, Double>,
    ) {
        private fun h(position: Point): Double = target.euclideanDistanceTo(position)

        private val scores = mutableMapOf(initial)

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
    private val cheatsCache = ConcurrentHashMap<Int, MutableMap<Int, Set<List<Point>>>>()

    operator fun get(pair: Int): Map<Int, Set<List<Point>>>? = cheatsCache[pair]

    fun getCheatsFor(from: Int): Map<Int, Set<List<Point>>> = cheatsCache[from] ?: emptyMap()

    fun addCheats(
        from: Int,
        to: Int,
        cheat: List<Point>,
    ) {
        cheatsCache.computeIfAbsent(from) { mutableMapOf() }[to] = setOf(cheat)
    }

    operator fun set(
        pair: Int,
        value: Map<Int, Set<List<Point>>>,
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
