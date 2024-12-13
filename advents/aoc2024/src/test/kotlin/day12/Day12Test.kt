package com.gilpereda.aoc2024.day12

import com.gilpereda.aoc2024.BaseTest
import com.gilpereda.aoc2024.Executable

class Day12Test : BaseTest() {
    override val example: String =
        """
        RRRRIICCFF
        RRRRIICCCF
        VVRRRCCFFF
        VVRCCCJFFF
        VVVVCJJCFE
        VVIVCCJJEE
        VVIIICJJEE
        MIIIIIJJEE
        MIIISIJEEE
        MMMISSJEEE
        """.trimIndent()

    override val resultExample1: String = "1930"

    override val resultReal1: String = "1461806"

    override val resultExample2: String = "1206"

    override val resultReal2: String = ""

    override val input: String = "/day12/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask
}
