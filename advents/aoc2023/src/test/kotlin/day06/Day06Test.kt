package com.gilpereda.aoc2022.day06

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable
import com.gilpereda.aoc2022.day05.firstTask
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day06Test {
    val example: String = """
        Time:      7  15   30
        Distance:  9  40  200
    """.trimIndent()

    @Test
    fun `should work with the example 1`() {
        val input = listOf(
            Race(7, 9),
            Race(15, 40),
            Race(30, 200),
        )

        assertThat(firstTask(input)).isEqualTo(288)
    }

    @Test
    fun `should get the first result`() {
        val input = listOf(
            Race(55, 246),
            Race(82, 1441),
            Race(64, 1012),
            Race(90, 1111),
        )

        val result = firstTask(input)
        assertThat(result).isGreaterThan(0)
        println("result: $result")
    }


    @Test
    fun `should work with the example 2`() {
        val input = listOf(
            Race(71530, 940200),
        )

        assertThat(firstTask(input)).isEqualTo(71503)
    }

    @Test
    fun `should get the second result`() {
        val input = listOf(
            Race(55826490, 246144110121111),
        )

        val result = firstTask(input)
        assertThat(result).isGreaterThan(0)
        println("result: $result")
    }
}