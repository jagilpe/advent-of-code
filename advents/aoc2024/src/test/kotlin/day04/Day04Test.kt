package com.gilpereda.aoc2024.day04

import com.gilpereda.aoc2024.BaseTest
import com.gilpereda.aoc2024.Executable

class Day04Test : BaseTest() {
    override val example: String =
        """
        MMMSXXMASM
        MSAMXMSMSA
        AMXSXMAAMM
        MSAMASMSMX
        XMASAMXAMM
        XXAMMXXAMA
        SMSMSASXSS
        SAXAMASAAA
        MAMMMXMMMM
        MXMXAXMASX
        """.trimIndent()

    override val resultExample1: String = "18"

    override val resultReal1: String = "2496"

    override val resultExample2: String = "9"

    override val resultReal2: String = "1967"

    override val input: String = "/day04/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask
}
