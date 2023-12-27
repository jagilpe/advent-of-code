package com.gilpereda.aoc2022.day08

val POSITION_REGEX = "([0-9A-Z]{3}) = \\(([0-9A-Z]{3}), ([0-9A-Z]{3})\\)".toRegex()

/*
 * 6250000000000 too low
 * 114200000
 */
fun firstTask(input: Sequence<String>): String {
    val (directions, positionsMap) = input.toList().parsed()
    tailrec fun go(acc: Long, rest: List<Direction>, current: String): Long =
        if(current == "ZZZ") {
            acc
        } else {
            if (rest.isNotEmpty()) {
                go(acc + 1, rest.drop(1), positionsMap[current]!!.next(rest.first()))
            } else {
                go(acc, directions, current)
            }
        }
    return go(0, directions, "AAA").toString()
}

fun secondTask(input: Sequence<String>): String {
    val (directions, positionsMap) = input.toList().parsed()


    tailrec fun go(acc: Long, rest: List<Direction>, current: String): Pair<Long, String> =
        when {
            rest.isEmpty() -> go(acc, directions, current)
            else -> {
                val next = positionsMap[current]!!.next(rest.first())
                if (next.endsWith('Z')) {
                    (acc + 1) to next
                } else {
                    go(acc + 1, rest.drop(1), next)
                }
            }
        }

    val cycles = positionsMap.initial()
        .map { go(0, directions, it) }


    return findLCMOfListOfNumbers(cycles.map { it.first }).toString()
//    val (directions, positionsMap) = input.toList().parsed()
//    tailrec fun go(acc: Long, rest: List<Direction>, current: List<String>): Long =
//        if (current.finished()) {
//            acc
//        } else {
//            if (rest.isNotEmpty()) {
//                if (acc % 10_000_000L == 0L) println("reached: $acc")
//                go(acc + 1, rest.drop(1), current.next(rest.first(), positionsMap))
//            } else {
//                go(acc, directions, current)
//            }
//        }
//
//    val initial = positionsMap.initial()
//    return go(0, directions, initial).toString()
}

fun findLCM(a: Long, b: Long): Long {
    val larger = if (a > b) a else b
    val maxLcm = a * b
    var lcm = larger
    while (lcm <= maxLcm) {
        if (lcm % a == 0L && lcm % b == 0L) {
            return lcm
        }
        lcm += larger
    }
    return maxLcm
}

fun findLCMOfListOfNumbers(numbers: List<Long>): Long {
    var result = numbers[0]
    for (i in 1 until numbers.size) {
        result = findLCM(result, numbers[i])
    }
    return result
}

fun Map<String, Position>.initial(): List<String> =
    keys.filter { it.endsWith("A") }

fun List<String>.next(direction: Direction, positionsMap: Map<String, Position>): List<String> =
    map { position -> positionsMap[position]!!.next(direction) }

fun List<String>.parsed(): Pair<List<Direction>, Map<String, Position>> {
    val directions = first().map { Direction.from(it) }
    val postionsMap = drop(2)
        .associate { line -> line.parsed() }
    return directions to postionsMap
}

fun String.parsed(): Pair<String, Position> =
    POSITION_REGEX.find(this)?.destructured?.let { (name, left, right) ->
        name to Position(name, left, right)
    }!!

data class Position(
    val name: String,
    val left: String,
    val right: String,
) {
    fun next(direction: Direction): String =
        when (direction) {
            Direction.L -> left
            Direction.R -> right
        }
}

enum class Direction {
    L,
    R;

    companion object {
        fun from(char: Char): Direction = when (char) {
            'L' -> L
            'R' -> R
            else -> throw IllegalStateException("impossible")
        }
    }
}