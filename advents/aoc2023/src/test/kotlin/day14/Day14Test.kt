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

    override val result1: String = "136"

    override val result2: String = "64"

    override val input: String = "/day14/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask
}