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

    override val result1: String = "1134"
    override val result2: String = "15"

    override val input: String = "/day09/input.txt"

    override val run1: (Sequence<String>) -> String = ::findBasins
    override val run2: (Sequence<String>) -> String = ::calculateRisk
}