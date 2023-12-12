package com.gilpereda.aoc2022.day11

import com.gilpereda.aoc2022.utils.geometry.Point

/**
 * first try 9766526
 */
fun firstTask(input: Sequence<String>): String =
    taskForExpansion(input, 2)

fun secondTask(input: Sequence<String>): String =
    taskForExpansion(input, 1_000_000)

private fun taskForExpansion(input: Sequence<String>, expansion: Long): String {
    val inputList = input.toList()
    val emptyRows = inputList.emptyRows()
    val emptyColumns = inputList.emptyColumns()
    val galaxies = inputList.parsed().map { it.adjustCoords(emptyRows, emptyColumns, expansion) }

    return (galaxies.flatMap { one ->
        galaxies.map { other -> one.distanceTo(other) }
    }.sum() / 2L).toString()
}

private fun List<String>.parsed(): List<Point> =
    flatMapIndexed { y, line ->
        line.mapIndexedNotNull { x, cell ->
            if (cell == '#') Point(x, y) else null
        }
    }

private fun List<String>.emptyRows(): List<Long> =
    mapIndexedNotNull { y, line -> if (line.contains('#')) null else y.toLong() }

private fun List<String>.emptyColumns(): List<Long> {
    val firstLine = first()
    return drop(1)
        .fold(firstLine.map { listOf(it) }) { acc, next ->
            acc.zip(next.toList()).map { (list, char) -> list + char }
        }
        .mapIndexedNotNull { x, list -> if (list.contains('#')) null else x.toLong() }
}

private fun Point.adjustCoords(emptyRows: List<Long>, emptyColumns: List<Long>, expansion: Long): Point {
    val rowsToAdd = emptyRows.count { it < y } * (expansion - 1)
    val columnsToAdd = emptyColumns.count { it < x } * (expansion - 1)
    return Point(x + columnsToAdd, y + rowsToAdd)
}