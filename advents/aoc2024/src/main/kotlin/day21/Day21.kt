package com.gilpereda.aoc2024.day21

import com.gilpereda.adventofcode.commons.geometry.Orientation
import com.gilpereda.adventofcode.commons.geometry.Point

fun firstTask(input: Sequence<String>): String {
    val cache = Cache()
    return input
        .map { Code(it, cache) }
        .sumOf { it.result(3) }
        .toString()
}

fun secondTask(input: Sequence<String>): String = TODO()

class Code(
    private val code: String,
    private val cache: Cache,
) {
    private val robotLevelActivated = mutableSetOf<Int>()
    private val numericPad =
        mapOf(
            '7' to Point.from(0, 0),
            '8' to Point.from(1, 0),
            '9' to Point.from(2, 0),
            '6' to Point.from(0, 1),
            '5' to Point.from(1, 1),
            '4' to Point.from(2, 1),
            '3' to Point.from(0, 2),
            '2' to Point.from(1, 2),
            '1' to Point.from(2, 2),
            '0' to Point.from(1, 3),
            'A' to Point.from(2, 3),
        )

    private val directionalKeyPad =
        mapOf(
            Button.from(Orientation.NORTH) to Point.from(1, 0),
            ActivateButton to Point.from(2, 0),
            Button.from(Orientation.WEST) to Point.from(0, 1),
            Button.from(Orientation.SOUTH) to Point.from(1, 1),
            Button.from(Orientation.EAST) to Point.from(2, 1),
        )

    fun result(levels: Int): Long {
        val complexity = complexity(levels)
        val number = code.drop(1).dropLast(1).toLong()
        return number * complexity
    }

    private fun complexity(levels: Int): Long {
        val numericPadMoves =
            findPathsInNumericPad(code)
                .filter { dump(it) == "<A^A>^^AvvvA" }
//                .filter { dump(it) == "<A^A^>^AvvvA" }
//                .filter { dump(it) == "<A^A^^>AvvvA" }

        var counter = 0
        println("paths: ${numericPadMoves.size}")
        val paths =
            numericPadMoves.flatMap {
                println("path: ${counter++}")
                deepFindPathsInDirectionalPad(it, levels)
            }

        val expected = "v<<A>>^A<A>AvA<^AA>A<vAAA>^A"
        val pathStrings = paths.map { dump(it) }.sorted()
        val found = pathStrings.firstOrNull { it.startsWith(expected.substring(0..12)) }

        return paths.minByOrNull { it.size }?.size?.toLong() ?: 0L
    }

    private fun deepFindPathsInDirectionalPad(
        list: List<Button>,
        level: Int,
    ): Set<List<Button>> {
        tailrec fun go(
            open: List<List<Button>>,
            acc: Set<List<Button>>,
        ): Set<List<Button>> =
            if (open.isEmpty()) {
                acc
            } else {
                println("open: ${open.size}")
                val (from, to) = open.first()
                if (from == to) {
                    go(open.drop(1), acc)
                } else {
                    val cacheEntry = Cache.Entry(from, to, level)
                    val cached = cache[cacheEntry]
                    val nextMoves =
                        if (cached != null) {
                            println("cache hit")
                            cached
                        } else {
                            if (level == 0) {
                                findPathInDirectionalPad(from, to)
                            } else {
                                val nextLevel = level - 1
                                val cacheResult = nextLevel in robotLevelActivated
                                findPathInDirectionalPad(from, to)
                                    .flatMap {
                                        deepFindPathsInDirectionalPad(listOf(from, to), nextLevel)
                                    }.also {
                                        if (cacheResult) {
                                            cache[cacheEntry] = it
                                        }
                                    }
                            }
                        }
                    val newAcc = acc.flatMap { path -> nextMoves.map { path + it } }.toSet()
                    go(open.drop(1), newAcc)
                }
            }
        val windowedList =
            if (level in robotLevelActivated) {
                list.windowed(2)
            } else {
                robotLevelActivated.add(level)
                listOf(ActivateButton, *list.toTypedArray()).windowed(2)
            }
        return go(windowedList, setOf(emptyList()))
    }

    private fun findPathsInNumericPad(code: String): List<List<Button>> {
        tailrec fun go(
            open: List<List<Char>>,
            acc: List<List<Button>>,
        ): List<List<Button>> =
            if (open.isEmpty()) {
                acc
            } else {
                val next = open.first()
                val newMoves = findPathInNumericPad(next)
                val newAcc = acc.flatMap { path -> newMoves.map { path + it } }
                go(open.drop(1), newAcc)
            }
        return go("A$code".toList().windowed(2), listOf(emptyList()))
    }

    private fun findPathInNumericPad(pair: List<Char>): List<List<Button>> =
        pair.let { (from, to) ->
            val fromPoint = numericPad.getValue(from)
            val toPoint = numericPad.getValue(to)
            fromPoint.movementsTo(toPoint).map { path -> path.map { Button.from(it) } + ActivateButton }
        }

    private fun findPathsInDirectionalPad(list: List<Button>): List<List<Button>> {
        tailrec fun go(
            open: List<List<Button>>,
            acc: List<List<Button>>,
        ): List<List<Button>> =
            if (open.isEmpty()) {
                acc
            } else {
                val (from, to) = open.first()
                val newMoves = findPathInDirectionalPad(from, to).ifEmpty { listOf(emptyList()) }
                val newAcc = acc.flatMap { path -> newMoves.map { path + it } }
                go(open.drop(1), newAcc)
            }
        return go(list.toList().windowed(2), listOf(emptyList()))
    }

    private fun findPathInDirectionalPad(
        from: Button,
        to: Button,
    ): Set<List<Button>> {
        val fromPoint = directionalKeyPad.getValue(from)
        val fromTo = directionalKeyPad.getValue(to)
        return (
            fromPoint
                .movementsTo(fromTo)
                .ifEmpty { listOf(emptyList()) }
        ).map { path -> path.map { Button.from(it) } + ActivateButton }.toSet()
    }

    private fun dump(instructions: List<Button>): String = instructions.joinToString("")
}

class Cache {
    private val cache = mutableMapOf<Entry, List<List<Button>>>()

    operator fun get(entry: Entry): List<List<Button>>? = cache[entry]

    operator fun set(
        entry: Entry,
        value: List<List<Button>>,
    ) {
        cache[entry] = value
    }

    data class Entry(
        val from: Button,
        val to: Button,
        val level: Int,
    )
}

sealed interface Button {
    companion object {
        private val buttons = mutableMapOf<Orientation, Button>()

        fun from(orientation: Orientation): Button = buttons.computeIfAbsent(orientation) { DirectionalButton(it) }
    }
}

data class DirectionalButton(
    val orientation: Orientation,
) : Button {
    override fun toString(): String = orientation.toString()
}

data object ActivateButton : Button {
    override fun toString(): String = "A"
}
