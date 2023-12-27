package com.gilpereda.aoc2022.day17

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable

class Day17Test : BaseTest() {
    override val example: String = """
        2413432311323
        3215453535623
        3255245654254
        3446585845452
        4546657867536
        1438598798454
        4457876987766
        3637877979653
        4654967986887
        4564679986453
        1224686865563
        2546548887735
        4322674655533
    """.trimIndent()

    override val resultExample1: String = "102"

    override val resultExample2: String = "94"

    override val resultReal1: String = "817"

    override val resultReal2: String = "0"

    override val input: String = "/day17/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask
}