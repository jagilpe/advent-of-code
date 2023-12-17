package com.gilpereda.aoc2022.day14

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable

class Day14Test : BaseTest() {
    override val example: String = """
        O....#....
        O.OO#....#
        .....##...
        OO.#O....O
        .O.....O#.
        O.#..O.#.#
        ..O..#O..O
        .......O..
        #....###..
        #OO..#....
    """.trimIndent()

    override val resultExample1: String = "136"

    override val resultExample2: String = "64"

    override val resultReal1: String = "112773"

    override val resultReal2: String = "98894"

    override val input: String = "/day14/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask
}