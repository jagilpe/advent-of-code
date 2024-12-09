package com.gilpereda.aoc2024.day07

import com.gilpereda.aoc2024.BaseTest
import com.gilpereda.aoc2024.Executable

class Day07Test : BaseTest() {
    override val example: String =
        """
        190: 10 19
        3267: 81 40 27
        83: 17 5
        156: 15 6
        7290: 6 8 6 15
        161011: 16 10 13
        192: 17 8 14
        21037: 9 7 18 13
        292: 11 6 16 20
        """.trimIndent()

    override val resultExample1: String = "3749"

    override val resultReal1: String = "14711933466277"

    override val resultExample2: String = "11387"

    override val resultReal2: String = "286580387663654"

    override val input: String = "/day07/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask
}
