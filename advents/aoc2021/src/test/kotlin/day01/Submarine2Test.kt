package com.gilpereda.adventsofcode.adventsofcode2021.day01

import com.gilpereda.adventsofcode.adventsofcode2021.BaseTest

class Submarine2Test : BaseTest() {
    override val example = """
        199
        200
        208
        210
        200
        207
        240
        269
        260
        263
    """.trimIndent()

    override val result: String = "5"

    override val input: String = "/day01/input.txt"

    override val run: (Sequence<String>) -> String = ::countWindowed


}