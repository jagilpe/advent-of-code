package com.gilpereda.aoc2022.day24

import com.gilpereda.aoc2022.utils.geometry.Axis
import com.gilpereda.aoc2022.utils.geometry.LongPoint
import com.gilpereda.aoc2022.utils.geometry.LongPoint3D
import java.math.BigInteger
import kotlin.math.sign

private const val DUMP = true

/**
 * 20926 -> too low
 */
fun firstTask(
    input: Sequence<String>,
    min: Long,
    max: Long,
): String {
    val hailstones = input.mapIndexed { index, line -> line.parsed(index) }.toList()

    tailrec fun go(
        next: Hailstone,
        rest: List<Hailstone>,
        acc: List<Intersection> = emptyList(),
    ): List<Intersection> =
        if (rest.isEmpty()) {
            acc
        } else {
            val newIntersections = rest.map { next.intersectionWith(it) }
            go(rest.first(), rest.drop(1), acc + newIntersections)
        }

    val intersections = go(hailstones.first(), hailstones.drop(1))
    val (intersecting, parallel) = intersections.partition { it.intersection != null }
    val (matching, notMatching) = intersecting.partition { it.matches(min, max) }

    return matching.count().toString()
}

fun secondTask(input: Sequence<String>): String {
    val hailstones = input.mapIndexed { index, line -> line.parsed(index) }.toList()

    val xProjection = findRockPositionAndVelocity(hailstones, Axis.X)
    val yProjection = findRockPositionAndVelocity(hailstones, Axis.Y)
    val zProjection = findRockPositionAndVelocity(hailstones, Axis.Z)

    val (y1, z1) = xProjection.first
    val (x1, z2) = yProjection.first
    val (x2, y2) = zProjection.first

    val (vy1, vz1) = xProjection.second
    val (vx1, vz2) = yProjection.second
    val (vx2, vy2) = zProjection.second

    assert(x1 == x2 && y1 == y2 && z1 == z2) { "Points should match" }
    assert(vx1 == vx2 && vy1 == vy2 && vz1 == vz2) { "Velocities should match" }

    return (x1 + y1 + z1).toString()
}

fun String.parsed(index: Int): Hailstone =
    split(" @ ").let { (position, velocity) ->
        Hailstone(
            name = "${index + 1}",
            position = LongPoint3D.from(position),
            velocity = LongPoint3D.from(velocity),
        )
    }

data class Intersection(
    val one: Hailstone,
    val other: Hailstone,
    val intersection: LongPoint3D? = null,
    val t: Double? = null,
    val u: Double? = null,
) {
    fun matches(
        min: Long,
        max: Long,
    ): Boolean = intersection != null && inFuture && intersection in min..max

    val inFuture: Boolean = t != null && u != null && t >= 0 && u >= 0

    override fun toString(): String =
        if (intersection != null) {
            "one = $one, other = $other, x = ${intersection.x}, y = ${intersection.y}, t = $t, u = $u, in future = $inFuture"
        } else {
            "one = $one, other = $other, lines are parallel"
        }
}

operator fun LongRange.contains(intersection: LongPoint3D): Boolean {
    val (x, y) = intersection
    return x in this && y in this
}

data class Hailstone(
    val name: String,
    val position: LongPoint3D,
    val velocity: LongPoint3D,
) {
    fun intersectionWith(other: Hailstone): Intersection {
        val (px0, py0) = position.decomposed2D()
        val (vx0, vy0) = velocity.decomposed2D()
        val (px1, py1) = other.position.decomposed2D()
        val (vx1, vy1) = other.velocity.decomposed2D()
        val denominator1 = vx0 * vy1 - vx1 * vy0
        val denominator0 = -denominator1
        return if (denominator1 == 0.0) {
            Intersection(this, other)
        } else {
            val t = ((py1 - py0) * vx1 + (px0 - px1) * vy1) / denominator0
            val u = ((py0 - py1) * vx0 + (px1 - px0) * vy0) / denominator1
            Intersection(this, other, LongPoint3D((px1 + vx1 * u).toLong(), (py1 + vy1 * u).toLong(), 0), t, u)
        }
    }

    fun projected(axis: Axis): ProjectedStone {
        // Take all components except the specified component
        return when (axis) {
            Axis.X -> ProjectedStone(position = LongPoint(position.y, position.z), velocity = LongPoint(velocity.y, velocity.z))
            Axis.Y -> ProjectedStone(position = LongPoint(position.x, position.z), velocity = LongPoint(velocity.x, velocity.z))
            Axis.Z -> ProjectedStone(position = LongPoint(position.x, position.y), velocity = LongPoint(velocity.x, velocity.y))
        }
    }

    override fun toString(): String = name
}

data class ProjectedStone(
    val position: LongPoint,
    val velocity: LongPoint,
) {
    val a: BigInteger = velocity.y.toBigInteger()
    val b: BigInteger = -velocity.x.toBigInteger()
    val c: BigInteger = (velocity.y.toBigInteger() * position.x.toBigInteger() - velocity.x.toBigInteger() * position.y.toBigInteger())

    /**
     * Adds the specified velocity to this projected stone
     */
    fun addingVelocity(delta: LongPoint): ProjectedStone = copy(velocity = velocity + delta)
}

private fun findRockPositionAndVelocity(
    stones: List<Hailstone>,
    axis: Axis,
): Pair<LongPoint, LongPoint> {
    val maxValue = 400L
    val minResultCount = 5
    (-maxValue..maxValue).forEach { vx ->
        (-maxValue..maxValue).forEach { vy ->
            val deltaV = LongPoint(vx, vy)
            val matchingPositions = mutableSetOf<LongPoint>()
            var resultCount = 0
            processPairs(stones, axis, deltaV) { intersection ->
                if (intersection != null) {
                    matchingPositions += intersection
                    resultCount++
                    resultCount < minResultCount
                } else {
                    false
                }
            }
            if (matchingPositions.size == 1 && resultCount >= minOf(minResultCount, stones.size / 2)) {
                return matchingPositions.single() to -deltaV
            }
        }
    }
    throw IllegalStateException("Could not find solutions")
}

private fun processPairs(
    stones: List<Hailstone>,
    axis: Axis,
    deltaSpeed: LongPoint = LongPoint(0, 0),
    process: (LongPoint?) -> Boolean,
) {
    stones.indices.forEach { i ->
        ((i + 1) until stones.size).forEach { j ->
            val firstStone = stones[i].projected(axis).addingVelocity(deltaSpeed)
            val secondStone = stones[j].projected(axis).addingVelocity(deltaSpeed)
            val intersection =
                solve(firstStone, secondStone)?.takeIf { p ->
                    listOf(firstStone, secondStone).all { (p.y - it.position.y).sign == it.velocity.y.sign }
                }
            if (!process(intersection)) return
        }
    }
}

fun solve(
    first: ProjectedStone,
    second: ProjectedStone,
): LongPoint? = solve(first.a, first.b, first.c, second.a, second.b, second.c)

// Solve two linear equations for x and y
// Equations of the form: ax + by = c
fun solve(
    a1: BigInteger,
    b1: BigInteger,
    c1: BigInteger,
    a2: BigInteger,
    b2: BigInteger,
    c2: BigInteger,
): LongPoint? {
    val d = b2 * a1 - b1 * a2
    if (d == BigInteger.ZERO) return null
    val x = (b2 * c1 - b1 * c2) / d
    val y = (c2 * a1 - c1 * a2) / d
    return LongPoint(x.toLong(), y.toLong())
}
