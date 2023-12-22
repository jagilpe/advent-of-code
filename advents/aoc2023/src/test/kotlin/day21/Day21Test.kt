package com.gilpereda.aoc2022.day21

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable
import com.gilpereda.aoc2022.utils.geometry.Point
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class Day21Test : BaseTest() {
    override val example: String = """
        ...........
        .....###.#.
        .###.##..#.
        ..#.#...#..
        ....#.#....
        .##..S####.
        .##..#...#.
        .......##..
        .##.#.####.
        .##..##.##.
        ...........
    """.trimIndent()

    override val resultExample1: String = "16"

    override val resultExample2: String = "668697"

    override val resultReal1: String = "3574"

    override val resultReal2: String = ""

    override val input: String = "/day21/input"

    override fun runExample1(sequence: Sequence<String>): String =
        firstTaskExample(sequence)

    override fun runReal1(sequence: Sequence<String>): String =
        firstTaskReal(sequence)

    override fun runExample2(sequence: Sequence<String>): String =
        secondTaskExample(sequence)

    override fun runReal2(sequence: Sequence<String>): String =
        secondTaskReal(sequence)

    @ParameterizedTest
    @CsvSource(
        "-11,5,-3",
        "-10,5,-2",
        "-6,5,-2",
        "-5,5,-1",
        "-4,5,-1",
        "-2,5,-1",
        "-1,5,-1",
        "0,5,0",
        "2,5,0",
        "4,5,0",
        "5,5,1",
        "6,5,1",
        "9,5,1",
        "10,5,2",
    )
    fun `should get the block coordinates`(value: Int, length: Int, expected: Int) {
        assertThat(value.mapped(length)).isEqualTo(expected)
    }
}