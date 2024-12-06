package com.gilpereda.aoc2024.util.geometry

import com.gilpereda.aoc2024.util.map.TypedTwoDimensionalMap

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
    WEST,
    ;

    fun isOpposite(orientation: Orientation): Boolean = orientation == opposite

    fun turnedDirectionTo(other: Orientation): Direction =
        when (this) {
            NORTH ->
                when (other) {
                    NORTH -> Direction.FORWARD
                    SOUTH -> Direction.BACKWARDS
                    EAST -> Direction.RIGHT
                    WEST -> Direction.LEFT
                }
            SOUTH ->
                when (other) {
                    NORTH -> Direction.BACKWARDS
                    SOUTH -> Direction.FORWARD
                    EAST -> Direction.LEFT
                    WEST -> Direction.RIGHT
                }
            EAST ->
                when (other) {
                    NORTH -> Direction.LEFT
                    SOUTH -> Direction.RIGHT
                    EAST -> Direction.FORWARD
                    WEST -> Direction.BACKWARDS
                }
            WEST ->
                when (other) {
                    NORTH -> Direction.RIGHT
                    SOUTH -> Direction.LEFT
                    EAST -> Direction.BACKWARDS
                    WEST -> Direction.FORWARD
                }
        }

    fun turnLeft(): Orientation =
        when (this) {
            NORTH -> WEST
            SOUTH -> EAST
            EAST -> NORTH
            WEST -> SOUTH
        }

    fun turnRight(): Orientation =
        when (this) {
            NORTH -> EAST
            SOUTH -> WEST
            EAST -> SOUTH
            WEST -> NORTH
        }

    val opposite: Orientation by lazy {
        when (this) {
            NORTH -> SOUTH
            SOUTH -> NORTH
            EAST -> WEST
            WEST -> EAST
        }
    }

    companion object {
        fun followed(
            from: Point,
            to: Point,
        ): Orientation =
            when {
                from.x == to.x && from.y > to.y -> NORTH
                from.x == to.x && from.y < to.y -> SOUTH
                from.y == to.y && from.x > to.x -> WEST
                from.y == to.y && from.x < to.x -> EAST
                else -> throw IllegalArgumentException("Could not find orientation")
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
