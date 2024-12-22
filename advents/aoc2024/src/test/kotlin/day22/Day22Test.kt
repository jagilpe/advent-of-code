package com.gilpereda.aoc2024.day22

import com.gilpereda.aoc2024.BaseTest
import com.gilpereda.aoc2024.Executable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class Day22Test : BaseTest() {
    override val example: String =
        """
        1
        10
        100
        2024
        """.trimIndent()

    override val example2: String =
        """
        1
        2
        3
        2024
        """.trimIndent()

    override val resultExample1: String = "37327623"

    override val resultReal1: String = "15006633487"

    override val resultExample2: String = "23"

    override val resultReal2: String = ""

    override val input: String = "/day22/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask

    @ParameterizedTest
    @CsvSource(
        value = [
            "1:8685429",
            "10:4700978",
            "100:15273692",
            "2024:8667524",
        ],
        delimiterString = ":",
    )
    fun `should calculate the 2000th secret`(
        initial: Long,
        expected: Long,
    ) {
        val actual = secretSequence(initial).take(2001).last()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `should generate the right sequence`() {
        val actual = secretSequence(123L).take(10).toList()

        assertThat(actual).hasSize(10)
    }
}
