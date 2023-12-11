package com.gilpereda.aoc2022.day11

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable

class Day11Test : BaseTest() {
    override val example: String = """
        ...#......
        .......#..
        #.........
        ..........
        ......#...
        .#........
        .........#
        ..........
        .......#..
        #...#.....
    """.trimIndent()

    override val result1: String = "374"

    override val result2: String = "1030"

    override val input: String = "/day11/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask
}