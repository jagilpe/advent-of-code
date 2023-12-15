package com.gilpereda.aoc2022.day15

fun firstTask(input: Sequence<String>): String =
    input.first().split(",")
        .map { Code(it) }
        .sumOf { it.result() }
        .toString()

fun secondTask(input: Sequence<String>): String {
    val boxMap = input.first().split(",")
        .fold(BoxMap()) { acc, next ->
            acc.process(next)
        }
    return boxMap.focusingPower().toString()
}

data class Code(
    val code: String,
) {
    fun result(): Int = hash(code)
}

fun hash(code: String): Int =
    code.fold(0) { acc, c -> ((c.code + acc) * 17) % 256 }

data class Lens(
    val label: String,
    val focalLength: Int,
)

class Box {
    private val lenses: MutableList<Lens> = mutableListOf()
    fun add(lens: Lens) =
        lenses.firstOrNull { it.label == lens.label }
            ?.let { found -> lenses.replaceAll { if (it == found) lens else it } }
            ?: lenses.add(lens)

    fun remove(label: String) =
        lenses.removeIf { it.label == label }

    fun focusingPower(box: Int): Int =
        lenses.foldIndexed(0) { index, acc, next ->
            acc + ((box + 1) * (index + 1) * next.focalLength)
        }
}

class BoxMap {
    private val boxes = List(256) { Box() }

    fun focusingPower(): Long =
        boxes.foldIndexed(0) { boxNum, acc, box ->
            acc + box.focusingPower(boxNum)
        }

    fun process(lens: String): BoxMap =
        when {
            lens.endsWith("-") -> remove(lens)
            lens.contains("=") -> add(lens)
            else -> throw IllegalArgumentException("Not valid input")
        }

    private fun remove(lens: String): BoxMap {
        val label = lens.removeSuffix("-")
        val box = hash(label)
        boxes[box].remove(label)
        return this
    }

    private fun add(lens: String): BoxMap {
        val (label, focalLength) = lens.split("=")
        val box = hash(label)
        return add(box, Lens(label, focalLength.toInt()))
    }

    fun add(box: Int, lens: Lens): BoxMap {
        boxes[box].add(lens)
        return this
    }
}