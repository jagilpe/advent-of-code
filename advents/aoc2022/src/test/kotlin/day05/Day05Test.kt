package com.gilpereda.aoc2022.day05

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day05Test : BaseTest() {
    override val example: String = """
        move 1 from 2 to 1
        move 3 from 1 to 3
        move 2 from 2 to 1
        move 1 from 1 to 2
    """.trimIndent()

    override val result1: String = "CMZ"

    override val result2: String = "4"

    override val input: String = "/day05/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask

    @Test
    fun `should parse a line`() {
        assertThat(parseMove("move 12 from 2 to 10"))
            .isEqualTo(Move(12, 2, 10))
    }

    @Test
    fun `should extract the result`() {
        val containers = mapOf(
            1 to "NZ",
            2 to "DCM",
            3 to "P",
        )
        assertThat(containers.result).isEqualTo("NDP")
    }
}