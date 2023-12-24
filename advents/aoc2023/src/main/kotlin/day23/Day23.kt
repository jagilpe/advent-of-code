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

fun secondTask(input: Sequence<String>): String {
    val forest = Graph(input.toList().parseToMap(Tile.Companion::from))
    val tracks = forest.tracks
    TODO()
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
                    val newAcc = if(candidate.size > acc) candidate.size else acc
                    go(open - candidate, newAcc)
                } else {
                    go(open - candidate + candidate.next(), acc)
                }
            }
        return go(listOf(ImLiList.singleton(finish)))
    }

    private val pointToTrek = mutableMapOf<Point, Int>()

    fun solve2(): Int {
        Graph(map)
        TODO()
    }

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

    private fun Trek.next2(): List<Trek> =
       head.neighbours.values
            .filter { point ->
                map.contains(point) && count(point) < 2
            }
            .filter {
                when (val tile = map[it]) {
                    is Forest -> false
                    is Slope -> true
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

class Graph(private val map: TypedTwoDimensionalMap<Tile>) {
    private val pointsInPath = map.valuesIndexed().filter { it.second != Forest }.map { it.first }
    private val start = map.valuesIndexed().first { it.second == Start }.first
    val tracks: List<Track> = findTracks()

    private fun findTracks(): List<Track> {
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
                    val newTrack = Track(point)
                    go(newTrack, remainingPoints - point, open.drop(1), newAcc)
                } else if (neighbours.size == 1) {
                    val point = neighbours.first()
                    current.add(point)
                    go(current, remainingPoints - point, open - point, acc)
                } else {
                    val newAcc = acc + current.dropLast()
                    val point = neighbours.first()
                    val newTrack = Track(point)
                    go(newTrack, remainingPoints - point, open - point + neighbours.drop(1), newAcc)
                }
            }
        return go(Track(start), pointsInPath - start).fillAdjacent()
    }

    private fun List<Track>.fillAdjacent(): List<Track> =
        map { track ->
            track.neighbourPoints()
                .mapNotNull { point -> this.firstOrNull { point in it } }
                .let(track::addAdjacents)
        }

    fun dumpTracks(): String {
        val indexedTracks = tracks.mapIndexed { index, track -> 'a' + index to track }
        return map.dump { point, tile ->
            indexedTracks.firstOrNull { point in it.second }?.first?.toString()
                ?: tile.toString()
        }
    }
}

private var nextTrackName = 'a'
data class Track(
    private val name: Char,
    private val points: MutableList<Point> = mutableListOf()
) {
    constructor(point: Point) : this(nextTrackName++, mutableListOf(point))

    private val adjacentList = mutableSetOf<Track>()

    operator fun contains(point: Point): Boolean = points.contains(point)

    fun addAdjacents(tracks: List<Track>): Track {
        adjacentList.addAll(tracks)
        return this
    }

    fun dropLast(): Track {
        points.removeLastOrNull()
        return this
    }

    fun neighbourPoints(): List<Point> =
        // Remember that we are removing the cross points from the track
        listOf(points.first(), points.last())
            .flatMap { it.neighbours.values } // These should be the cross points
            .flatMap { it.neighbours.values } // These should be the neighbours of the cross points
            .filter { it !in points }

    fun add(point: Point): Track {
        points.add(point)
        return this
    }

    fun nextNeighbours(): List<Point> =
        points.last()
            .neighbours.values.filter { it !in points }

    override fun toString(): String = "$name"
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