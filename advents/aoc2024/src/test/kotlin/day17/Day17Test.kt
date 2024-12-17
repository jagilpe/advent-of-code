package com.gilpereda.aoc2024.day17

import com.gilpereda.aoc2024.BaseTest
import com.gilpereda.aoc2024.Executable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class Day17Test : BaseTest() {
    override val example: String =
        """
        Register A: 729
        Register B: 0
        Register C: 0

        Program: 0,1,5,4,3,0
        """.trimIndent()

    override val example2: String =
        """
        Register A: 2024
        Register B: 0
        Register C: 0

        Program: 0,3,5,4,3,0
        """.trimIndent()

    override val resultExample1: String = "4,6,3,5,6,3,5,2,1,0"

    override val resultReal1: String = "7,4,2,0,5,0,5,3,7"

    override val resultExample2: String = "117440"

    override val resultReal2: String = "202991746427434"

    override val input: String = "/day17/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask

    @ParameterizedTest
    @MethodSource
    fun `should make the calculations`(
        customizer: Computer.() -> Unit,
        programString: String,
        actualExtractor: (Computer) -> Any,
        expected: Any,
    ) {
        val program = programString.parsed()
        val computer =
            Computer(0, 0, 0)
                .apply(customizer)
                .runProgram(program)
        val actual = actualExtractor(computer)

        assertThat(actual).isEqualTo(expected)
    }

    private fun `should make the calculations`(): Stream<Arguments> =
        Stream.of(
            testCase(
                { it.registerC = 9 },
                "2,6",
                { it.registerB },
                1L,
            ),
            testCase(
                { it.registerA = 10 },
                "5,0,5,1,5,4",
                { it.outputString },
                "0,1,2",
            ),
            testCase(
                { it.registerA = 2024 },
                "0,1,5,4,3,0",
                { it.outputString to it.registerA },
                "4,2,5,6,7,7,7,7,3,1,0" to 0L,
            ),
            testCase(
                { it.registerB = 29 },
                "1,7",
                { it.registerB },
                26L,
            ),
            testCase(
                {
                    it.registerB = 2024
                    it.registerC = 43690
                },
                "4,0",
                { it.registerB },
                44354L,
            ),
            testCase(
                { it.registerA = 2024 },
                "0,0",
                { it.registerA },
                2024L,
            ),
            testCase(
                { it.registerA = 2024 },
                "0,1",
                { it.registerA },
                1012L,
            ),
            testCase(
                { it.registerA = 2024 },
                "0,2",
                { it.registerA },
                506L,
            ),
        )

    private fun testCase(
        computer: (Computer) -> Unit,
        programString: String,
        actual: (Computer) -> Any,
        expected: Any,
    ): Arguments = Arguments.of(computer, programString, actual, expected)
}
