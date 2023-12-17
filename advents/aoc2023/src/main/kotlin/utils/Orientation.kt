package com.gilpereda.aoc2022.utils

enum class Orientation {
    NORTH,
    SOUTH,
    EAST,
    WEST;
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
