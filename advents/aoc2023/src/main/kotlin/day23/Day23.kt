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
 * 7602 not right
 * 7842 not right
 *
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

class Graph(private val map: TypedTwoDimensionalMap<Tile>, private val partOne: Boolean) {
    private val pointsInPath = map.valuesIndexed().filter { it.second != Forest }.map { it.first }
    private val startPoint = map.valuesIndexed().first { it.second == Start }.first
    private val finishPoint = map.valuesIndexed().first { it.second == Finish }.first
    val tracks: List<Track> = findTracks()
    private val startTrack by lazy { tracks.first { startPoint in it } }
    private val finishTrack by lazy { tracks.first { finishPoint in it } }
    private val crossroads: Set<Crossroad> = findCrossroads()
    private val startCrossroad = crossroads.first { startTrack in it }
    private val finishCrossroad = crossroads.first { finishTrack in it }

//    fun findLongestRoute(): Int {
//        var iter = 0
//        tailrec fun go(open: MutableList<Route>, longestRoute: Route? = null): Int {
//            if (iter++ % 1_000 == 0)
//                println("Iter: $iter, open: ${open.size}, max length: ${longestRoute?.length}")
//            return if (open.isEmpty()) {
//                longestRoute!!.length
//            } else {
//                val current = open.minBy { it.length }
//                if (current.finished) {
//                    val newLongest = if (longestRoute == null || longestRoute.length < current.length) current else longestRoute
//                    open.remove(current)
//                    go(open, newLongest)
//                } else {
//                    val newRoutes = current.newRoutes()
//                    open.remove(current)
//                    open.addAll(newRoutes)
//                    go(open, longestRoute)
//                }
//            }
//        }
//
//        return go(mutableListOf(Route(listOf(State(startCrossroad, startTrack)))))
//    }

    fun findLongestRoute(): Int {
//        var iter = 0
        val visited = mutableSetOf<String>()
        fun go(current: Crossroad, distance: Int): Int {
            return if (current == finishCrossroad) {
                distance + finishTrack.length - 1
            } else {
                visited.add(current.name)
                val max = current.adjacents
                    .filter { (_, crossroad) -> crossroad.name !in visited }
                    .maxOfOrNull { (track, crossroad) ->
                        go(
                            crossroad,
                            distance + 1 + track.length
                        )
                    }
                visited.remove(current.name)
                max ?: 0
            }
        }

        return go(startCrossroad, startTrack.length + 1)
    }

//    private fun Route.next(): List<Route> {
//        val last = first()
//        val secondLast = getOrNull(1)
//        val adjacents = last.adjacents(secondLast)
//        return adjacents
//                .filter { adjacent -> adjacent != secondLast && adjacent !in this }
//                .map { listOf(it) + this }
//    }

    private fun Route.newRoutes(): List<Route> {
        val tracks = states.map { it.cameFrom }
        return states.last().crossroad.adjacents.filter { it.key !in tracks }.map { State(it.value, it.key) }
            .map { Route(states + it) }
    }

    private val Route.finished: Boolean
        get() = states.last().crossroad == finishCrossroad

    private val Route.length: Int
        get() = states.sumOf { it.cameFrom.length } + finishTrack.length + states.size - 1

    private fun findTracks(): List<Track> {
        val trackFactory = trackFactory()
        val startingTrack = trackFactory(startPoint)
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
                    val newTrack = trackFactory(point)
                    go(newTrack, remainingPoints - point, open.drop(1), newAcc)
                } else if (neighbours.size == 1) {
                    val point = neighbours.first()
                    current.add(point)
                    go(current, remainingPoints - point, open - point, acc)
                } else {
                    val newAcc = acc + current.dropLast()
                    val point = neighbours.first()
                    val newTrack = trackFactory(point)
                    go(newTrack, remainingPoints - point, open - point + neighbours.drop(1), newAcc)
                }
            }

        return go(startingTrack, pointsInPath - startPoint).fillTrackAdjacents()
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
        val crossRoadsFactory = crossroadFactory()
        fun Set<Crossroad>.addCrossroad(track: Track, adjacents: Set<Track>): Set<Crossroad> =
            if (adjacents.isNotEmpty()) {
                val currentUpCrossroads = filter { cx -> adjacents.all { it in cx } }
                if (currentUpCrossroads.size > 1) throw IllegalStateException("Could not have multiple up crossroads")
                val upCrossroad = currentUpCrossroads.firstOrNull()
                if (upCrossroad != null) {
                    if (track !in upCrossroad) upCrossroad.add(track)
                    this + upCrossroad
                } else {
                    this + crossRoadsFactory(adjacents + track)
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

    fun dumpTracks(): String {
        val indexedTracks = tracks.mapIndexed { index, track -> 'a' + index to track }
        return map.dump { point, tile ->
            indexedTracks.firstOrNull { point in it.second }?.first?.toString()
                ?: tile.toString()
        }
    }

    inner class Route(
        val states: List<State>,
    ) {
        override fun toString(): String =
            states
                .joinToString(separator = " -> ", postfix = if (finished) " -> ${finishTrack.name}" else "") {
                    "${it.cameFrom.name} -> ${it.crossroad.name}"
                }
    }

    inner class State(
        val crossroad: Graph.Crossroad,
        val cameFrom: Graph.Track
    )

    inner class Track(
        val name: String,
        private val points: MutableList<Point> = mutableListOf()
    ) {
        constructor(name: String, point: Point) : this(name, mutableListOf(point))

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
            routes(points.last())

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

    private fun trackFactory(): (point: Point,) -> Track {
        var char1 = 'a'
        var char2 = 'a'
        return { point ->
            val name = "$char1$char2"
            if (char2 == 'z') {
                char2 = 'a'
                char1 += 1
            } else {
                char2 += 1
            }
            Track(name, point)
        }
    }

    private fun crossroadFactory(): (trackList: Set<Track>) -> Crossroad {
        var char1 = 'A'
        var char2 = 'A'

        return { trackList ->
            val name = "$char1$char2"
            if (char2 == 'Z') {
                char2 = 'A'
                char1 += 1
            } else {
                char2 += 1
            }
            Crossroad(name, trackList)}
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