package com.gilpereda.aoc2024.day21

import com.gilpereda.aoc2024.BaseTest
import com.gilpereda.aoc2024.Executable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class Day21Test : BaseTest() {
    override val example: String =
        """
        029A
        980A
        179A
        456A
        379A
        """.trimIndent()

    override val resultExample1: String = "126384"

    override val resultReal1: String = ""

    override val resultExample2: String = ""

    override val resultReal2: String = ""

    override val input: String = "/day21/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask

    @ParameterizedTest
    @CsvSource(
        value = [
            "029A;1972",
            "980A;58800",
        ],
        delimiterString = ";",
    )
    fun `should calculate the complexity of one code`(
        code: String,
        expected: Long,
    ) {
        val cache = Cache()
        assertThat(Code(code, cache).result(1)).isEqualTo(expected)
    }
}
