package com.gilpereda.aoc2024.day13

import com.gilpereda.aoc2024.BaseTest
import com.gilpereda.aoc2024.Executable

class Day13Test : BaseTest() {
    override val example: String =
        """
        Button A: X+94, Y+34
        Button B: X+22, Y+67
        Prize: X=8400, Y=5400

        Button A: X+26, Y+66
        Button B: X+67, Y+21
        Prize: X=12748, Y=12176

        Button A: X+17, Y+86
        Button B: X+84, Y+37
        Prize: X=7870, Y=6450

        Button A: X+69, Y+23
        Button B: X+27, Y+71
        Prize: X=18641, Y=10279
        """.trimIndent()

    override val resultExample1: String = "480"

    override val resultReal1: String = "28753"

    override val resultExample2: String = "875318608908"

    override val resultReal2: String = "102718967795500"

    override val input: String = "/day13/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask
}
