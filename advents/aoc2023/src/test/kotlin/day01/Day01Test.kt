package com.gilpereda.aoc2022.day01

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable

class Day01Test : BaseTest() {
    override val example: String = """
        1abc2
        pqr3stu8vwx
        a1b2c3d4e5f
        treb7uchet
    """.trimIndent()

    override val example2: String = """
        two1nine
        eightwothree
        abcone2threexyz
        xtwone3four
        4nineeightseven2
        zoneight234
        7pqrstsixteen
    """.trimIndent()

    override val result1: String = "142"

    override val result2: String = "281"

    override val input: String = "/day01/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask
}