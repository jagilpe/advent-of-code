package com.gilpereda.aoc2022.utils

import kotlin.math.abs

data class Point(
    val x: Long,
    val y: Long,
) {
    constructor(x: Int, y: Int) : this(x.toLong(), y.toLong())

    fun distanceTo(other: Point): Long =
        abs(x - other.x) + abs(y - other.y)
}