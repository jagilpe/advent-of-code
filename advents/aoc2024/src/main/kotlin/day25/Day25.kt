package com.gilpereda.aoc2024.day25

/**
 * 1914 too low
 */
fun firstTask(input: Sequence<String>): String {
    val elements =
        input
            .joinToString("\n")
            .split("\n\n")
            .map(::parseElement)

    val keys = elements.filterIsInstance<Key>()
    val locks = elements.filterIsInstance<Lock>()

    return keys
        .asSequence()
        .flatMap { key ->
            locks.map { lock -> key to lock }
        }.count { (key, lock) -> lock.matches(key) }
        .toString()
}

fun secondTask(input: Sequence<String>): String = TODO()

fun parseElement(string: String): Element {
    val lines = string.split("\n")
    return lines.fold(Key() as Element) { acc, line ->
        val columns =
            line.mapIndexed { index, cell ->
                if (cell == '#') {
                    (acc.columns.getOrElse(index, { 0 })) + 1
                } else {
                    acc.columns.getOrElse(index, { 0 })
                }
            }
        if (lines.first() == "#####") {
            Lock(columns)
        } else {
            Key(columns)
        }
    }
}

sealed interface Element {
    val columns: List<Int>
}

data class Key(
    override val columns: List<Int> = emptyList(),
) : Element

data class Lock(
    override val columns: List<Int> = emptyList(),
) : Element {
    fun matches(key: Key): Boolean =
        columns
            .zip(key.columns)
            .all { (one, other) -> one + other <= 7 }
}
