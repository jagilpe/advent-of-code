package com.gilpereda.aoc2022.day20

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.of
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

/**
 * -8271 -> wrong
 */
class Day20Test : BaseTest() {
    override val example: String = """1
2
-3
3
-2
0
4"""

    override val result1: String = "3"

    override val result2: String = "1623178306"

    override val input: String = "/day20/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask

    @ParameterizedTest
    @MethodSource("movements")
    fun `should move the elements correctly`(initial: List<Long>, value: Long, expected: List<Long>) {
        val input = initial.map(::Value)
        val toMove = input.first { it.value == value}
        val output = expected.map { i -> input.find { it.value == i } }
        assertThat(input.move(toMove, input.size)).isEqualTo(output)
    }

    companion object {
        @JvmStatic
        fun movements(): Stream<Arguments> = Stream.of(
            of(listOf(0L, 1, 2, 3, 4, 5, 6), 3, listOf(0L, 1, 2, 4, 5, 6, 3)),
            of(listOf(0L, 1, 2, 13, 4, 5, 6), 13, listOf(0L, 1, 2, 4, 13, 5, 6)),
            of(listOf(1L, 2, -3, 3, -2, 0, 4), 1, listOf(2L, 1, -3, 3, -2, 0, 4)),
            of(listOf(2L, 1, -3, 3, -2, 0, 4), 2, listOf(1L, -3, 2, 3, -2, 0, 4)),
            of(listOf(1L, -3, 2, 3, -2, 0, 4), -3, listOf(1L, 2, 3, -2, -3, 0, 4)),
            of(listOf(1L, 2, 3, -2, -3, 0, 4), 3, listOf(1L, 2, -2, -3, 0, 3, 4)),
            of(listOf(1L, 2, -2, -3, 0, 3, 4), -2, listOf(1L, 2, -3, 0, 3, 4, -2)),
            of(listOf(1L, 2, -3, 0, 3, 4, -2), 0, listOf(1L, 2, -3, 0, 3, 4, -2)),
            of(listOf(1L, 2, -3, 0, 3, 4, -2), 4, listOf(1L, 2, -3, 4, 0, 3, -2)),
        )
    }

}
