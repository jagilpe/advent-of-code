package com.gilpereda.aoc2022.utils

enum class Orientation {
    NORTH,
    SOUTH,
    EAST,
    WEST;
}


fun <T> TwoDimensionalMap<T>.transform(orientation: Orientation): TwoDimensionalMap<T> =
    when (orientation) {
        Orientation.NORTH -> transpose()
        Orientation.SOUTH -> transpose().mirror()
        Orientation.WEST -> this
        Orientation.EAST -> mirror()
    }

fun <T> TwoDimensionalMap<T>.transformBack(orientation: Orientation): TwoDimensionalMap<T> =
    when (orientation) {
        Orientation.NORTH -> transpose()
        Orientation.SOUTH -> mirror().transpose()
        Orientation.WEST -> this
        Orientation.EAST -> mirror()
    }
