package com.gilpereda.aoc2024.day20

import com.gilpereda.aoc2024.BaseTest
import com.gilpereda.aoc2024.Executable

class Day20Test : BaseTest() {
    override val example: String =
        """
        4,50
        ###############
        #...#...#.....#
        #.#.#.#.#.###.#
        #S#...#.#.#...#
        #######.#.#.###
        #######.#.#...#
        #######.#.###.#
        ###..E#...#...#
        ###.#######.###
        #...###...#...#
        #.#####.#.###.#
        #.#...#.#.#...#
        #.#.#.#.#.#.###
        #...#...#...###
        ###############
        """.trimIndent()

    override val resultExample1: String = "30"

    override val resultReal1: String = "1490"

    override val resultExample2: String = "285"

    override val resultReal2: String = ""

    override val input: String = "/day20/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask
}
