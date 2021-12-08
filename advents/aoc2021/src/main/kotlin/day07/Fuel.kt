package com.gilpereda.adventsofcode.adventsofcode2021.day07

import java.util.Collections.max
import kotlin.math.abs

fun consumedFuel(input: Sequence<String>): String {
    val positions = input.first().split(",").map(String::toInt)
    return positions.asSequence()
        .map { alignment -> positions.sumOf { abs(alignment - it) } }
        .toList().minOrNull()!!.toString()
}

fun consumedFuel2(input: Sequence<String>): String {
    val positions = input.first().split(",").map(String::toInt)
    val max = max(positions)
    return (0..max).asSequence()
        .map { alignment -> positions.sumOf {
            getConsume(abs(alignment - it)) }
        }
        .toList().minOrNull()!!.toString()
}

val consumes = mutableMapOf<Int, Int>()
fun getConsume(movement: Int): Int =
    consumes.computeIfAbsent(movement) { (1..movement).sum() }
