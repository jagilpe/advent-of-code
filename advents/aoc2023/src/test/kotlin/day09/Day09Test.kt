package com.gilpereda.aoc2022.day09

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable

class Day09Test : BaseTest() {
    override val example: String = """
        0 3 6 9 12 15
        1 3 6 10 15 21
        10 13 16 21 30 45
    """.trimIndent()

    override val resultExample1: String = "114"

    override val resultExample2: String = "2"

    override val resultReal1: String = "1974232246"

    override val resultReal2: String = "928"

    override val input: String = "/day09/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask
}