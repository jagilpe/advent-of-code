package com.gilpereda.aoc2022.utils.geometry

import com.gilpereda.aoc2022.utils.TypedTwoDimensionalMap

enum class Direction {
    FORWARD,
    RIGHT,
    BACKWARDS,
    LEFT,
}

enum class Orientation {
    NORTH,
    SOUTH,
    EAST,
    WEST;

    fun isOpposite(orientation: Orientation): Boolean =
        orientation == opposite

    fun turnedDirectionTo(other: Orientation): Direction =
        when (this) {
            NORTH -> when (other) {
                NORTH -> Direction.FORWARD
                SOUTH -> Direction.BACKWARDS
                EAST -> Direction.RIGHT
                WEST -> Direction.LEFT
            }
            SOUTH -> when (other) {
                NORTH -> Direction.BACKWARDS
                SOUTH -> Direction.FORWARD
                EAST -> Direction.LEFT
                WEST -> Direction.RIGHT
            }
            EAST -> when (other) {
                NORTH -> Direction.LEFT
                SOUTH -> Direction.RIGHT
                EAST -> Direction.FORWARD
                WEST -> Direction.BACKWARDS
            }
            WEST -> when (other) {
                NORTH -> Direction.RIGHT
                SOUTH -> Direction.LEFT
                EAST -> Direction.BACKWARDS
                WEST -> Direction.FORWARD
            }
        }

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
