package com.gilpereda.aoc2022.day22

import com.gilpereda.aoc2022.day22.Orientation.*
import com.gilpereda.aoc2022.day22.SideId.*
import kotlin.math.absoluteValue

fun secondTask(input: Sequence<String>): String {
    val lines = input.toList()
    val cube = parseCube(lines)
    val (_, movements) = lines.parsed()

    val travel = movements.fold(listOf(Travel.init(cube))) { acc, movement ->
        acc + acc.last().next(movement)
    }

    return travel.last().result.toString()
}

data class Travel(
    val cube: Cube,
    val position: Point,
    val orientation: Orientation,
) {
    val result: Int by lazy {
        1000 * (position.y + 1) + 4 * (position.x + 1) + orientation.value
    }

    fun next(movement: Movement): Travel =
        when (movement) {
            is Turn -> movement.next(orientation).let { copy(orientation = it) }
            is Go -> {
                val (lane, index, way) = cube.getLanePosition(position, orientation)
                val (newPosition, newOrientation) = lane.move(movement.steps, index, way)
                copy(position = newPosition, orientation = newOrientation)
            }
        }

    companion object {
        fun init(cube: Cube): Travel =
            Travel(cube, Point(cube.width, 0), RIGHT)
    }
}

fun parseCube(input: List<String>): Cube {
    val lines = input.filter { it.isNotBlank() }
    val width = lines.first().length / 3

    val seed = SideId.values().associateWith { listOf<Point>() }
    return lines.flatMapIndexed { y, line ->
        line.mapIndexedNotNull { x, cell ->
            if (cell == '#') Point(x, y) else null
        }
    }.fold(seed) { acc, point ->
        val (sideId, point) = point.transformed(width)
        acc + mapOf(sideId to acc.getOrDefault(sideId, listOf()) + point)
    }.let {
        Cube(
            width = width,
            sideA = Side(width, A.start(width), it[A]!!),
            sideB = Side(width, B.start(width), it[B]!!),
            sideC = Side(width, C.start(width), it[C]!!),
            sideD = Side(width, D.start(width), it[D]!!),
            sideE = Side(width, E.start(width), it[E]!!),
            sideF = Side(width, F.start(width), it[F]!!),
        )
    }
}

data class Cube(
    val width: Int,
    val sideA: Side,
    val sideB: Side,
    val sideC: Side,
    val sideD: Side,
    val sideE: Side,
    val sideF: Side,
) {
    private val aLanes: List<Lane> = (0 until width).map { i ->
        Lane(
            width,
            listOf(sideA.segment(i, DOWN), sideC.segment(i, DOWN), sideD.segment(i, DOWN), sideF.segment(i, LEFT))
        )
    }

    private val bLanes: List<Lane> = (0 until width).map { i ->
        Lane(
            width,
            listOf(sideB.segment(i, DOWN), sideC.segment(i, LEFT), sideE.segment(i, DOWN), sideF.segment(i, DOWN))
        )
    }

    private val cLanes: List<Lane> = (0 until width).map { i ->
        Lane(
            width,
            listOf(
                sideA.segment(i, RIGHT),
                sideB.segment(i, RIGHT),
                sideD.segment(width - 1 - i, LEFT),
                sideE.segment(width - 1 - i, LEFT),
            )
        )
    }

    fun getLanePosition(point: Point, orientation: Orientation): LanePosition {
        val (side, pointInSide) = point.transformed(width)
        return when (side) {
            A -> when (orientation) {
                UP -> LanePosition(aLanes[pointInSide.x], pointInSide.y, Way.DEC)
                DOWN -> LanePosition(aLanes[pointInSide.x], pointInSide.y, Way.INC)
                RIGHT -> LanePosition(cLanes[pointInSide.y], pointInSide.x, Way.INC)
                LEFT -> LanePosition(cLanes[pointInSide.y], pointInSide.x, Way.DEC)
            }

            B -> when (orientation) {
                UP -> LanePosition(bLanes[pointInSide.x], pointInSide.y, Way.DEC)
                DOWN -> LanePosition(bLanes[pointInSide.x], pointInSide.y, Way.INC)
                RIGHT -> LanePosition(cLanes[pointInSide.y], pointInSide.x + width, Way.INC)
                LEFT -> LanePosition(cLanes[pointInSide.y], pointInSide.x + width, Way.DEC)
            }

            C -> when (orientation) {
                UP -> LanePosition(aLanes[pointInSide.x], pointInSide.y + width, Way.DEC)
                DOWN -> LanePosition(aLanes[pointInSide.x], pointInSide.y + width, Way.INC)
                RIGHT -> LanePosition(bLanes[pointInSide.y], 2 * width - pointInSide.x - 1, Way.DEC)
                LEFT -> LanePosition(bLanes[pointInSide.y], 2 * width - pointInSide.x - 1, Way.INC)
            }

            D -> when (orientation) {
                UP -> LanePosition(aLanes[pointInSide.x], 2 * width + pointInSide.y, Way.DEC)
                DOWN -> LanePosition(aLanes[pointInSide.x], 2 * width + pointInSide.y, Way.INC)
                RIGHT -> LanePosition(cLanes[width - pointInSide.y - 1], 3 * width - pointInSide.x - 1, Way.DEC)
                LEFT -> LanePosition(cLanes[width - pointInSide.y - 1], 3 * width - pointInSide.x - 1, Way.INC)
            }

            E -> when (orientation) {
                UP -> LanePosition(bLanes[pointInSide.x], 2 * width + pointInSide.y, Way.DEC)
                DOWN -> LanePosition(bLanes[pointInSide.x], 2 * width + pointInSide.y, Way.INC)
                RIGHT -> LanePosition(cLanes[width - pointInSide.y - 1], 4 * width - pointInSide.x - 1, Way.DEC)
                LEFT -> LanePosition(cLanes[width - pointInSide.y - 1], 4 * width - pointInSide.x - 1, Way.INC)
            }

            F -> when (orientation) {
                UP -> LanePosition(bLanes[pointInSide.x], 3 * width + pointInSide.y, Way.DEC)
                DOWN -> LanePosition(bLanes[pointInSide.x], 3 * width + pointInSide.y, Way.INC)
                RIGHT -> LanePosition(aLanes[pointInSide.y], 4 * width - pointInSide.x - 1, Way.DEC)
                LEFT -> LanePosition(aLanes[pointInSide.y], 4 * width - pointInSide.x - 1, Way.INC)
            }
        }
    }

}

data class LanePosition(
    val lane: Lane,
    val position: Int,
    val way: Way,
)

data class Side(
    val width: Int,
    val start: Point,
    val rocksAt: List<Point>,
) {
    fun segment(index: Int, orientation: Orientation): Segment =
        when (orientation) {
            DOWN -> rocksAt.filter { it.x == index }.map { it.y }
                .let { Segment(it) { i, w -> Point(index + start.x, i + start.y) to w.transform(orientation) } }

            UP -> rocksAt.filter { it.x == index }.map { width - it.y - 1 }
                .let {
                    Segment(it) { i, w ->
                        Point(index + start.x, width - i - 1 + start.y) to w.transform(
                            orientation
                        )
                    }
                }

            RIGHT -> rocksAt.filter { it.y == index }.map { it.x }
                .let { Segment(it) { i, w -> Point(i + start.x, index + start.y) to w.transform(orientation) } }

            LEFT -> rocksAt.filter { it.y == index }.map { width - it.x - 1 }
                .let {
                    Segment(it) { i, w ->
                        Point(width - i - 1 + start.x, index + start.y) to w.transform(
                            orientation
                        )
                    }
                }
        }
}

data class Lane(
    val segmentLength: Int,
    val segments: List<Segment>,
) {
    val rocksAt: List<Int> = segments.foldIndexed(emptyList()) { index, acc, segment ->
        acc + segment.rocksAt.map { it + segmentLength * index }
    }

    private val length = segmentLength * segments.size

    fun move(steps: Int, from: Int, way: Way): Pair<Point, Orientation> {
        tailrec fun go(acc: Int, rest: Int): Int =
            when {
                rest == 0 -> acc
                rest > 0 -> {
                    val newPosition = way.move(acc).toIndex()
                    if (canMoveTo(newPosition)) {
                        go(newPosition, rest - 1)
                    } else {
                        acc
                    }
                }
                else -> throw IllegalStateException("rest can not be negative")
            }

        val final = go(from, steps)
        return segments[final / segmentLength].convert(final % segmentLength, way)
    }

    private fun canMoveTo(position: Int): Boolean = position !in rocksAt

    private fun Int.toIndex(): Int =
        when {
            this in 0 until length -> this
            this >= length -> (this % length)
            else -> ((((this / length).absoluteValue + 1) * length + this) % length)
        }.toInt()
}

data class Segment(
    val rocksAt: List<Int>,
    val convert: (Int, Way) -> Pair<Point, Orientation>,
)

data class Point(val x: Int, val y: Int) {
    fun transformed(width: Int): Pair<SideId, Point> =
        listOfNotNull(
            toSideA(width)?.let { A to it },
            toSideB(width)?.let { B to it },
            toSideC(width)?.let { C to it },
            toSideD(width)?.let { D to it },
            toSideE(width)?.let { E to it },
            toSideF(width)?.let { F to it },
        ).firstOrNull() ?: throw Exception("Could not transform $this")

    private fun toSideA(width: Int): Point? =
        if (y in 0 until width && x in width until 2 * width) Point(x - width, y) else null

    private fun toSideB(width: Int): Point? =
        if (y in 0 until width && x in 2 * width until 3 * width) Point(x - 2 * width, y) else null

    private fun toSideC(width: Int): Point? =
        if (y in width until 2 * width && x in width until 2 * width) Point(x - width, y - width) else null

    private fun toSideD(width: Int): Point? =
        if (y in 2 * width until 3 * width && x in width until 2 * width) Point(x - width, y - 2 * width) else null

    private fun toSideE(width: Int): Point? =
        if (y in 2 * width until 3 * width && x in 0 until width) Point(x, y - 2 * width) else null

    private fun toSideF(width: Int): Point? =
        if (y in 3 * width until 4 * width && x in 0 until width) Point(x, y - 3 * width) else null
}

infix fun Int.x(other: Int): Point = Point(this, other)

enum class SideId(val start: (width: Int) -> Point) {
    A({ width -> Point(width, 0) }),
    B({ width -> Point(2 * width, 0) }),
    C({ width -> Point(width, width) }),
    D({ width -> Point(width, 2 * width) }),
    E({ width -> Point(0, 2 * width) }),
    F({ width -> Point(0, 3 * width) }),
}

enum class Way(val transform: (Orientation) -> Orientation, val move: (Int) -> Int) {
    INC(
        { orientation ->
            when (orientation) {
                UP -> UP
                DOWN -> DOWN
                RIGHT -> RIGHT
                LEFT -> LEFT
            }
        },
        { i -> i + 1 }
    ),
    DEC(
        { orientation ->
            when (orientation) {
                UP -> DOWN
                DOWN -> UP
                RIGHT -> LEFT
                LEFT -> RIGHT
            }
        },
        { i -> i - 1 }
    ),
}
