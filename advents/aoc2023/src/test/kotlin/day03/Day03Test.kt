package com.gilpereda.aoc2022.day03

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable

class Day03Test : BaseTest() {
    override val example: String = """
        467..114..
        ...*......
        ..35..633.
        ......#...
        617*......
        .....+.58.
        ..592.....
        ......755.
        ...${'$'}.*....
        .664.598..
    """.trimIndent()

    override val result1: String = "4361"

    override val result2: String = "467835"

    override val input: String = "/day03/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask
}