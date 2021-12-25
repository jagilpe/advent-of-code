package com.gilpereda.adventsofcode.adventsofcode2021.day25

import com.gilpereda.adventsofcode.adventsofcode2021.Executable


val part1: Executable = { input ->
    val initial = input.parseInput()
    generateSequence(Triple(2, initial.next(), initial)) { (count, seaMap, _) ->
        Triple(count + 1, seaMap.next(), seaMap)
    }
        .takeWhile { (count, current, previous) ->
            current != previous
        }
        .last().first.toString()
}

val part2: Executable = { TODO() }

data class SeaMap(val seaMap: List<List<Cell>>, val width: Int, val height: Int) {
    override fun toString(): String = seaMap.joinToString(separator = "\n", prefix = "\n", postfix = "\n") { row -> row.joinToString("") }
}

sealed interface Cell

object Empty : Cell {
    override fun toString(): String = "."
}

object East : Cell {
    override fun toString(): String = ">"
}

object South : Cell {
    override fun toString(): String = "v"
}

fun Sequence<String>.parseInput(): SeaMap {
    val map = this.map { parseLine(it) }.toList()
    val height = map.size
    val width = map.first().size
    assert(map.all { it.size == width })
    return SeaMap(map, width, height)
}

private fun parseLine(line: String): List<Cell> =
    line.map {
        when (it) {
            '.' -> Empty
            '>' -> East
            'v' -> South
            else -> throw Exception("Illegal character $it")
        }
    }

fun SeaMap.next(): SeaMap =
    nextEast().nextSouth()


fun SeaMap.nextEast(): SeaMap = copy(seaMap = seaMap.map { it.nextLine() })

fun SeaMap.nextSouth(): SeaMap =
    (0 until height).map { y ->
        (0 until width).map { x ->
            val south = if (y == height - 1) seaMap[0][x] else seaMap[y + 1][x]
            val north = if (y == 0) seaMap[height - 1][x] else seaMap[y - 1][x]
            val cell = seaMap[y][x]
            when (cell) {
                Empty -> if (north == South) South else cell
                South -> if (south == Empty) Empty else cell
                else -> cell
            }
        }
    }.let { this.copy(seaMap = it)}

fun List<Cell>.nextLine(): List<Cell> =
    mapIndexed { x, cell ->
        val west = if (x == 0) last() else this[x - 1]
        val east = if (x == size - 1) first() else this[x + 1]
        val next = when (cell) {
            Empty -> if (west == East) East else cell
            East -> if (east == Empty) Empty else cell
            else -> cell
        }
        next
    }
