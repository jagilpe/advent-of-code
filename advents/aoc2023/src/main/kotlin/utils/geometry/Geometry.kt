package com.gilpereda.aoc2022.utils.geometry

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class Point(
    val x: Long,
    val y: Long,
) {
    constructor(x: Int, y: Int) : this(x.toLong(), y.toLong())

    fun north(): Point = Point(x, y - 1)
    fun south(): Point = Point(x, y + 1)
    fun west(): Point = Point(x - 1, y)
    fun east(): Point = Point(x + 1, y)

    fun isBorder(height: Long, width: Long): Boolean =
        x == 0L || x == width - 1L || y == 0L || y == height - 1L

    fun distanceTo(other: Point): Long =
        abs(x - other.x) + abs(y - other.y)

    infix fun inside(polygon: Polygon): Boolean =
        polygon.pointIsInside(this)
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
    fun crossesHorizontalLineAtYBeforeX(x: Long, y: Long): Boolean =
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