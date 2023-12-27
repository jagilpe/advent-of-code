package com.gilpereda.aoc2022.day02

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable

class Day02Test : BaseTest() {
    override val example: String = """
        Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
        Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue
        Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red
        Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red
        Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green
    """.trimIndent()

    override val resultExample1: String = "8"

    override val resultExample2: String = "2286"

    override val resultReal1: String = "2207"

    override val resultReal2: String = "62241"

    override val input: String = "/day02/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask
}