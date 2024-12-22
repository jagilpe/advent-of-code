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

    override val resultReal1: String = "248684"

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
            "179A;12172",
            "456A;29184",
            "379A;24256",
        ],
        delimiterString = ";",
    )
    fun `should calculate the complexity of one code`(
        code: String,
        expected: Long,
    ) {
        val cache = StepCache()
        val robot = Robot(cache, Robot(cache))
        assertThat(Code(code, robot).result()).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "029A;<vA<AA>>^AvAA<^A>A<v<A>>^AvA^A<vA>^A<v<A>^A>AAvA^A<v<A>A>^AAAvA<^A>A",
            "980A;<v<A>>^AAAvA^A<vA<AA>>^AvAA<^A>A<v<A>A>^AAAvA<^A>A<vA>^A<A>A",
            "179A;<v<A>>^A<vA<A>>^AAvAA<^A>A<v<A>>^AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A",
            "456A;<v<A>>^AA<vA<A>>^AAvAA<^A>A<vA>^A<A>A<vA>^A<A>A<v<A>A>^AAvA<^A>A",
            "379A;<v<A>>^AvA^A<vA<AA>>^AAvA<^A>AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A",
        ],
        delimiterString = ";",
    )
    fun `should revert the transformation`(
        code: String,
        stepsString: String,
    ) {
        val steps = stepsString.toList().map(Button::from)

        val actual = revertNumericPad(revertDirectionPad(revertDirectionPad(steps)))

        assertThat(actual).isEqualTo(code)
    }
}
