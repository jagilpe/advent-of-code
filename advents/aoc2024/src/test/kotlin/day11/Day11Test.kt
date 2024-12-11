package com.gilpereda.aoc2024.day11

import com.gilpereda.aoc2024.BaseTest
import com.gilpereda.aoc2024.Executable

class Day11Test : BaseTest() {
    override val example: String = "125 17"

    override val example2: String
        get() = TODO()

    override val resultExample1: String = "55312"

    override val resultReal1: String = "231278"

    override val resultExample2: String = ""

    override val resultReal2: String = ""

    override val input: String = "/day11/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask
}
