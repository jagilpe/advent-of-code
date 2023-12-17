package com.gilpereda.aoc2022.utils.geometry

import com.gilpereda.aoc2022.utils.Index
import com.gilpereda.aoc2022.utils.Orientation
import kotlin.math.*

class Point private constructor(
    val x: Int,
    val y: Int,
) {

    fun north(): Point = from(x, y - 1)
    fun south(): Point = from(x, y + 1)
    fun west(): Point = from(x - 1, y)
    fun east(): Point = from(x + 1, y)

    fun isBorder(height: Int, width: Int): Boolean =
        x == 0 || x == width - 1 || y == 0 || y == height - 1

    fun distanceTo(other: Point): Int =
        abs(x - other.x) + abs(y - other.y)

    fun move(orientation: Orientation): Point =
        when (orientation) {
            Orientation.NORTH -> from(x, y - 1)
            Orientation.SOUTH -> from(x, y + 1)
            Orientation.EAST -> from(x + 1, y)
            Orientation.WEST -> from(x - 1, y)
        }

    fun moveUntil(orientation: Orientation, predicate: (Point) -> Boolean): Point =
        generateSequence(this) { point -> point.move(orientation) }.first(predicate)

    fun withinLimits(xRange: IntRange, yRange: IntRange): Boolean =
        x in xRange && y in yRange

    val neighbours: Map<Orientation, Point> by lazy {
        Orientation.entries.associateWith { move(it) }
    }

    infix fun inside(polygon: Polygon): Boolean =
        polygon.pointIsInside(this)

    override fun toString(): String = """
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

    companion object {
        fun from(x: Index, y: Index): Point = Point(x, y)
    }
}

enum class DistanceMeasurement {
    EUCLIDEAN,
    MANHATTAN,
}

fun List<Point>.toPolygon(): Polygon {
    tailrec fun go(acc: List<Line>, rest: List<Point>, current: Line): Polygon =
        if (rest.isEmpty()) {
            Polygon(acc)
        } else {
            val head = rest.first()
            val tail = rest.drop(1)
            val nextLine = current.extendTo(head)
            if (nextLine != null) {
                go(acc, tail, nextLine)
            } else {
                go(acc + current, tail, Line(current.to, head))
            }
        }
    return go(emptyList(), this.drop(1) + first(), Line(first(), first()))
}

data class Polygon(
    private val sides: List<Line>
) {
    fun pointIsInside(point: Point): Boolean =
        sides.count { side -> side.crossesHorizontalLineAtYBeforeX(point.x, point.y) } % 2 == 1
}

data class Line(
    val from: Point,
    val to: Point,
) {
    fun crossesHorizontalLineAtYBeforeX(x: Int, y: Int): Boolean =
        isVertical && y in minY until maxY && from.x < x

    fun extendTo(point: Point): Line? =
        if (followsTheLine(point)) {
            copy(to = point)
        } else {
            null
        }

    private fun followsTheLine(point: Point): Boolean =
        (isHorizontal && from.y == point.y) || (isVertical && from.x == point.x)

    private val isHorizontal: Boolean = from.y == to.y
    private val isVertical: Boolean = from.x == to.x

    private val minY = min(from.y, to.y)
    private val maxY = max(from.y, to.y)

}