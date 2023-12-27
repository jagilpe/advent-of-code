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

    override val resultExample1: String = "374"

    override val resultExample2: String = "1030"

    override val resultReal1: String = "9799681"

    override val resultReal2: String = "513171773355"

    override val input: String = "/day11/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask
}