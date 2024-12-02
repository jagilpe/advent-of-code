package com.gilpereda.aoc2024.day02

import com.gilpereda.aoc2024.BaseTest
import com.gilpereda.aoc2024.Executable

class Day02Test : BaseTest() {
    override val example: String =
        """
        7 6 4 2 1
        1 2 7 8 9
        9 7 6 2 1
        1 3 2 4 5
        8 6 4 4 1
        1 3 6 7 9
        """.trimIndent()

    override val resultExample1: String = "2"

    override val resultReal1: String = "598"

    override val resultExample2: String = "4"

    override val resultReal2: String = "634"

    override val input: String = "/day02/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask
}
