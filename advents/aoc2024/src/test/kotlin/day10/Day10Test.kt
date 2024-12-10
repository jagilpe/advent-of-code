package com.gilpereda.aoc2024.day10

import com.gilpereda.aoc2024.BaseTest
import com.gilpereda.aoc2024.Executable

class Day10Test : BaseTest() {
    override val example: String =
        """
        89010123
        78121874
        87430965
        96549874
        45678903
        32019012
        01329801
        10456732
        """.trimIndent()

    override val resultExample1: String = "36"

    override val resultReal1: String = "593"

    override val resultExample2: String = "81"

    override val resultReal2: String = ""

    override val input: String = "/day10/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask
}
