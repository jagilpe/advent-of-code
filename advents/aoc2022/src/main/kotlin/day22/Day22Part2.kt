package com.gilpereda.aoc2022.day22

fun secondTask(input: Sequence<String>): String = TODO()



fun parseSides(input: List<String>): Map<SideId, Side> = TODO()

data class Side(
    val rocksAt : List<Point>
)

data class Point(val x: Int, val y: Int)

infix fun Int.x(other: Int): Point = TODO()

enum class SideId {
    A,
    B,
    C,
    D,
    E,
    F,
}
