package com.gilpereda.adventsofcode.adventsofcode2021.day09

import com.gilpereda.adventsofcode.adventsofcode2021.BaseTest

class HeightsMapTest1 : BaseTest() {
    override val example: String = """
        2199943210
        3987894921
        9856789892
        8767896789
        9899965678
    """.trimIndent()

    override val result: String = "1134"

    override val input: String = "/day09/input.txt"

    override val run: (Sequence<String>) -> String = ::findBasins
}