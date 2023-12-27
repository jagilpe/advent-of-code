package com.gilpereda.aoc2022.day08

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day08Test : BaseTest() {
    override val example: String = """
        RL

        AAA = (BBB, CCC)
        BBB = (DDD, EEE)
        CCC = (ZZZ, GGG)
        DDD = (DDD, DDD)
        EEE = (EEE, EEE)
        GGG = (GGG, GGG)
        ZZZ = (ZZZ, ZZZ)
    """.trimIndent()

    override val example2: String = """
        LR

        11A = (11B, XXX)
        11B = (XXX, 11Z)
        11Z = (11B, XXX)
        22A = (22B, XXX)
        22B = (22C, 22C)
        22C = (22Z, 22Z)
        22Z = (22B, 22B)
        XXX = (XXX, XXX)
    """.trimIndent()

    override val resultExample1: String = "2"

    override val resultExample2: String = "6"

    override val resultReal1: String = "16697"

    override val resultReal2: String = "10668805667831"

    override val input: String = "/day08/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask

    @Test
    fun `should parse the positions`() {
        val expectedPosition = Position(
            "MQB",
            "FDF",
            "VJN"
        )
        assertThat("MQB = (FDF, VJN)".parsed()).isEqualTo("MQB" to expectedPosition)
    }
}