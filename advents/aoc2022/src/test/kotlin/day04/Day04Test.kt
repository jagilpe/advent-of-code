package com.gilpereda.aoc2022.day04

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable

class Day04Test : BaseTest() {
    override val example: String = """
        2-4,6-8
        2-3,4-5
        5-7,7-9
        2-8,3-7
        6-6,4-6
        2-6,4-8
    """.trimIndent()

    override val result1: String = "2"

    override val result2: String = "4"

    override val input: String = "/day04/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask
}