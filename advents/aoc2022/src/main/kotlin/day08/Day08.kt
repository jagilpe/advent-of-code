package com.gilpereda.aoc2022.day08

import java.util.stream.IntStream.IntMapMultiConsumer

typealias Forest = List<List<Int>>

fun firstTask(input: Sequence<String>): String {
    val forest = input.parsed
    val forestT = transpose(forest)
    return forest.mapIndexed { x, line ->
        line.filterIndexed { y, _ -> Point(x, y, forest, forestT).isVisible }.count()
    }.sum().toString()
}

fun secondTask(input: Sequence<String>): String {
    val forest = input.parsed
    val forestT = transpose(forest)
    val flatMapIndexed = forest.flatMapIndexed { x, line ->
        line.mapIndexed { y, _ -> Point(x, y, forest, forestT).scenicScore }
    }
    return flatMapIndexed.maxOfOrNull { it.score }.toString()
}

val Sequence<String>.parsed: Forest
    get() = map(::parseLine).toList()

fun parseLine(line: String): List<Int> =
    line.toList().map { it.code - 48 }

fun transpose(forest: Forest): Forest {
    val init: Forest = List(forest.first().size) { listOf() }
    return forest.fold(init) { acc, line ->
        acc.mapIndexed { i, l -> l + line[i] }
    }
}

data class Point(
    val x: Int,
    val y: Int,
    val forest: Forest,
    val tForest: Forest,
) {
    private val height = forest[x][y]
    private val forestHeight = forest.first().size
    private val forestWidth = tForest.first().size

    val isVisible: Boolean
        get() = x == 0 || x == tForest.size - 1 || y == 0 || y == forest.size - 1 ||
                isVisibleFromUp || isVisibleFromDown || isVisibleFromLeft || isVisibleFromRight

    private val isVisibleFromUp: Boolean
        get() {
            return treesToTheNorth.isEmpty() || treesToTheNorth.all { it < height }
        }

    private val treesToTheNorth = tForest[y].slice(0 until x)

    private val isVisibleFromDown: Boolean
        get() {
            return treesToTheSouth.isEmpty() || treesToTheSouth.all { it < height }
        }
    private val treesToTheSouth = tForest[y].slice(x + 1 until forestHeight)

    private val isVisibleFromLeft: Boolean
        get() {
            return treesToTheEast.isEmpty() || treesToTheEast.all { it < height }
        }
    private val treesToTheEast = forest[x].slice(0 until y)

    private val isVisibleFromRight: Boolean
        get() {
            return treesToTheWest.isEmpty() || treesToTheWest.all { it < height }
        }
    private val treesToTheWest = forest[x].slice(y + 1 until forestWidth)

    val scenicScore: VisibleTrees
        get() = VisibleTrees(
            x = x,
            y = y,
            h = height,
            north = visibleTreesToNorth,
            south = visibleTreesToSouth,
            east = visibleTreesToEast,
            west = visibleTreesToWest,
            score = visibleTreesToNorth * visibleTreesToSouth * visibleTreesToEast * visibleTreesToWest,
        )

    private val visibleTreesToNorth: Int
        get() =
            (treesToTheNorth + 0).reversed().visibleTrees


    private val visibleTreesToSouth: Int
        get() =
            (listOf(0) + treesToTheSouth).visibleTrees

    private val visibleTreesToEast: Int
        get() =
            (treesToTheEast + 0).reversed().visibleTrees

    private val visibleTreesToWest: Int
        get() =
            (listOf(0) + treesToTheWest).visibleTrees

    private val List<Int>.visibleTrees: Int
        get() = windowed(2, 1, false)
            .takeWhile { (first, _) -> first < height }.count()

}

data class VisibleTrees(
    val x: Int,
    val y: Int,
    val h: Int,
    val north: Int,
    val south: Int,
    val east: Int,
    val west: Int,
    val score: Int,
)
