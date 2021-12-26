package com.gilpereda.adventsofcode.adventsofcode2021.day22

import com.gilpereda.adventsofcode.adventsofcode2021.Executable
import com.gilpereda.adventsofcode.adventsofcode2021.day22.IntersectionPoint.*
import kotlin.math.abs

val relevantCuboid = Cuboid(
    -50..50,
    -50..50,
    -50..50,
)

val part1: Executable = { input ->
    val cuboids = input.map(::parseLine)
        .filter { it.cuboid inside relevantCuboid }
        .fold(emptySet<Cuboid>()) { acc, reactor ->
            if (reactor.on) {
                acc + difference(reactor.cuboid, acc)
            } else {
                acc.flatMap { cuboid -> cuboid - reactor.cuboid }.toSet()
            }
        }

    TODO()
}

fun difference(cuboid: Cuboid, cuboidSet: Set<Cuboid>): Set<Cuboid> {
    tailrec fun go(set: List<Cuboid>, rest: Set<Cuboid>): Set<Cuboid> =
        if (set.isEmpty()) {
            rest
        } else if (rest.isEmpty()) {
            emptySet()
        } else {
            val first = set.first()
            go(set.drop(1), rest.flatMap { c -> c - first }.toSet())
        }

    return go(cuboidSet.toList(), setOf(cuboid))
}

val part2: Executable = { input -> TODO() }

private val cuboidRegex =
    "(on|off) x=([-\\d]+)\\.\\.([-\\d]+),y=([-\\d]+)\\.\\.([-\\d]+),z=([-\\d]+)\\.\\.([-\\d]+)".toRegex()

fun parseLine(line: String): ReactorCuboid =
    cuboidRegex.find(line)?.destructured?.let { (onOff, xFrom, xTo, yFrom, yTo, zFrom, zTo) ->
        ReactorCuboid(
            cuboid = Cuboid(
                xRange = xFrom.toInt()..xTo.toInt(),
                yRange = yFrom.toInt()..yTo.toInt(),
                zRange = zFrom.toInt()..zTo.toInt()
            ),
            on = onOff == "on"
        )
    } ?: throw Exception("Could not parse line $line")

data class ReactorCuboid(val cuboid: Cuboid, val on: Boolean)

data class Cuboid(val xRange: IntRange, val yRange: IntRange, val zRange: IntRange) {
    init {
        assert(xRange.first <= xRange.last && yRange.first <= yRange.last && zRange.first <= zRange.last)
    }

    infix fun inside(other: Cuboid): Boolean =
        xRange.first in other.xRange && xRange.last in other.xRange &&
                yRange.first in other.yRange && yRange.last in other.yRange &&
                zRange.first in other.zRange && zRange.last in other.zRange

    infix fun intersectsWith(other: Cuboid): Boolean =
        xRange intersects other.xRange && yRange intersects other.yRange && zRange intersects other.zRange

    infix fun intersectionWith(other: Cuboid): Cuboid? =
        when {
            other inside this -> other
            this inside other -> this
            !(this intersectsWith other) -> null
            else -> Cuboid(
                xRange = xRange intersectionWith other.xRange,
                yRange = yRange intersectionWith other.yRange,
                zRange = zRange intersectionWith other.zRange,
            )
        }

    operator fun plus(other: Cuboid): Set<Cuboid> =
        when (this intersectsIn other) {
            INSIDE -> setOf(this)
            WRAPS -> setOf(other)
            NONE -> setOf(this, other)
            else -> intersectionWith(other)
                ?.let { intersection ->
                    (this - intersection) + (other - intersection) + intersection
                } ?: setOf(this, other)
        }


    operator fun minus(other: Cuboid): Set<Cuboid> =
        when (this intersectsIn other) {
            INSIDE -> subtractSubtrahendInside(other)
            NONE -> setOf(this)
            WRAPS -> emptySet()
            VERTEX_X_POS_Y_POS_Z_POS -> setOf(
                Cuboid(xRange, yRange, zRange.first..other.zRange.first),
                Cuboid(xRange.first..other.xRange.first, yRange, other.zRange.first..zRange.last),
                Cuboid(other.xRange.first..xRange.last, yRange.first..other.yRange.first, other.zRange.first..zRange.last),
            )
            VERTEX_X_POS_Y_POS_Z_NEG -> setOf(
                Cuboid(xRange, yRange, other.zRange.last..zRange.last),
                Cuboid(xRange.first..other.xRange.first, yRange, zRange.first..other.zRange.last),
                Cuboid(other.xRange.first..xRange.last, yRange.first..other.yRange.first, zRange.first..other.zRange.last),
            )
            VERTEX_X_POS_Y_NEG_Z_POS -> setOf(
                Cuboid(xRange, yRange, zRange.first..other.zRange.first),
                Cuboid(xRange.first..other.xRange.first, yRange, other.zRange.first..zRange.last),
                Cuboid(other.xRange.first..xRange.last, other.yRange.last..yRange.last, other.zRange.first..zRange.last),
            )
            VERTEX_X_NEG_Y_POS_Z_POS -> setOf(
                Cuboid(xRange, yRange, zRange.first..other.zRange.first),
                Cuboid(other.xRange.last..xRange.last, yRange, other.zRange.first..zRange.last),
                Cuboid(xRange.first..other.xRange.last, yRange.first..other.yRange.first, other.zRange.first..zRange.last),
            )
            VERTEX_X_POS_Y_NEG_Z_NEG -> setOf(
                Cuboid(xRange, yRange, other.zRange.last..zRange.last),
                Cuboid(xRange.first..other.xRange.first, yRange, zRange.first..other.zRange.last),
                Cuboid(other.xRange.first..xRange.last, other.yRange.last..yRange.last, zRange.first..other.zRange.last),
            )
            VERTEX_X_NEG_Y_POS_Z_NEG -> setOf(
                Cuboid(xRange, yRange, other.zRange.last..zRange.last),
                Cuboid(other.xRange.last..xRange.last, yRange, zRange.first..other.zRange.last),
                Cuboid(xRange.first..other.xRange.last, yRange.first..other.yRange.first, zRange.first..other.zRange.last),
            )
            VERTEX_X_NEG_Y_NEG_Z_POS -> setOf(
                Cuboid(xRange, yRange, zRange.first..other.zRange.first),
                Cuboid(other.xRange.last..xRange.last, yRange, other.zRange.first..zRange.last),
                Cuboid(xRange.first..other.xRange.last, other.yRange.last..yRange.last, other.zRange.first..zRange.last),
            )
            VERTEX_X_NEG_Y_NEG_Z_NEG -> setOf(
                Cuboid(xRange, yRange, other.zRange.last..zRange.last),
                Cuboid(other.xRange.last..xRange.last, yRange, zRange.first..other.zRange.last),
                Cuboid(xRange.first..other.xRange.last, other.yRange.last..yRange.last, zRange.first..other.zRange.last),
            )
            EDGE_X_POS_Y_POS -> setOf(
                Cuboid(xRange.first..other.xRange.first, yRange, zRange),
                Cuboid(other.xRange.first..xRange.last, yRange.first..other.yRange.first, zRange),
            )
            EDGE_X_POS_Y_NEG -> setOf(
                Cuboid(xRange.first..other.xRange.first, yRange, zRange),
                Cuboid(other.xRange.first..xRange.last, other.yRange.last..yRange.last, zRange),
            )
            EDGE_X_NEG_Y_POS -> setOf(
                Cuboid(other.xRange.last..xRange.last, yRange, zRange),
                Cuboid(xRange.first..other.xRange.last, yRange.first..other.yRange.first, zRange),
            )
            EDGE_X_NEG_Y_NEG -> setOf(
                Cuboid(other.xRange.last..xRange.last, yRange, zRange),
                Cuboid(xRange.first..other.xRange.last, other.yRange.last..yRange.last, zRange),
            )
            EDGE_X_POS_Z_POS -> setOf(
                Cuboid(xRange.first..other.xRange.first, yRange, zRange),
                Cuboid(other.xRange.first..xRange.last, yRange, zRange.first..other.zRange.first),
            )
            EDGE_X_POS_Z_NEG -> setOf(
                Cuboid(xRange.first..other.xRange.first, yRange, zRange),
                Cuboid(other.xRange.first..xRange.last, yRange, other.zRange.last..zRange.last),
            )
            EDGE_X_NEG_Z_POS -> setOf(
                Cuboid(other.xRange.last..xRange.last, yRange, zRange),
                Cuboid(xRange.first..other.xRange.last, yRange, zRange.first..other.zRange.first),
            )
            EDGE_X_NEG_Z_NEG -> setOf(
                Cuboid(other.xRange.last..xRange.last, yRange, zRange),
                Cuboid(xRange.first..other.xRange.last, yRange, other.zRange.last..zRange.last),
            )
            EDGE_Y_POS_Z_POS -> setOf(
                Cuboid(xRange, yRange.first..other.yRange.first, zRange),
                Cuboid(xRange, other.yRange.first..yRange.last, zRange.first..other.zRange.first),
            )
            EDGE_Y_POS_Z_NEG -> setOf(
                Cuboid(xRange, yRange.first..other.yRange.first, zRange),
                Cuboid(xRange, other.yRange.first..yRange.last, other.zRange.last..zRange.last),
            )
            EDGE_Y_NEG_Z_POS -> setOf(
                Cuboid(xRange, other.yRange.last..yRange.last, zRange),
                Cuboid(xRange, yRange.first..other.yRange.last, zRange.first..other.zRange.first),
            )
            EDGE_Y_NEG_Z_NEG -> setOf(
                Cuboid(xRange, other.yRange.last..yRange.last, zRange),
                Cuboid(xRange, yRange.first..other.yRange.last, other.zRange.last..zRange.last),
            )
            FACE_X_POS -> setOf(Cuboid(xRange.first..other.xRange.first, yRange, zRange))
            FACE_X_NEG -> setOf(Cuboid(other.xRange.last..xRange.last, yRange, zRange))
            FACE_Y_POS -> setOf(Cuboid(xRange, yRange.first..other.yRange.first, zRange))
            FACE_Y_NEG -> setOf(Cuboid(xRange, other.yRange.last..yRange.last, zRange))
            FACE_Z_POS -> setOf(Cuboid(xRange, yRange, zRange.first..other.zRange.first))
            FACE_Z_NEG -> setOf(Cuboid(xRange, yRange, other.zRange.last..zRange.last))
        }

    private fun intersections(other: Cuboid): Set<Cuboid> = TODO()

    private fun subtractSubtrahendInside(other: Cuboid): Set<Cuboid> =
        setOf(
            Cuboid(xRange, yRange, zRange.first..other.zRange.first),
            Cuboid(xRange, yRange, other.zRange.last..zRange.last),
            Cuboid(xRange, yRange.first..other.yRange.first, other.zRange),
            Cuboid(xRange, other.yRange.last..yRange.last, other.zRange),
            Cuboid(xRange.first..other.xRange.first, other.yRange, other.zRange),
            Cuboid(other.xRange.last..xRange.last, other.yRange, other.zRange),
        )

    infix fun intersectsIn(other: Cuboid): IntersectionPoint =
        when {
            xRange positionOf other.xRange == Position.OUTSIDE
                    && yRange positionOf other.yRange == Position.OUTSIDE
                    && zRange positionOf other.zRange == Position.OUTSIDE -> NONE
            xRange positionOf other.xRange == Position.INSIDE
                    && yRange positionOf other.yRange == Position.INSIDE
                    && zRange positionOf other.zRange == Position.INSIDE -> INSIDE
            xRange positionOf other.xRange == Position.WRAPS
                    && yRange positionOf other.yRange == Position.WRAPS
                    && zRange positionOf other.zRange == Position.WRAPS -> WRAPS
            xRange positionOf other.xRange == Position.INTERSECT_RIGHT
                    && yRange positionOf other.yRange == Position.INTERSECT_RIGHT
                    && zRange positionOf other.zRange == Position.INTERSECT_RIGHT -> VERTEX_X_POS_Y_POS_Z_POS
            xRange positionOf other.xRange == Position.INTERSECT_LEFT
                    && yRange positionOf other.yRange == Position.INTERSECT_RIGHT
                    && zRange positionOf other.zRange == Position.INTERSECT_RIGHT -> VERTEX_X_NEG_Y_POS_Z_POS
            xRange positionOf other.xRange == Position.INTERSECT_RIGHT
                    && yRange positionOf other.yRange == Position.INTERSECT_LEFT
                    && zRange positionOf other.zRange == Position.INTERSECT_RIGHT -> VERTEX_X_POS_Y_NEG_Z_POS
            xRange positionOf other.xRange == Position.INTERSECT_RIGHT
                    && yRange positionOf other.yRange == Position.INTERSECT_RIGHT
                    && zRange positionOf other.zRange == Position.INTERSECT_LEFT -> VERTEX_X_POS_Y_POS_Z_NEG
            xRange positionOf other.xRange == Position.INTERSECT_RIGHT
                    && yRange positionOf other.yRange == Position.INTERSECT_LEFT
                    && zRange positionOf other.zRange == Position.INTERSECT_LEFT -> VERTEX_X_POS_Y_NEG_Z_NEG
            xRange positionOf other.xRange == Position.INTERSECT_LEFT
                    && yRange positionOf other.yRange == Position.INTERSECT_LEFT
                    && zRange positionOf other.zRange == Position.INTERSECT_RIGHT -> VERTEX_X_NEG_Y_NEG_Z_POS
            xRange positionOf other.xRange == Position.INTERSECT_LEFT
                    && yRange positionOf other.yRange == Position.INTERSECT_RIGHT
                    && zRange positionOf other.zRange == Position.INTERSECT_LEFT -> VERTEX_X_NEG_Y_POS_Z_NEG
            xRange positionOf other.xRange == Position.INTERSECT_LEFT
                    && yRange positionOf other.yRange == Position.INTERSECT_LEFT
                    && zRange positionOf other.zRange == Position.INTERSECT_LEFT -> VERTEX_X_NEG_Y_NEG_Z_NEG
            xRange positionOf other.xRange == Position.INTERSECT_RIGHT
                    && yRange positionOf other.yRange == Position.WRAPS
                    && zRange positionOf other.zRange == Position.WRAPS -> FACE_X_POS
            xRange positionOf other.xRange == Position.INTERSECT_LEFT
                    && yRange positionOf other.yRange == Position.WRAPS
                    && zRange positionOf other.zRange == Position.WRAPS -> FACE_X_NEG
            xRange positionOf other.xRange == Position.WRAPS
                    && yRange positionOf other.yRange == Position.INTERSECT_RIGHT
                    && zRange positionOf other.zRange == Position.WRAPS -> FACE_Y_POS
            xRange positionOf other.xRange == Position.WRAPS
                    && yRange positionOf other.yRange == Position.INTERSECT_LEFT
                    && zRange positionOf other.zRange == Position.WRAPS -> FACE_Y_NEG
            xRange positionOf other.xRange == Position.WRAPS
                    && yRange positionOf other.yRange == Position.WRAPS
                    && zRange positionOf other.zRange == Position.INTERSECT_RIGHT -> FACE_Z_POS
            xRange positionOf other.xRange == Position.WRAPS
                    && yRange positionOf other.yRange == Position.WRAPS
                    && zRange positionOf other.zRange == Position.INTERSECT_LEFT -> FACE_Z_NEG
            xRange positionOf other.xRange == Position.INTERSECT_RIGHT
                    && yRange positionOf other.yRange == Position.INTERSECT_RIGHT
                    && zRange positionOf other.zRange == Position.WRAPS -> EDGE_X_POS_Y_POS
            xRange positionOf other.xRange == Position.INTERSECT_RIGHT
                    && yRange positionOf other.yRange == Position.INTERSECT_LEFT
                    && zRange positionOf other.zRange == Position.WRAPS -> EDGE_X_POS_Y_NEG
            xRange positionOf other.xRange == Position.INTERSECT_LEFT
                    && yRange positionOf other.yRange == Position.INTERSECT_RIGHT
                    && zRange positionOf other.zRange == Position.WRAPS -> EDGE_X_NEG_Y_POS
            xRange positionOf other.xRange == Position.INTERSECT_LEFT
                    && yRange positionOf other.yRange == Position.INTERSECT_LEFT
                    && zRange positionOf other.zRange == Position.WRAPS -> EDGE_X_NEG_Y_NEG
            xRange positionOf other.xRange == Position.INTERSECT_RIGHT
                    && yRange positionOf other.yRange == Position.WRAPS
                    && zRange positionOf other.zRange == Position.INTERSECT_RIGHT -> EDGE_X_POS_Z_POS
            xRange positionOf other.xRange == Position.INTERSECT_RIGHT
                    && yRange positionOf other.yRange == Position.WRAPS
                    && zRange positionOf other.zRange == Position.INTERSECT_LEFT -> EDGE_X_POS_Z_NEG
            xRange positionOf other.xRange == Position.INTERSECT_LEFT
                    && yRange positionOf other.yRange == Position.WRAPS
                    && zRange positionOf other.zRange == Position.INTERSECT_RIGHT -> EDGE_X_NEG_Z_POS
            xRange positionOf other.xRange == Position.INTERSECT_LEFT
                    && yRange positionOf other.yRange == Position.WRAPS
                    && zRange positionOf other.zRange == Position.INTERSECT_LEFT -> EDGE_X_NEG_Z_NEG
            xRange positionOf other.xRange == Position.WRAPS
                    && yRange positionOf other.yRange == Position.INTERSECT_RIGHT
                    && zRange positionOf other.zRange == Position.INTERSECT_RIGHT -> EDGE_Y_POS_Z_POS
            xRange positionOf other.xRange == Position.WRAPS
                    && yRange positionOf other.yRange == Position.INTERSECT_RIGHT
                    && zRange positionOf other.zRange == Position.INTERSECT_LEFT -> EDGE_Y_POS_Z_NEG
            xRange positionOf other.xRange == Position.WRAPS
                    && yRange positionOf other.yRange == Position.INTERSECT_LEFT
                    && zRange positionOf other.zRange == Position.INTERSECT_RIGHT -> EDGE_Y_NEG_Z_POS
            xRange positionOf other.xRange == Position.WRAPS
                    && yRange positionOf other.yRange == Position.INTERSECT_LEFT
                    && zRange positionOf other.zRange == Position.INTERSECT_LEFT -> EDGE_Y_NEG_Z_NEG
            else -> throw Exception("Could not find the intersection position")
        }

}

data class Point(val x: Int, val y: Int, val z: Int)

val IntRange.length: Long
    get() = abs(last - first + 1).toLong()

private infix fun IntRange.intersects(other: IntRange): Boolean =
    other.first < first && other.last >= first || other.last > last && other.first <= last || other.first in this || other.last in this

infix fun IntRange.positionOf(other: IntRange): Position =
    when {
        (other.first < first && other.last < first) || (other.first > last && other.last > last) -> Position.OUTSIDE
        other.first < first && other.last in this -> Position.INTERSECT_LEFT
        other.first in this && other.last > last -> Position.INTERSECT_RIGHT
        other.first in this && other.last in this -> Position.INSIDE
        first in other && last in other -> Position.WRAPS
        else -> throw Exception("Could not find the position")
    }


enum class Position {
    OUTSIDE,
    INTERSECT_LEFT,
    INSIDE,
    INTERSECT_RIGHT,
    WRAPS
}

enum class IntersectionPoint {
    NONE,
    INSIDE,
    WRAPS,
    VERTEX_X_POS_Y_POS_Z_POS,
    VERTEX_X_POS_Y_POS_Z_NEG,
    VERTEX_X_POS_Y_NEG_Z_POS,
    VERTEX_X_NEG_Y_POS_Z_POS,
    VERTEX_X_POS_Y_NEG_Z_NEG,
    VERTEX_X_NEG_Y_POS_Z_NEG,
    VERTEX_X_NEG_Y_NEG_Z_POS,
    VERTEX_X_NEG_Y_NEG_Z_NEG,
    EDGE_X_POS_Y_POS,
    EDGE_X_POS_Y_NEG,
    EDGE_X_NEG_Y_POS,
    EDGE_X_NEG_Y_NEG,
    EDGE_X_POS_Z_POS,
    EDGE_X_POS_Z_NEG,
    EDGE_X_NEG_Z_POS,
    EDGE_X_NEG_Z_NEG,
    EDGE_Y_POS_Z_POS,
    EDGE_Y_POS_Z_NEG,
    EDGE_Y_NEG_Z_POS,
    EDGE_Y_NEG_Z_NEG,
    FACE_X_POS,
    FACE_X_NEG,
    FACE_Y_POS,
    FACE_Y_NEG,
    FACE_Z_POS,
    FACE_Z_NEG,
}

private infix fun IntRange.intersectionWith(other: IntRange): IntRange {
    val newFirst = maxOf(first, other.first)
    val newLast = minOf(last, other.last)
    return newFirst..newLast
}
