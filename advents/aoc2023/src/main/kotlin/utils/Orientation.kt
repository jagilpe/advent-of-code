package com.gilpereda.aoc2022.utils

import com.gilpereda.aoc2022.day10.Pipe

enum class Orientation {
    NORTH,
    SOUTH,
    EAST,
    WEST;

    fun isOpposite(orientation: Orientation): Boolean =
        orientation == opposite

    val opposite: Orientation by lazy {
        when (this) {
            NORTH -> SOUTH
            SOUTH -> NORTH
            EAST -> WEST
            WEST -> EAST
        }
    }
}


fun <T> TypedTwoDimensionalMap<T>.transform(orientation: Orientation): TypedTwoDimensionalMap<T> =
    when (orientation) {
        Orientation.NORTH -> transpose()
        Orientation.SOUTH -> transpose().mirror()
        Orientation.WEST -> this
        Orientation.EAST -> mirror()
    }

fun <T> TypedTwoDimensionalMap<T>.transformBack(orientation: Orientation): TypedTwoDimensionalMap<T> =
    when (orientation) {
        Orientation.NORTH -> transpose()
        Orientation.SOUTH -> mirror().transpose()
        Orientation.WEST -> this
        Orientation.EAST -> mirror()
    }
