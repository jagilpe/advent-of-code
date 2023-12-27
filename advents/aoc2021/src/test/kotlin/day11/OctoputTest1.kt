package com.gilpereda.adventsofcode.adventsofcode2021.day11

import com.gilpereda.adventsofcode.adventsofcode2021.BaseTest
import org.junit.jupiter.api.Test

class OctoputTest1 : BaseTest() {
    override val example: String = """
        5483143223
        2745854711
        5264556173
        6141336146
        6357385478
        4167524645
        2176841721
        6882881134
        4846848554
        5283751526
    """.trimIndent()

    override val result1: String = "1656"
    override val result2: String = "195"

    override val input: String = "/day11/input.txt"

    override val run1: (Sequence<String>) -> String = ::octopus
    override val run2: (Sequence<String>) -> String = ::synchonized

}