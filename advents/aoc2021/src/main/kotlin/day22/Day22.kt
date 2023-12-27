package com.gilpereda.adventsofcode.adventsofcode2021.day22

import com.gilpereda.adventsofcode.adventsofcode2021.Executable
import kotlin.math.abs

val relevantCuboid = Cuboid(
    -50..50,
    -50..50,
    -50..50,
)

val part1: Executable = { input ->
    input.map(::parseLine)
        .filter { it.cuboid inside relevantCuboid }
        .fold(emptySet<Cuboid>()) { acc, reactor ->
            if (reactor.on) {
                acc + difference(reactor.cuboid, acc)
            } else {
                acc.flatMap { cuboid -> cuboid - reactor.cuboid }.toSet()
            }
        }
        .fold(0L) { count, cuboid -> count + cuboid.points }.toString()
}

val part2: Executable = { input ->
    input.map(::parseLine)
        .fold(emptySet<Cuboid>()) { acc, reactor ->
            if (reactor.on) {
                acc + difference(reactor.cuboid, acc)
            } else {
                acc.flatMap { cuboid -> cuboid - reactor.cuboid }.toSet()
            }
        }
        .fold(0L) { count, cuboid -> count + cuboid.points }.toString()
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

    val points: Long
        get() = xRange.length * yRange.length * zRange.length

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
        setOf(this) + (other - this)

    operator fun minus(other: Cuboid): Set<Cuboid> =
        when {
            this inside other -> emptySet()
            !(this intersectsWith other) -> setOf(this)
            else -> setOfNotNull(
                positiveFace(other),
                negativeFace(other),
                positiveLateral(other),
                negativeLateral(other),
                top(other),
                bottom(other),
            )
        }

    private fun negativeFace(other: Cuboid): Cuboid? =
        if (other.zRange.first > zRange.first) {
            Cuboid(xRange, yRange, zRange.first until other.zRange.first)
        } else {
            null
        }

    private fun positiveFace(other: Cuboid): Cuboid? =
        if (other.zRange.last < zRange.last) {
            Cuboid(xRange, yRange, (other.zRange.last + 1)..zRange.last)
        } else {
            null
        }

    private fun negativeLateral(other: Cuboid): Cuboid? =
        if (other.xRange.first > xRange.first) {
            Cuboid(xRange.first until other.xRange.first, yRange, centralZRange(other))
        } else {
            null
        }

    private fun positiveLateral(other: Cuboid): Cuboid? =
        if (other.xRange.last < xRange.last) {
            Cuboid((other.xRange.last + 1)..xRange.last, yRange, centralZRange(other))
        } else {
            null
        }

    private fun bottom(other: Cuboid): Cuboid? =
        if (other.yRange.first > yRange.first) {
            Cuboid(centralXRange(other), yRange.first until other.yRange.first, centralZRange(other))
        } else {
            null
        }

    private fun top(other: Cuboid): Cuboid? =
        if (other.yRange.last < yRange.last) {
            Cuboid(centralXRange(other), (other.yRange.last + 1)..yRange.last, centralZRange(other))
        } else {
            null
        }

    private fun centralZRange(other: Cuboid): IntRange =
        maxOf(zRange.first, other.zRange.first)..minOf(zRange.last, other.zRange.last)

    private fun centralXRange(other: Cuboid): IntRange =
        maxOf(xRange.first, other.xRange.first)..minOf(xRange.last, other.xRange.last)

}

data class Point(val x: Int, val y: Int, val z: Int)

val IntRange.length: Long
    get() = abs(last - first + 1).toLong()

private infix fun IntRange.intersects(other: IntRange): Boolean =
    other.first < first && other.last >= first || other.last > last && other.first <= last || other.first in this || other.last in this

private infix fun IntRange.intersectionWith(other: IntRange): IntRange {
    val newFirst = maxOf(first, other.first)
    val newLast = minOf(last, other.last)
    return newFirst..newLast
}
