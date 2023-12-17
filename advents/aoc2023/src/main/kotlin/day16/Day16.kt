package com.gilpereda.aoc2022.day16

import com.gilpereda.aoc2022.utils.Orientation
import com.gilpereda.aoc2022.utils.geometry.Point
import com.gilpereda.aoc2022.utils.map.ByteArrayTwoDimensionalMap
import com.gilpereda.aoc2022.utils.map.parseToByteArrayMap


val startBeam = Beam(
    Point.from(0, 0),
    Orientation.EAST
)

fun firstTask(input: Sequence<String>): String =
    input.toList().parsed().solve(startBeam).toString()

fun secondTask(input: Sequence<String>): String {
    val listInput = input.toList()
    val height = listInput.size
    val width = listInput.first().length
    return startPoints(height, width)
        .maxOf {
            listInput.parsed().solve(it)
        }.toString()
}

fun startPoints(height: Int, width: Int): Sequence<Beam> =
    listOf(
        List(height) { Beam(Point.from(0, it), Orientation.EAST)},
        List(height) { Beam(Point.from(width - 1, it), Orientation.WEST)},
        List(width) { Beam(Point.from(it, 0), Orientation.SOUTH)},
        List(width) { Beam(Point.from(it, height - 1), Orientation.NORTH)},
    ).flatten().asSequence()

fun List<String>.parsed(): Game =
    Game(
        toList().parseToByteArrayMap(
            toValue = Tile.Companion::byteToTile,
            fromValue = Tile.Companion::tileToByte,
            parse = Tile.Companion::from
        )
    )

data class Game(
    private val map: ByteArrayTwoDimensionalMap<Tile>,
) {

    val energized: Int
        get() = map.count { it.energized }

    //    fun dump(): String =
//        map.dump { point: Point, tile: Tile ->
//            val beam = currentBeams.firstOrNull { it.point == point }
//            when {
//                beam != null -> beam.orientation.dump()
//                else -> tile.toString()
//            }
//        }
    private fun Orientation.dump(): String =
        when (this) {
            Orientation.NORTH -> "^"
            Orientation.SOUTH -> "v"
            Orientation.EAST -> ">"
            Orientation.WEST -> "<"
        }

    fun solve(startBeam: Beam): Int {
        tailrec fun go(beams: List<Beam>): Int =
            if (beams.isEmpty()) {
                energized
            } else {
                val next = beams.map { beam ->
                    val previousTile =  tile(beam.point)
                    val (nextTile, nextBeams) = previousTile.next(beam)
                    map.replace(beam.point, nextTile)
                    val countEnergized = if (previousTile.energized) 0 else 1
                    Pair(nextBeams, countEnergized)
                }
                val nextBeams = next.flatMap { it.first }.filter { it.isValid }
//                println(dump(nextBeams))
//                println("\n\n")
                go(nextBeams)
            }
        return go(listOf(startBeam))
    }

    private fun dump(current: List<Beam>): String =
        map.dump { point, tile ->
            when (val beam = current.firstOrNull { it.point == point }) {
                null -> tile.dump()
                else -> beam.orientation.dump()
            }
        }

    private fun tile(point: Point): Tile = map[point]

    private val Beam.isValid: Boolean
        get() = point.x in 0 until map.width && point.y in 0 until map.height
}

data class Beam(
    val point: Point,
    val orientation: Orientation,
)

sealed interface Tile {
    val energized: Boolean
    val code: Int
    val visited: Set<Orientation>
    fun dump(): String

    fun nextBeams(beam: Beam): List<Beam>

    fun next(beam: Beam): Pair<Tile, List<Beam>> =
        if (beam.orientation !in visited) {
            Pair(new(energized = true, visited = visited + beam.orientation), nextBeams(beam))
        } else {
            Pair(this, emptyList())
        }

    fun new(energized: Boolean, visited: Set<Orientation>): Tile

    data class Empty(
        override val energized: Boolean = false,
        override val visited: Set<Orientation> = setOf()
    ) : Tile {
        override val code: Int = 0
        override fun dump(): String = if (energized) "#" else "."

        override fun new(energized: Boolean, visited: Set<Orientation>): Tile = Empty(energized, visited)

        override fun nextBeams(beam: Beam): List<Beam> = listOf(beam.copy(point = beam.point.move(beam.orientation)))
    }

    data class MirrorRight(
        override val energized: Boolean = false,
        override val visited: Set<Orientation> = setOf()
    ) : Tile {
        override val code: Int = 1
        override fun dump(): String = if (energized) "#" else "/"

        override fun new(energized: Boolean, visited: Set<Orientation>): Tile = MirrorRight(energized, visited)

        override fun nextBeams(beam: Beam): List<Beam> =
            with(beam) {
                when (orientation) {
                    Orientation.NORTH -> Orientation.EAST
                    Orientation.SOUTH -> Orientation.WEST
                    Orientation.EAST -> Orientation.NORTH
                    Orientation.WEST -> Orientation.SOUTH
                }.let { newOrientation ->
                    listOf(Beam(point = point.move(newOrientation), orientation = newOrientation))
                }
            }
    }

    data class MirrorLeft(
        override val energized: Boolean = false,
        override val visited: Set<Orientation> = emptySet(),
    ) : Tile {
        override val code: Int = 2
        override fun dump(): String = if (energized) "#" else "\\"

        override fun new(energized: Boolean, visited: Set<Orientation>): Tile = MirrorLeft(energized, visited)

        override fun nextBeams(beam: Beam): List<Beam> =
            with(beam) {
                when (orientation) {
                    Orientation.NORTH -> Orientation.WEST
                    Orientation.SOUTH -> Orientation.EAST
                    Orientation.EAST -> Orientation.SOUTH
                    Orientation.WEST -> Orientation.NORTH
                }.let { newOrientation ->
                    listOf(Beam(point = point.move(newOrientation), orientation = newOrientation))
                }
            }
    }

    data class SplitHorizontal(
        override val energized: Boolean = false,
        override val visited: Set<Orientation> = emptySet(),
    ) : Tile {
        override val code: Int = 3
        override fun dump(): String = if (energized) "#" else "-"

        override fun new(energized: Boolean, visited: Set<Orientation>): Tile = SplitHorizontal(energized, visited)

        override fun nextBeams(beam: Beam): List<Beam> =
            with(beam) {
                when (orientation) {
                    Orientation.NORTH, Orientation.SOUTH -> listOf(
                        Beam(point.move(Orientation.EAST), Orientation.EAST),
                        Beam(point.move(Orientation.WEST), Orientation.WEST),
                    )
                    Orientation.EAST, Orientation.WEST -> listOf(copy(point = point.move(orientation)))
                }
            }
    }

    data class SplitVertical(
        override val energized: Boolean = false,
        override val visited: Set<Orientation> = emptySet(),
    ) : Tile {
        override val code: Int = 4
        override fun dump(): String = if (energized) "#" else "|"

        override fun new(energized: Boolean, visited: Set<Orientation>): Tile = SplitVertical(energized, visited)

        override fun nextBeams(beam: Beam): List<Beam> =
            with(beam) {
                when (orientation) {
                    Orientation.NORTH, Orientation.SOUTH -> listOf(copy(point = point.move(orientation)))
                    Orientation.EAST, Orientation.WEST -> listOf(
                        Beam(point.move(Orientation.NORTH), Orientation.NORTH),
                        Beam(point.move(Orientation.SOUTH), Orientation.SOUTH),
                    )
                }
            }
    }

    companion object {
        fun from(char: Char): Tile =
            when (char) {
                '.' -> Empty()
                '\\' -> MirrorLeft()
                '/' ->  MirrorRight()
                '|' -> SplitVertical()
                '-' -> SplitHorizontal()
                else -> throw IllegalArgumentException("Not possible")
            }

        fun byteToTile(byte: UByte): Tile {
            val asInt = byte.toInt()
            val code = asInt % 8
            val energized = (asInt % 16) / 8 > 0
            val visited = codeToVisited(asInt / 16)
            return when (code) {
                0 -> Empty(energized, visited)
                1 -> MirrorRight(energized, visited)
                2 -> MirrorLeft(energized, visited)
                3 -> SplitHorizontal(energized, visited)
                4 -> SplitVertical(energized, visited)
                else -> throw IllegalArgumentException("Invalid code $code")
            }
        }

        private fun codeToVisited(code: Int): Set<Orientation> {
            tailrec fun go(acc: Set<Orientation>, next: Orientation, rest: Int): Set<Orientation> =
                if (rest == 0) {
                    acc
                } else {
                    when (next) {
                        Orientation.NORTH ->
                            if (rest / 8 == 0) go(acc, Orientation.SOUTH, rest)
                            else go(acc + next, Orientation.SOUTH, rest % 8)
                        Orientation.SOUTH ->
                            if (rest / 4 == 0) go(acc, Orientation.WEST, rest)
                            else go(acc + next, Orientation.WEST, rest % 4)
                        Orientation.WEST ->
                            if (rest / 2 == 0) go(acc, Orientation.EAST, rest)
                            else go(acc + next, Orientation.EAST, rest % 2)
                        Orientation.EAST -> acc + Orientation.EAST
                    }
                }
            return go(emptySet(), Orientation.NORTH, code)
        }

        fun tileToByte(tile: Tile): UByte {
            val energizedCode = if (tile.energized) 1 else 0
            val visitedCode = tile.visited.fold(0) { acc, orientation ->
                when (orientation) {
                    Orientation.EAST -> 1
                    Orientation.WEST -> 2
                    Orientation.SOUTH -> 4
                    Orientation.NORTH -> 8
                } + acc
            }
            return (tile.code + energizedCode * 8 + visitedCode * 16).toUByte()
        }
    }
}