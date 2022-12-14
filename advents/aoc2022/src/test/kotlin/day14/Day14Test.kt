package com.gilpereda.aoc2022.day14

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.expect

class Day14Test : BaseTest() {
    override val example: String = """498,4 -> 498,6 -> 496,6
503,4 -> 502,4 -> 502,9 -> 494,9
    """

    override val result1: String = "24"

    override val result2: String = "93"

    override val input: String = "/day14/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask

    @Test
    fun `should parse the paths`() {
        val expected = listOf(
            Path.of(Point(498, 4), Point(498, 6), Point(496, 6),),
            Path.of(Point(503, 4), Point(502,4), Point(502,9), Point(494,9),)
        )

        assertThat(example.splitToSequence("\n").parsed()).isEqualTo(expected)
    }

    @Test
    fun `should fill the cave`() {
        val paths = example.splitToSequence("\n").parsed()
        val cave = Cave(paths)

        assertThat(cave).isNotNull
    }
}