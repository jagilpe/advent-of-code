package com.gilpereda.adventsofcode.adventsofcode2021.day23

import com.gilpereda.adventsofcode.adventsofcode2021.Executable


typealias Cave = List<MutableList<out Cell>>

val part1: (Cave) -> Int = { TODO() }

val part2: Executable = { TODO() }

sealed interface Cell {
    val free: Boolean
    val amphipod: Amphipod?
}

enum class Amphipod(val consume: Int) {
    AMBER(1),
    BRONZE(10),
    COPPER(100),
    DESERT(1000)
}

object Wall : Cell {
    override val free: Boolean = false
    override val amphipod: Amphipod? = null
}

data class Hall(override val amphipod: Amphipod? = null) : Cell {
    override val free: Boolean = amphipod == null
}

object Door : Cell {
    override val free: Boolean = true
    override val amphipod: Amphipod? = null
}

data class Room(override val amphipod: Amphipod? = null, private val accepted: Amphipod) : Cell {
    override val free: Boolean = amphipod == null
}

data class Move(val cave: Cave, val cost: Int)

data class Point(val x: Int, val y: Int)

fun Cave.move(from: Point, to: Point): Move {
    val source = this[from.y][from.x]
    val dest = this[to.y][to.x]
    val amphipod = source.amphipod
    if (amphipod != null) {
        TODO()
    } else {
        throw Exception("Source cell $source is empty")
    }
}

val Cave.nextMoves: List<Move>
    get() = TODO()