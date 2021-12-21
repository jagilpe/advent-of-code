package com.gilpereda.adventsofcode.adventsofcode2021.day20

import com.gilpereda.adventsofcode.adventsofcode2021.Executable
import com.gilpereda.adventsofcode.adventsofcode2021.day16.binaryToInt


val part1: Executable = {
    val (algorithmTable, initialImage) = parseInput(it.toList())
    generateSequence(initialImage) { it.next(algorithmTable) }.take(3).last().lightPixels.toString()
}

val part2: Executable = {
    val (algorithmTable, initialImage) = parseInput(it.toList())
    generateSequence(initialImage) { it.next(algorithmTable) }.take(51).last().lightPixels.toString()
}

typealias AlgorithmTable = List<Boolean>

class ImageMap(val map: List<List<Boolean>>, val width: Int, val height: Int, val background: Boolean = false) {

    fun next(algorithmTable: AlgorithmTable): ImageMap {
        val nextBackground = when (background) {
            true -> algorithmTable[511]
            false -> algorithmTable[0]
        }
        val newWidth = width + 2
        val newHeight = height + 2
        val newMap = (0..newHeight).asSequence().map { y ->
            (0..newWidth).asSequence().map { x -> newPixel(algorithmTable, x - 1, y - 1) }.toList()
        }.toList()

        return ImageMap(newMap, newWidth, newHeight, nextBackground)
    }

    override fun toString(): String = """
${map.map { row -> row.map { if (it) '#' else '.' }.joinToString("") }.joinToString("\n")}
    """.trimIndent()

    val lightPixels: Int
        get() = map.flatten().count { it }

    private fun newPixel(algorithmTable: AlgorithmTable, x: Int, y: Int): Boolean {
        val cellValue = listOf(
            -1 to -1,
            -1 to 0,
            -1 to 1,
            0 to -1,
            0 to 0,
            0 to 1,
            1 to -1,
            1 to 0,
            1 to 1,
        ).map { (incY, incX) -> if (pixel(x + incX, y + incY)) '1' else '0' }
            .joinToString("")
            .binaryToInt
        return algorithmTable[cellValue]
    }

    private fun pixel(x: Int, y: Int): Boolean =
        when {
            x < 0 || y < 0 || x >= width || y >= height -> background
            else -> map[y][x]
        }
}

fun parseInput(input: List<String>): Pair<AlgorithmTable, ImageMap> {
    val algorithmTable: AlgorithmTable = parseRow(input.first())

    assert(algorithmTable.size == 512)

    val imageMap = parseImage(input.drop(2))
    return Pair(algorithmTable, imageMap)
}

private fun parseRow(row: String) = row.map {
    when (it) {
        '.' -> false
        '#' -> true
        else -> throw Exception("Illegal character $it")
    }
}

private fun parseImage(input: List<String>): ImageMap {
    val map = input.map(::parseRow).toList()
    val width = map.first().size
    assert(map.all { it.size == width })
    val height = map.size

    return ImageMap(map, width, height)
}
