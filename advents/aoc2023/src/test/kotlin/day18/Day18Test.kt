package com.gilpereda.aoc2022.day18

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable

class Day18Test : BaseTest() {
    override val example: String = """
        R 6 (#70c710)
        D 5 (#0dc571)
        L 2 (#5713f0)
        D 2 (#d2c081)
        R 2 (#59c680)
        D 2 (#411b91)
        L 5 (#8ceee2)
        U 2 (#caa173)
        L 1 (#1b58a2)
        U 2 (#caa171)
        R 2 (#7807d2)
        U 3 (#a77fa3)
        L 2 (#015232)
        U 2 (#7a21e3)
    """.trimIndent()

    override val resultExample1: String = "62"

    override val resultExample2: String = "952408144115"

    override val resultReal1: String = "47045"

    override val resultReal2: String = "952408144115"

    override val input: String = "/day18/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask
}