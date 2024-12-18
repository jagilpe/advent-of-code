package com.gilpereda.adventofcode.commons.geometry

import com.gilpereda.adventofcode.commons.geometry.Direction.BACKWARDS
import com.gilpereda.adventofcode.commons.geometry.Direction.FORWARD
import com.gilpereda.adventofcode.commons.geometry.Direction.LEFT
import com.gilpereda.adventofcode.commons.geometry.Direction.RIGHT
import com.gilpereda.adventofcode.commons.geometry.Orientation.EAST
import com.gilpereda.adventofcode.commons.geometry.Orientation.NORTH
import com.gilpereda.adventofcode.commons.geometry.Orientation.SOUTH
import com.gilpereda.adventofcode.commons.geometry.Orientation.WEST
import com.gilpereda.adventofcode.commons.map.Index
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

class Point private constructor(
    val x: Int,
    val y: Int,
) {
    fun north(steps: Int = 1): Point = from(x, y - steps)

    fun south(steps: Int = 1): Point = from(x, y + steps)

    fun west(steps: Int = 1): Point = from(x - steps, y)

    fun east(steps: Int = 1): Point = from(x + steps, y)

    fun isBorder(
        height: Int,
        width: Int,
    ): Boolean = x == 0 || x == width - 1 || y == 0 || y == height - 1

    fun distanceTo(other: Point): Int = abs(x - other.x) + abs(y - other.y)

    fun euclideanDistanceTo(other: Point): Double = sqrt((other.x - x).toDouble().pow(2.0) + (other.y - y).toDouble().pow(2.0))

    fun move(
        orientation: Orientation,
        steps: Int = 1,
    ): Point =
        when (orientation) {
            NORTH -> north(steps = steps)
            SOUTH -> south(steps = steps)
            EAST -> east(steps = steps)
            WEST -> west(steps = steps)
        }

    operator fun minus(other: Point): Point = from(x = x - other.x, y = y - other.y)

    operator fun plus(other: Point): Point = from(x = x + other.x, y = y + other.y)

    fun moveUntil(
        orientation: Orientation,
        predicate: (Point) -> Boolean,
    ): Point = generateSequence(this) { point -> point.move(orientation) }.first(predicate)

    fun withinLimits(
        xRange: IntRange,
        yRange: IntRange,
    ): Boolean = x in xRange && y in yRange

    val neighbours: Map<Orientation, Point> by lazy {
        Orientation.entries.associateWith { move(it) }
    }

    infix fun inside(polygon: Polygon): Boolean = polygon.pointIsInside(this)

    override fun toString(): String =
        """
        (x=$x, y=$y)
        """.trimIndent()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Point

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }

    operator fun component1(): Int = x

    operator fun component2(): Int = y

    companion object {
        fun from(
            x: Index,
            y: Index,
        ): Point = Point(x, y)
    }
}

data class Polygon(
    private val points: List<Point>,
) {
    fun pointIsInside(point: Point): Boolean = sides.count { side -> side.crossesHorizontalLineAtYBeforeX(point.x, point.y) } % 2 == 1

    fun external(): Polygon {
        val newSides =
            (listOf(sides.last()) + sides).windowed(2).map { (previous, side) ->
                when (previous.orientation.turnedDirectionTo(side.orientation)) {
                    RIGHT ->
                        when (side.orientation) {
                            NORTH -> if (clockwise) side.from.south() else side.from.east()
                            SOUTH -> if (clockwise) side.from.east() else side.from.south()
                            EAST -> if (clockwise) side.from else side.from.south().east()
                            WEST -> if (clockwise) side.from.south().east() else side.from
                        }

                    LEFT ->
                        when (side.orientation) {
                            NORTH -> if (clockwise) side.from else side.from.east().south()
                            SOUTH -> if (clockwise) side.from.south().east() else side.from
                            EAST -> if (clockwise) side.from.east() else side.from.south()
                            WEST -> if (clockwise) side.from.south() else side.from.east()
                        }

                    else -> throw IllegalStateException("Unpossible turn")
                }
            }
        return Polygon(newSides)
    }

    private val clockwise: Boolean by lazy {
        (sides + sides.first())
            .windowed(2) { (side, next) ->
                when (side.orientation.turnedDirectionTo(next.orientation)) {
                    FORWARD -> 0
                    RIGHT -> 1
                    BACKWARDS -> 0
                    LEFT -> -1
                }
            }.sum() > 0
    }

    val area: Double by lazy {
        sides.sumOf { (from, to) -> from.x.toDouble() * (to.y + 1).toDouble() - (from.y + 1).toDouble() * to.x.toDouble() }.absoluteValue /
            2
    }

    private val sides: List<Line> by lazy {
        val closedPoints = if (points.first() == points.last()) points else points + points.first()
        closedPoints.windowed(2, 1).map { (from, to) -> Line(from, to) }
    }
}

private data class SideCorrection(
    val line: Line,
    val turnDirection: Direction,
    val accumulatedTurns: Int,
) {
    fun corrected(clockwise: Boolean): Line = TODO()
}

data class Line(
    val from: Point,
    val to: Point,
) {
    fun crossesHorizontalLineAtYBeforeX(
        x: Int,
        y: Int,
    ): Boolean = isVertical && y in minY until maxY && from.x < x

    fun extendTo(point: Point): Line? =
        if (followsTheLine(point)) {
            copy(to = point)
        } else {
            null
        }

    val orientation: Orientation by lazy {
        if (isHorizontal) {
            if (from.x <= to.x) {
                EAST
            } else {
                WEST
            }
        } else if (from.y <= to.y) {
            SOUTH
        } else {
            NORTH
        }
    }

    private fun followsTheLine(point: Point): Boolean = (isHorizontal && from.y == point.y) || (isVertical && from.x == point.x)

    private val isHorizontal: Boolean = from.y == to.y
    private val isVertical: Boolean = from.x == to.x

    private val minY = min(from.y, to.y)
    private val maxY = max(from.y, to.y)
}
