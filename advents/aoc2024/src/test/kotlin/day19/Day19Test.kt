package com.gilpereda.aoc2024.day19

import com.gilpereda.aoc2024.BaseTest
import com.gilpereda.aoc2024.Executable

class Day19Test : BaseTest() {
    override val example: String =
        """
        r, wr, b, g, bwu, rb, gb, br

        brwrr
        bggr
        gbbr
        rrbgbr
        ubwu
        bwurrg
        brgr
        bbrgwb
        """.trimIndent()

    override val resultExample1: String = "6"

    override val resultReal1: String = "371"

    override val resultExample2: String = "16"

    override val resultReal2: String = "650354687260341"

    override val input: String = "/day19/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask
}
