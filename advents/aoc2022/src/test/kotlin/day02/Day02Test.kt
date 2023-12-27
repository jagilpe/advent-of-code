package com.gilpereda.aoc2022.day02

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable

class Day02Test : BaseTest() {
    override val example: String = """
        A Y
        B X
        C Z
    """.trimIndent()

    override val result1: String = "15"

    override val result2: String = "12"

    override val input: String = "/day02/input"

    override val run1: Executable = ::rockPaperScissors

    override val run2: Executable = ::rockPaperScissors2
}