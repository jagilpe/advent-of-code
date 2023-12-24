package com.gilpereda.aoc2022.day23

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable

class Day23Test : BaseTest() {
    override val example: String = """
        #S#####################
        #.......#########...###
        #######.#########.#.###
        ###.....#.>X>.###.#.###
        ###v#####.#v#.###.#.###
        ###X>...#.#.#.....#...#
        ###v###.#.#.#########.#
        ###...#.#.#.......#...#
        #####.#.#.#######.#.###
        #.....#.#.#.......#...#
        #.#####.#.#.#########v#
        #.#...#...#...###...>X#
        #.#.#v#######v###.###v#
        #...#X>.#...>X>.#.###.#
        #####v#.#.###v#.#.###.#
        #.....#...#...#.#.#...#
        #.#########.###.#.#.###
        #...###...#...#...#.###
        ###.###.#.###v#####v###
        #...#...#.#.>X>.#.>X###
        #.###.###.#.###.#.#v###
        #.....###...###...#...#
        #####################O#
    """.trimIndent()

    override val resultExample1: String = "94"

    override val resultExample2: String = "154"

    override val resultReal1: String = "2314"

    override val resultReal2: String = ""

    override val input: String = "/day23/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask
}