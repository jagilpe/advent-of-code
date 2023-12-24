package com.gilpereda.aoc2022.day24

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable

class Day24Test : BaseTest() {
    override val example: String = """
        19, 13, 30 @ -2,  1, -2
        18, 19, 22 @ -1, -1, -2
        20, 25, 34 @ -2, -2, -4
        12, 31, 28 @ -1, -2, -1
        20, 19, 15 @  1, -5, -3
    """.trimIndent()

    override val resultExample1: String = "2"

    override val resultExample2: String
        get() = TODO()

    override val resultReal1: String = ""

    override val resultReal2: String = ""

    override val input: String = "/day24/input"

    override fun runExample1(sequence: Sequence<String>): String =
        firstTask(sequence, 7, 27)

    override fun runReal1(sequence: Sequence<String>): String =
        firstTask(sequence, 200000000000000, 400000000000000)

    override val run2: Executable = ::secondTask
}