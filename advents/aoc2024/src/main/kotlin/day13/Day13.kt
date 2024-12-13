package com.gilpereda.aoc2024.day13

import com.gilpereda.adventofcode.commons.geometry.Point
import java.math.BigInteger

private val BUTTON_A_REGEX = "Button A: X\\+(\\d+), Y\\+(\\d+)".toRegex()
private val BUTTON_B_REGEX = "Button B: X\\+(\\d+), Y\\+(\\d+)".toRegex()
private val PRIZE_REGEX = "Prize: X=(\\d+), Y=(\\d+)".toRegex()

fun firstTask(input: Sequence<String>): String = input.resolve(0)

fun secondTask(input: Sequence<String>): String = input.resolve(10000000000000)

private fun Sequence<String>.resolve(add: Long): String =
    joinToString(separator = "\n")
        .split("\n\n")
        .map(::parseGame)
        .sumOf { it.result(add) }
        .toString()

private fun parseGame(lines: String): Game {
    val (buttonALine, buttonBLine, prizeLine) = lines.split("\n")
    val buttonA = BUTTON_A_REGEX.find(buttonALine)!!.destructured.let { (x, y) -> Point.from(x.toInt(), y.toInt()) }
    val buttonB = BUTTON_B_REGEX.find(buttonBLine)!!.destructured.let { (x, y) -> Point.from(x.toInt(), y.toInt()) }
    val prize = PRIZE_REGEX.find(prizeLine)!!.destructured.let { (x, y) -> Point.from(x.toInt(), y.toInt()) }
    return Game(buttonA, buttonB, prize)
}

private val THREE = BigInteger.valueOf(3L)
private val ZERO = BigInteger.valueOf(0L)

data class Game(
    val buttonA: Point,
    val buttonB: Point,
    val prize: Point,
) {
    fun result(add: Long = 0): BigInteger {
        val ax = BigInteger.valueOf(buttonA.x.toLong())
        val ay = BigInteger.valueOf(buttonA.y.toLong())
        val bx = BigInteger.valueOf(buttonB.x.toLong())
        val by = BigInteger.valueOf(buttonB.y.toLong())
        val px = BigInteger.valueOf(prize.x.toLong() + add)
        val py = BigInteger.valueOf(prize.y.toLong() + add)
        val xa = (bx * py - by * px)
        val xb = (bx * ay - by * ax)
        val ya = (ax * py - ay * px)
        val yb = (ax * by - ay * bx)
        return if (xa.mod(xb.abs()).equals(ZERO) && ya.mod(yb.abs()).equals(ZERO)) {
            ((xa / xb * THREE) + (ya / yb))
        } else {
            ZERO
        }
    }
}
