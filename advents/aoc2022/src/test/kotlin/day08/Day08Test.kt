package com.gilpereda.aoc2022.day08

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day08Test : BaseTest() {
    override val example: String = """30373
25512
65332
33549
35390"""

    override val result1: String
        get() = "21"

    override val result2: String
        get() = "8"

    override val input: String = "/day08/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask

    @Test
    fun `should parse a line`() {
        assertThat(parseLine("25512")).isEqualTo(listOf(2,5,5,1,2))
    }

    @Test
    fun `should transpose a forest`() {
        val input = """30373
25512
65332
33549
35390"""
        val expected = listOf(
            listOf(3, 2, 6, 3, 3),
            listOf(0, 5, 5, 3, 5),
            listOf(3, 5, 3, 5, 3),
            listOf(7, 1, 3, 4, 9),
            listOf(3, 2, 2, 9, 0),
        )

        assertThat(transpose(input.splitToSequence("\n").parsed))
            .isEqualTo(expected)
    }

    @Test
    fun `all border trees are visible`() {
        val input = """12
34
        """.trimMargin()
        assertThat(firstTask(input.splitToSequence("\n"))).isEqualTo("4")
    }

}