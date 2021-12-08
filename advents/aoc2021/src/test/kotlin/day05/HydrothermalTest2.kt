package com.gilpereda.adventsofcode.adventsofcode2021.day05

import com.gilpereda.adventsofcode.adventsofcode2021.BaseTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class HydrothermalTest2 : BaseTest() {
    override val example: String = """
        0,9 -> 5,9
        8,0 -> 0,8
        9,4 -> 3,4
        2,2 -> 2,1
        7,0 -> 7,4
        6,4 -> 2,0
        0,9 -> 2,9
        3,4 -> 1,4
        0,0 -> 8,8
        5,5 -> 8,2
    """.trimIndent()

    override val result: String = "12"

    override val input: String = "/day05/input.txt"

    override val run: (Sequence<String>) -> String = ::hydrothermal2
}