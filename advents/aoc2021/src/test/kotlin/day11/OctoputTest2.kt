package com.gilpereda.adventsofcode.adventsofcode2021.day11

import com.gilpereda.adventsofcode.adventsofcode2021.BaseTest
import org.junit.jupiter.api.Test

class OctoputTest2 : BaseTest() {
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

    override val result: String = "195"

    override val input: String = "/day11/input.txt"

    override val run: (Sequence<String>) -> String = ::synchonized

}