package com.gilpereda.aoc2024.day20

import com.gilpereda.adventofcode.commons.concurrency.SequenceCollector
import com.gilpereda.adventofcode.commons.concurrency.logProgress
import com.gilpereda.adventofcode.commons.concurrency.transformAndCollect
import com.gilpereda.adventofcode.commons.geometry.Point
import com.gilpereda.adventofcode.commons.map.parseToMap
import java.util.concurrent.atomic.AtomicLong

fun firstTask(input: Sequence<String>): String {
    val inputList = input.toList()
    val savings =
        inputList
            .first()
            .split(",")
            .first()
            .toInt()
    val game = Game(inputList.drop(1))

    return game.solve1(2, savings).toString()
}

fun secondTask(input: Sequence<String>): String {
    val inputList = input.toList()
    val savings = inputList.first().split(",")[1].toInt()
    val game = Game(inputList.drop(1))

    return game.solve2(20, savings).toString()
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

    fun solve1(
        maxCheat: Int,
        savings: Int,
    ): Int {
        val path = TrackFinder().solve()
        return findAllShortcutsWithMaxCheat(path, maxCheat)
            .count { it.win >= savings }
    }

    private fun findAllShortcutsWithMaxCheat(
        path: List<Point>,
        maxCheat: Int,
    ): Sequence<Shortcut> =
        path
            .asSequence()
            .map { it to findCheatsFrom(it, maxCheat) }
            .flatMap { (startPoint, cheatsFromStart) ->
                val startIndex = path.indexOf(startPoint)
                cheatsFromStart.flatMap { (endPoint, cheatsToPoint) ->
                    val endIndex = path.indexOf(endPoint)
                    cheatsToPoint.map {
                        Shortcut(
                            start = startIndex,
                            end = endIndex,
                            length = it.size,
                            win = endIndex - startIndex - it.size,
                        )
                    }
                }
            }

    fun solve2(
        maxCheat: Int,
        savings: Int,
    ): Long {
        val path = TrackFinder().solve()

        val collector = ShortcutCollector(savings)
        (1..<path.size)
            .asSequence()
            .flatMap { window -> (0..<path.size - window).asSequence().map { start -> start to (start + window) } }
            .transformAndCollect(
                transform = { findBestCheatBetween(path, it.first, it.second, maxCheat) },
                collector = collector.logProgress(100_000),
            )
        return collector.get()
    }

    private fun findBestCheatBetween(
        path: List<Point>,
        from: Int,
        to: Int,
        maxCheat: Int,
    ): Shortcut? {
        val startPoint = path[from]
        val endPoint = path[to]
        val open = mutableSetOf(startPoint)
        val cameFrom = mutableMapOf<Point, Point>()
        val gScore = GScores(startPoint to 0.0)

        val fScore = FScores(startPoint to 0.0)
        while (open.isNotEmpty()) {
            val current = open.minByOrNull { fScore[it] }!!
            if (current == endPoint) {
                val newPath = reconstructPath(cameFrom, current).reversed()
                return if (newPath.isValidShortcut(maxCheat)) {
                    Shortcut(
                        start = from,
                        end = to,
                        length = newPath.size,
                        win = to - from - newPath.size,
                    )
                } else {
                    null
                }
            }

            open.remove(current)
            current.neighbours
                .map { it.value }
//                .filter { it.isValidForShortcut(cameFrom, maxCheat) }
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
        return null
    }

    private fun Point.isValidForShortcut(
        cameFrom: Map<Point, Point>,
        maxCheat: Int,
    ): Boolean = reconstructPath(cameFrom, this).isValidShortcut(maxCheat)

    private fun List<Point>.isValidShortcut(maxCheat: Int): Boolean {
        val walls =
            mapIndexed { index, point -> index to map.getNullable(point) }
                .filter { it.second == Cell.Wall }
        return if (walls.isEmpty()) {
            true
        } else {
            val firstWallAt = walls.first().first
            val lastWallAt = walls.last().first
            lastWallAt - firstWallAt < maxCheat
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

    private fun findCheatsBetween(
        source: Point,
        destination: Point,
    ): List<Point>? {
        val open = mutableSetOf(source)
        val cameFrom = mutableMapOf<Point, Point>()
        val gScore = GScores(source to 0.0)

        val fScore = FScores(source to 0.0)
        while (open.isNotEmpty()) {
            val current = open.minByOrNull { fScore[it] }!!
            if (current == destination) {
                return reconstructPath(cameFrom, current).reversed() + target
            }

            open.remove(current)
            current.neighbours
                .map { it.value }
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
        return null
    }

    private fun Point.isValid(): Boolean = x in 1..(map.width - 2) && y in 1..(map.height - 2)

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

data class Shortcut(
    val start: Int,
    val end: Int,
    val win: Int,
    val length: Int,
)

class ShortcutCollector(
    private val minWin: Int,
) : SequenceCollector<Shortcut?> {
    private val counter: AtomicLong = AtomicLong(0)

    override fun emit(value: Shortcut?) {
        if (value != null && value.win >= minWin) counter.incrementAndGet()
    }

    fun get(): Long = counter.get()
}

enum class Cell(
    private val s: String,
) {
    Track("."),
    Wall("#"),
    ;

    override fun toString(): String = s
}
