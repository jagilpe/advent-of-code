package com.gilpereda.aoc2022.day10

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable

class Day10Test : BaseTest() {
    override val example: String = """
        F
        ..F7.
        .FJ|.
        SJ.L7
        |F--J
        LJ...
    """.trimIndent()

    override val example2: String = """
        7
        FF7FSF7F7F7F7F7F---7
        L|LJ||||||||||||F--J
        FL-7LJLJ||||||LJL-77
        F--JF--7||LJLJ7F7FJ-
        L---JF-JLJ.||-FJLJJ7
        |F|F-JF---7F7-L7L|7|
        |FFJF7L7F-JF7|JL---7
        7-L-JL7||F7|L7F-7F7|
        L.L7LFJ|||||FJL7||LJ
        L7JLJL-JLJLJL--JLJ.L
    """.trimIndent()

    override val resultExample1: String = "8"

    override val resultExample2: String = "10"

    override val resultReal1: String = "6682"

    override val resultReal2: String = "353"

    override val input: String = "/day10/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask
}