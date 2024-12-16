package com.gilpereda.aoc2024.day16

import com.gilpereda.aoc2024.BaseTest
import com.gilpereda.aoc2024.Executable

class Day16Test : BaseTest() {
    override val example: String =
        """
        #################
        #...#...#...#..E#
        #.#.#.#.#.#.#.#.#
        #.#.#.#...#...#.#
        #.#.#.#.###.#.#.#
        #...#.#.#.....#.#
        #.#.#.#.#.#####.#
        #.#...#.#.#.....#
        #.#.#####.#.###.#
        #.#.#.......#...#
        #.#.###.#####.###
        #.#.#...#.....#.#
        #.#.#.#####.###.#
        #.#.#.........#.#
        #.#.#.#########.#
        #S#.............#
        #################
        """.trimIndent()

    override val resultExample1: String = "11048"

    override val resultReal1: String = "90440"

    override val resultExample2: String = "64"

    override val resultReal2: String = ""

    override val input: String = "/day16/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask
}
