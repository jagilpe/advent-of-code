package com.gilpereda.aoc2024.day06

import com.gilpereda.aoc2024.BaseTest
import com.gilpereda.aoc2024.Executable

class Day06Test : BaseTest() {
    override val example: String =
        """
        ....#.....
        .........#
        ..........
        ..#.......
        .......#..
        ..........
        .#..^.....
        ........#.
        #.........
        ......#...
        """.trimIndent()

    override val resultExample1: String = "41"

    override val resultReal1: String = "5453"

    override val resultExample2: String = "6"

    override val resultReal2: String = "2188"

    override val input: String = "/day06/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask
}
