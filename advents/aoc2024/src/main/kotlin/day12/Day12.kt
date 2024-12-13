package com.gilpereda.aoc2024.day12

import com.gilpereda.adventofcode.commons.geometry.Point
import com.gilpereda.adventofcode.commons.map.TypedTwoDimensionalMap
import com.gilpereda.adventofcode.commons.map.parseToMap

/**
 * 1461542 is too low
 * 1487052 is too high
 */
fun firstTask(input: Sequence<String>): String {
    val map = input.parseToMap { c -> c }
    val areaList = areaList(map)
    return areaList.sumOf { it.price(map) }.toString()
}

/**
 * 880226 too low
 * 880828 too low
 * 887932
 */
fun secondTask(input: Sequence<String>): String {
    val map = input.parseToMap { c -> c }
    val areaList = areaList(map)
    return areaList.sumOf { it.price2(map) }.toString()
}

private fun areaList(map: TypedTwoDimensionalMap<Char>): List<Area> =
    map
        .valuesIndexed()
        .fold(emptyList<Area>()) { acc, (point, c) ->
            val matched = acc.firstOrNull { it.inArea(c, point) }
            if (matched != null) {
                matched.addPoint(point)
                acc
            } else {
                acc + Area(c).addPoint(point)
            }
        }.let(::mergeAreas)

private tailrec fun mergeAreas(areas: List<Area>): List<Area> {
    val mergedAreas =
        areas
            .groupBy { it.plantType }
            .values
            .flatMap { merge(it) }
    return if (mergedAreas == areas) {
        mergedAreas
    } else {
        mergeAreas(mergedAreas)
    }
}

private fun merge(areas: List<Area>): List<Area> =
    areas.fold(emptyList()) { acc, next ->
        val touchingArea = acc.firstOrNull { it touches next }
        if (touchingArea != null) {
            touchingArea.merge(next)
            acc
        } else {
            acc + next
        }
    }

class Area(
    val plantType: Char,
) {
    private val points: MutableSet<Point> = mutableSetOf()

    fun price(map: TypedTwoDimensionalMap<Char>): Long = area() * perimeter(map)

    fun price2(map: TypedTwoDimensionalMap<Char>): Long = area() * sides(map)

    private fun area(): Long = points.count().toLong()

    private fun perimeter(map: TypedTwoDimensionalMap<Char>): Long = points.sumOf { limits(it, map).toLong() }

    private fun sides(map: TypedTwoDimensionalMap<Char>): Long = points.sumOf { corners(it, map) }.toLong()

    private fun corners(
        point: Point,
        map: TypedTwoDimensionalMap<Char>,
    ): Int =
        listOf(
            if (cornerNW(point, map)) 1 else 0,
            if (cornerNE(point, map)) 1 else 0,
            if (cornerSW(point, map)) 1 else 0,
            if (cornerSE(point, map)) 1 else 0,
        ).sum()

    private fun cornerNW(
        point: Point,
        map: TypedTwoDimensionalMap<Char>,
    ): Boolean =
        (point.north(1).isOutsideArea(map) && point.west(1).isOutsideArea(map)) ||
            (
                point.north(1).west(1).isOutsideArea(map) &&
                    (point.north(1).isInsideArea(map) && point.west(1).isInsideArea(map))
            )

    private fun cornerNE(
        point: Point,
        map: TypedTwoDimensionalMap<Char>,
    ): Boolean =

        (point.north(1).isOutsideArea(map) && point.east(1).isOutsideArea(map)) ||
            (
                point.north(1).east(1).isOutsideArea(map) &&
                    (point.north(1).isInsideArea(map) && point.east(1).isInsideArea(map))
            )

    private fun cornerSW(
        point: Point,
        map: TypedTwoDimensionalMap<Char>,
    ): Boolean =

        (point.south(1).isOutsideArea(map) && point.west(1).isOutsideArea(map)) ||
            (
                point.south(1).west(1).isOutsideArea(map) &&
                    (point.south(1).isInsideArea(map) && point.west(1).isInsideArea(map))
            )

    private fun cornerSE(
        point: Point,
        map: TypedTwoDimensionalMap<Char>,
    ): Boolean =

        (point.south(1).isOutsideArea(map) && point.east(1).isOutsideArea(map)) ||
            (
                point.south(1).east(1).isOutsideArea(map) && (point.south(1).isInsideArea(map) && point.east(1).isInsideArea(map))
            )

    fun dump(
        point: Point,
        map: TypedTwoDimensionalMap<Char>,
    ): String {
        val north = point.north().isOutsideArea(map)
        val south = point.south().isOutsideArea(map)
        val east = point.east().isOutsideArea(map)
        val west = point.west().isOutsideArea(map)

        val nw =
            when {
                north && west -> "+"
                north || west -> " "
                else -> " "
            }
        val n = if (north) "-" else " "
        val ne =
            when {
                north && east -> "+"
                north || east -> " "
                else -> " "
            }
        val w = if (west) "|" else " "
        val e = if (east) "|" else " "
        val sw =
            when {
                south && west -> "+"
                south || west -> " "
                else -> " "
            }
        val se =
            when {
                south && east -> "+"
                south || east -> " "
                else -> " "
            }
        val s = if (south) "-" else " "
        return "$nw$n$ne\n$w$plantType$e\n$sw$s$se"
    }

    private fun limits(
        point: Point,
        map: TypedTwoDimensionalMap<Char>,
    ): Int =
        listOf(
            if (point.north(1).isOutsideArea(map)) 1 else 0,
            if (point.east(1).isOutsideArea(map)) 1 else 0,
            if (point.west(1).isOutsideArea(map)) 1 else 0,
            if (point.south(1).isOutsideArea(map)) 1 else 0,
        ).sum()

    private fun Point.isOutsideArea(map: TypedTwoDimensionalMap<Char>): Boolean = this !in points

    private fun Point.isInsideArea(map: TypedTwoDimensionalMap<Char>): Boolean = this in points

    fun merge(area: Area): Area {
        points.addAll(area.points)
        return this
    }

    fun inArea(
        type: Char,
        point: Point,
    ): Boolean = type == this.plantType && (point in points || points.any { point touches it })

    fun addPoint(point: Point): Area {
        points.add(point)
        return this
    }

    infix fun touches(area: Area): Boolean = points.any { one -> area.points.any { other -> one touches other } }

    private infix fun Point.touches(other: Point): Boolean =
        (x == other.x && y in (other.y - 1)..(other.y + 1)) ||
            (x in (other.x - 1)..(other.x + 1) && y == other.y)

    override fun toString(): String = "$plantType: $points"
}
