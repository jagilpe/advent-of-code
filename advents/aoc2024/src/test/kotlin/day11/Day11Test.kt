package com.gilpereda.aoc2024.day11

import com.gilpereda.aoc2024.BaseTest
import com.gilpereda.aoc2024.Executable

class Day11Test : BaseTest() {
    override val example: String = "125 17"

    override val resultExample1: String = "55312"

    override val resultReal1: String = "231278"

    override val resultExample2: String = "65601038650482"

    override val resultReal2: String = "274229228071551"

    override val input: String = "/day11/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask
}
