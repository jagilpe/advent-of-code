package com.gilpereda.aoc2022.day23

import com.gilpereda.aoc2022.utils.TypedTwoDimensionalMap
import com.gilpereda.aoc2022.utils.collections.ImLiList
import com.gilpereda.aoc2022.utils.geometry.Orientation
import com.gilpereda.aoc2022.utils.geometry.Point
import com.gilpereda.aoc2022.utils.parseToMap

typealias Trek = ImLiList<Point>

fun firstTask(input: Sequence<String>): String {
    val forest = ForestMap(input.toList().parseToMap(Tile.Companion::from))
    return forest.solve1().toString()
}

/**
 * 3095 too low
 * 3311 too low
 */
fun secondTask(input: Sequence<String>): String {
    val graph = Graph(input.toList().parseToMap(Tile.Companion::from), false)
    return graph.findLongestRoute().toString()
}

class ForestMap(
    val map: TypedTwoDimensionalMap<Tile>
) {
    private val start: Point = map.valuesIndexed().first { it.second is Start }.first
    private val finish: Point = map.valuesIndexed().first { it.second is Finish }.first

    fun solve1(): Int {
        tailrec fun go(open: List<Trek>, acc: Int = 0): Int =
            if (open.isEmpty()) {
                acc - 1
            } else {
                val candidate = open.maxBy { it.size }
                if (candidate.finished) {
                    val newAcc = if (candidate.size > acc) candidate.size else acc
                    go(open - candidate, newAcc)
                } else {
                    go(open - candidate + candidate.next(), acc)
                }
            }
        return go(listOf(ImLiList.singleton(finish)))
    }

    private val pointToTrek = mutableMapOf<Point, Int>()

    private val Trek.finished: Boolean
        get() = head == start

    private fun Trek.next(): List<Trek> =
        head.neighbours.values
            .filter { point ->
                map.contains(point) && point != previous && point !in this
            }
            .filter {
                when (val tile = map[it]) {
                    is Forest -> false
                    is Slope -> tile.match(Orientation.followed(it, head))
                    else -> true
                }
            }
            .map { this + it }

    private val Trek.previous: Point?
        get() = tail.headOrNull

    private fun dump(trek: Trek): String =
        map.dump { point, tile ->
            if (point in trek) {
                "O"
            } else {
                tile.toString()
            }
        }
}

typealias Route = List<Graph.Track>

class Graph(private val map: TypedTwoDimensionalMap<Tile>, private val partOne: Boolean) {
    private val pointsInPath = map.valuesIndexed().filter { it.second != Forest }.map { it.first }
    private val start = map.valuesIndexed().first { it.second == Start }.first
    private val finish = map.valuesIndexed().first { it.second == Finish }.first
    val tracks: List<Track> = findTracks()
    private val startTrack by lazy { tracks.first { start in it } }
    private val finishTrack by lazy { tracks.first { finish in it } }
    private val crossroads: Set<Crossroad> = findCrossroads()

    fun findLongestRoute(): Int {
        var iter = 0
        tailrec fun go(open: List<Route>, acc: Int = 0): Int {
            if (iter++ % 10_000 == 0) {
                println("iter: $iter, open: ${open.size}, acc: $acc")
            }
            return if (open.isEmpty()) {
                acc - 1
            } else {
                val current = open.first()
                val newOpen = open.drop(1)
                val next = current.next()
                if (current.finished) {
                    go(newOpen, maxOf(acc, current.length))
                } else {
                    go(newOpen + next, acc)
                }
            }
        }


        return go(listOf(listOf(startTrack)))
    }

    private fun Route.next(): List<Route> {
        val last = first()
        val secondLast = getOrNull(1)
        val adjacents = last.adjacents(secondLast)
        return if (adjacents.count { it in this } >= 2) {
            emptyList()
        } else {
            adjacents
                .filter { adjacent -> adjacent != secondLast && adjacent !in this }
                .map { listOf(it) + this }
        }
    }

    private val Route.length: Int
        get() = sumOf { it.length } + size - 1

    private val Route.finished: Boolean
        get() = first == finishTrack

    private fun findTracks(): List<Track> {
        var nextTrackName = 'a'
        val startingTrack = Track(start, nextTrackName++)
        tailrec fun go(
            current: Track,
            remainingPoints: List<Point>,
            open: List<Point> = emptyList(),
            acc: List<Track> = emptyList()
        ): List<Track> =
            if (remainingPoints.isEmpty()) {
                acc + current
            } else {
                val neighbours = current.nextNeighbours()
                    .filter { point -> point in map && map[point] != Forest && point in remainingPoints }
                if (neighbours.isEmpty()) {
                    val newAcc = acc + current
                    val point = open.first()
                    val newTrack = Track(point, nextTrackName++)
                    go(newTrack, remainingPoints - point, open.drop(1), newAcc)
                } else if (neighbours.size == 1) {
                    val point = neighbours.first()
                    current.add(point)
                    go(current, remainingPoints - point, open - point, acc)
                } else {
                    val newAcc = acc + current.dropLast()
                    val point = neighbours.first()
                    val newTrack = Track(point, nextTrackName++)
                    go(newTrack, remainingPoints - point, open - point + neighbours.drop(1), newAcc)
                }
            }

        return go(startingTrack, pointsInPath - start).fillTrackAdjacents()
    }

    private fun List<Track>.fillTrackAdjacents(): List<Track> =
        map { track ->
            track.routesUp()
                .mapNotNull { point -> this.firstOrNull { point in it } }
                .let(track::addAdjacentsUp)
            track.routesDown()
                .mapNotNull { point -> this.firstOrNull { point in it } }
                .let(track::addAdjacentsDown)
        }

    private fun findCrossroads(): Set<Crossroad> {
        var crossroadId = 1
        fun Set<Crossroad>.addCrossroad(track: Track, adjacents: Set<Track>): Set<Crossroad> =
            if (adjacents.isNotEmpty()) {
                val currentUpCrossroads = filter { cx -> adjacents.all { it in cx } }
                if (currentUpCrossroads.size > 1) throw IllegalStateException("Could not have multiple up crossroads")
                val upCrossroad = currentUpCrossroads.firstOrNull()
                if (upCrossroad != null) {
                    if (track !in upCrossroad) upCrossroad.add(track)
                    this + upCrossroad
                } else {
                    this + Crossroad("${crossroadId++}", adjacents + track)
                }
            } else {
                this
            }
        return tracks.fold(emptySet<Crossroad>()) { acc, track ->
            acc
                .addCrossroad(track, track.adjacentsUp)
                .addCrossroad(track, track.adjacentsDown)
        }.fillCrossroadAdjacents()
    }

    private fun Set<Crossroad>.fillCrossroadAdjacents(): Set<Crossroad> =
        onEach { cx ->
            cx.tracks.forEach { track ->
                filter { it != cx && track in it }
                    .forEach { other -> cx.addAdjacent(other, track) }
            }
        }

    private

    fun dumpTracks(): String {
        val indexedTracks = tracks.mapIndexed { index, track -> 'a' + index to track }
        return map.dump { point, tile ->
            indexedTracks.firstOrNull { point in it.second }?.first?.toString()
                ?: tile.toString()
        }
    }

    inner class Track(
        private val name: Char,
        private val points: MutableList<Point> = mutableListOf()
    ) {
        constructor(point: Point, name: Char) : this(name, mutableListOf(point))

        val length: Int by lazy { points.size }

        val adjacentsUp = mutableSetOf<Track>()
        val adjacentsDown = mutableSetOf<Track>()

        fun adjacents(comingFrom: Track?): Set<Track> =
            when (comingFrom) {
                null -> adjacentsDown.ifEmpty { adjacentsUp }
                in adjacentsUp -> adjacentsDown
                else -> adjacentsUp
            }

        operator fun contains(point: Point): Boolean = points.contains(point)

        fun addAdjacentsUp(tracks: List<Track>): Track {
            adjacentsUp.addAll(tracks)
            return this
        }

        fun addAdjacentsDown(tracks: List<Track>): Track {
            adjacentsDown.addAll(tracks)
            return this
        }

        fun dropLast(): Track {
            points.removeLastOrNull()
            return this
        }

        fun routesUp(): List<Point> =
            routes(points.first())

        fun routesDown(): List<Point> =
            routes(points.last)

        private fun routes(point: Point): List<Point> =
            point.neighbours.values // These should be the cross points
                .flatMap { it.neighbours.values } // These should be the neighbours of the cross points
                .filter { it !in points }

        fun add(point: Point): Track {
            points.add(point)
            return this
        }

        fun nextNeighbours(): List<Point> =
            points.last()
                .neighbours.values.filter { it !in points }

        private fun Point.followsTheSlope(): (Point) -> Boolean = { other ->
            when (val tile = map[this]) {
                is Slope -> tile.match(Orientation.followed(other, this))
                else -> true
            }
        }

        override fun toString(): String = "$name - $length"
    }

    inner class Crossroad(
        val name: String,
        trackList: Set<Track>
    ) {
        val tracks: MutableSet<Track> = mutableSetOf<Track>()
            .also { it.addAll(trackList) }

        val adjacents: MutableMap<Track, Crossroad> = mutableMapOf()

        operator fun contains(track: Track): Boolean =
            tracks.contains(track)

        fun add(track: Track) {
            tracks.add(track)
        }

        fun addAdjacent(other: Crossroad, through: Track) {
            adjacents[through] = other
        }

        override fun toString(): String = "CX $name: ${tracks.joinToString(" - ")}"
    }
}

sealed interface Tile {
    companion object {
        fun from(char: Char): Tile =
            when (char) {
                'S' -> Start
                'O' -> Finish
                '#' -> Forest
                'X' -> Cross
                '.' -> Path
                '^' -> Slope(char)
                'v' -> Slope(char)
                '>' -> Slope(char)
                '<' -> Slope(char)
                else -> throw IllegalArgumentException("Illegal tile $char")
            }
    }
}


data object Forest : Tile {
    override fun toString(): String = "#"
}

data object Start : Tile {
    override fun toString(): String = "S"
}

data object Finish : Tile {
    override fun toString(): String = "F"
}

data object Path : Tile {
    override fun toString(): String = "."
}

data object Cross : Tile {
    override fun toString(): String = "X"
}

data class Slope(
    private val direction: Char
) : Tile {
    override fun toString(): String = "$direction"

    fun match(orientation: Orientation): Boolean =
        when (orientation) {
            Orientation.NORTH -> direction == '^'
            Orientation.SOUTH -> direction == 'v'
            Orientation.EAST -> direction == '>'
            Orientation.WEST -> direction == '<'
        }
}