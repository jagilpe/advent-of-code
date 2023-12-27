package com.gilpereda.aoc2022.day05

import java.lang.Exception


typealias Containers = Map<Int, String>

//val start = mapOf(
//    1 to "NZ",
//    2 to "DCM",
//    3 to "P",
//)

val start = mapOf(
    1 to "GWLJBRTD",
    2 to "CWS",
    3 to "MTZR",
    4 to "VPSHCTD",
    5 to "ZDLTPG",
    6 to "DCQJZRBF",
    7 to "RTFMJDBS",
    8 to "MVTBRHL",
    9 to "VSDPQ"
)

val extractRegex = "move ([0-9]+) from ([0-9]+) to ([0-9]+)".toRegex()

val Containers.result: String
    get() = map {(_, column) -> column.first()}.joinToString("")

fun firstTask(input: Sequence<String>): String =
    input.map(::parseMove)
        .fold(start) { acc, move ->
            move.move(acc)
        }
        .result

fun secondTask(input: Sequence<String>): String =
    TODO()

data class Move(
    val amount: Int,
    val from: Int,
    val to: Int,
) {
    fun move(containers: Containers): Containers {
        val packets = containers[from]!!.take(amount)
        return containers + mapOf(
            from to containers[from]!!.drop(amount),
            to to packets + containers[to]
        )
    }
}

fun parseMove(line: String): Move =
    extractRegex.find(line)?.destructured
        ?.let { (amount, from, to) -> Move(amount.toInt(), from.toInt(), to.toInt()) }
        ?: throw Exception("Cannot parse $line")