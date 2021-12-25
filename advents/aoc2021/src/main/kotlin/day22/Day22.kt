package com.gilpereda.adventsofcode.adventsofcode2021.day22

import com.gilpereda.adventsofcode.adventsofcode2021.Executable
import kotlin.math.abs

val relevantCuboid = Cuboid(
    -50..50,
    -50..50,
    -50..50,
    false
)

val part1: Executable = { input ->
    val cuboids = input.map(::parseLine).toList()
        .filter { it intersects relevantCuboid }
    secondVersion(cuboids, relevantCuboid).toString()
}

val part2: Executable = { input ->
    val cuboids = input.map(::parseLine).toList()
    val points = cuboids.fold(emptySet<Point>()) { acc, cuboid -> acc + cuboid.points }
    secondVersion(cuboids).toString()
}

private fun secondVersion(cuboids: List<Cuboid>, relevantCuboid: Cuboid? = null): Int {
    val reversedCuboids = cuboids.reversed()
    val points = cuboids.points.let {
        if (relevantCuboid != null) it.filter { it inside relevantCuboid } else it
    }
    val start = System.currentTimeMillis()
    return points.filterIndexed { index, point ->
        if (index % 1_000_000 == 0) {
            val time = System.currentTimeMillis() - start
            println("$time - $index")
        }
        point.finalState(reversedCuboids)
    }.count()
}

private fun thirdVersion(cuboids: List<Cuboid>): Int =
    cuboids.fold(emptySet<Point>()) { acc, cuboid ->
        cuboid.points.fold(acc) { set, point -> if (cuboid.on) set + point else set - point }
    }.size


val List<Cuboid>.points: Sequence<Point>
    get() =
        intersections(this).asSequence().flatMap { intersections ->
            val xRange = intersections.minOf { it.xRange.first }..intersections.maxOf { it.xRange.last }
            val yRange = intersections.minOf { it.yRange.first }..intersections.maxOf { it.yRange.last }
            val zRange = intersections.minOf { it.zRange.first }..intersections.maxOf { it.zRange.last }
            val numberOfPoints = xRange.length * yRange.length * zRange.length
            println("Intersection number of points: $numberOfPoints")
            xRange.asSequence().flatMap { x ->
                yRange.asSequence().flatMap { y -> zRange.asSequence().map { z -> Point(x, y, z) } }
            }
        }

fun intersections(cuboids: List<Cuboid>): List<Set<Cuboid>> {
    val intersectionsList = cuboids.map { cuboid ->
        Intersections(
            cuboid = cuboid,
            intersects = cuboids.filter { it == cuboid || cuboid intersects it }.toSet(),
        )
    }
    val intersectionsSetList = cuboids.fold(emptyList<Set<Cuboid>>()) { setList, cuboid ->
        val cuboidIntersections = intersectionsList.forCuboid(cuboid)
        setList.fold(listOf(cuboidIntersections)) { acc, set ->
            if (acc.any { set.hasCommonElementsWith(it) }) {
                acc.map {
                    if (set.hasCommonElementsWith(it)) {
                        set + it
                    } else {
                        it
                    }
                }
            } else {
                acc + listOf(set)
            }
        }
    }

    assert(cuboids.all { cuboid -> intersectionsSetList.count { cuboid in it } == 1 })
    return intersectionsSetList
}

fun Set<Cuboid>.hasCommonElementsWith(other: Set<Cuboid>): Boolean = any { it in other }

fun List<Intersections>.forCuboid(cuboid: Cuboid): Set<Cuboid> =
    map { it.intersects }.filter { cuboid in it }
        .fold(emptySet()) { acc, intersects -> acc + intersects }

val Cuboid.points: Sequence<Point>
    get() = xRange.asSequence()
        .flatMap { x -> yRange.asSequence().flatMap { y -> zRange.asSequence().map { z -> Point(x, y, z) } } }

infix fun Point.inside(cuboid: Cuboid): Boolean = x in cuboid.xRange && y in cuboid.yRange && z in cuboid.zRange

fun Point.finalState(cuboids: List<Cuboid>): Boolean = cuboids.firstOrNull { this inside it }?.on ?: false

private fun firstVersion(input: Sequence<String>) =
    input.map(::parseLine)
        .filter { it intersects relevantCuboid }
        .fold(Reactor(relevantCuboid.xRange, relevantCuboid.yRange, relevantCuboid.zRange)) { reactor, next ->
            next.updateReactor(reactor)
        }.countOn().toString()


private val cuboidRegex =
    "(on|off) x=([-\\d]+)\\.\\.([-\\d]+),y=([-\\d]+)\\.\\.([-\\d]+),z=([-\\d]+)\\.\\.([-\\d]+)".toRegex()

fun parseLine(line: String): Cuboid =
    cuboidRegex.find(line)?.destructured?.let { (onOff, xFrom, xTo, yFrom, yTo, zFrom, zTo) ->
        Cuboid(
            xRange = xFrom.toInt()..xTo.toInt(),
            yRange = yFrom.toInt()..yTo.toInt(),
            zRange = zFrom.toInt()..zTo.toInt(),
            on = onOff == "on"
        )
    } ?: throw Exception("Could not parse line $line")

data class Cuboid(val xRange: IntRange, val yRange: IntRange, val zRange: IntRange, val on: Boolean) {
    infix fun intersects(other: Cuboid): Boolean =
        xRange intersects other.xRange && yRange intersects other.yRange && zRange intersects other.zRange

    fun updateReactor(reactor: Reactor): Reactor {
        xRange.flatMap { x -> yRange.flatMap { y -> zRange.map { z -> Point(x, y, z) } } }
            .forEach { reactor[it] = on }
        return reactor
    }

    val numberOfPoints: Long
        get() = xRange.length * yRange.length * zRange.length

    val points: Set<Point>
        get() = xRange.flatMap { x ->
            yRange.flatMap { y -> zRange.map { z -> Point(x, y, z) } }
        }.toSet()
}

class Reactor(
    private val xRange: IntRange, private val yRange: IntRange, private val zRange: IntRange,
) {
    private val cubes = mutableSetOf<Point>()
//    private val cubes = BooleanArray(xRange.length * yRange.length * zRange.length)

    operator fun set(point: Point, value: Boolean) {
        if (point.inReactor) {
            if (value) {
                cubes.add(point)
            } else {
                cubes.remove(point)
            }
        }
    }

    operator fun get(point: Point): Boolean = cubes.contains(point)

    fun countOn(): Int = cubes.size

    private val Point.asIndex: Long
        get() = x - xRange.first + (y - yRange.first) * xRange.length + (z - zRange.first) * xRange.length * yRange.length

    private val Point.inReactor: Boolean
        get() = x in xRange && y in yRange && z in zRange
}

data class Point(val x: Int, val y: Int, val z: Int)

val IntRange.length: Long
    get() = abs(last - first + 1).toLong()

private infix fun IntRange.intersects(other: IntRange): Boolean =
    other.first < first && other.last >= first || other.last > last && other.first <= last || other.first in this || other.last in this


data class Intersections(
    val cuboid: Cuboid,
    val intersects: Set<Cuboid>
)