package com.gilpereda.aoc2022.utils.map

import com.gilpereda.aoc2022.utils.TypedTwoDimensionalMap
import com.gilpereda.aoc2022.utils.geometry.Point
import kotlin.math.absoluteValue

class UnlimitedTypedTwoDimensionalMap<T>(
    private val map: TypedTwoDimensionalMap<T>,
) {
    val originalWidth: Int
        get() = map.width
    val originalHeight: Int
        get() = map.height

    operator fun get(point: Point): T =
        map[transformed(point)]

    fun valuesIndexed(): List<Pair<Point, T>> =
        map.valuesIndexed()

    fun transformed(point: Point): Point =
        Point.from(point.x.transform(map.width), point.y.transform(map.height))

    private fun Int.transform(length: Int): Int =
        if (this >= 0) this % length else length - ((this + 1) % length) .absoluteValue -1
}