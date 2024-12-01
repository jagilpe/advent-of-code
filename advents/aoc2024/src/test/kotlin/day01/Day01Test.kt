package com.gilpereda.aoc2024.day01

import com.gilpereda.aoc2024.BaseTest
import com.gilpereda.aoc2024.Executable

class Day01Test : BaseTest() {
    override val example: String = """
        3   4
        4   3
        2   5
        1   3
        3   9
        3   3
    """.trimIndent()

    override val resultExample1: String = "11"

    override val resultReal1: String = "1580061"

    override val resultExample2: String = "31"

    override val resultReal2: String = ""

    override val input: String = "/day01/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask
}