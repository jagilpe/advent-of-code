package com.gilpereda.aoc2022.utils

enum class Orientation {
    NORTH,
    SOUTH,
    EAST,
    WEST,
}


fun <T> TwoDimensionalMap<T>.transform(orientation: Orientation): TwoDimensionalMap<T> =
    when (orientation) {
        Orientation.NORTH -> transpose()
        Orientation.SOUTH -> transpose().reflect()
        Orientation.WEST -> this
        Orientation.EAST -> reflect()
    }

fun <T> TwoDimensionalMap<T>.transformBack(orientation: Orientation): TwoDimensionalMap<T> =
    when (orientation) {
        Orientation.NORTH -> transpose()
        Orientation.SOUTH -> reflect().transpose()
        Orientation.WEST -> this
        Orientation.EAST -> reflect()
    }
