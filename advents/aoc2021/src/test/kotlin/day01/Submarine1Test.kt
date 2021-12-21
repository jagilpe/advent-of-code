package com.gilpereda.adventsofcode.adventsofcode2021.day01

import com.gilpereda.adventsofcode.adventsofcode2021.BaseTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Submarine1Test : BaseTest() {
    override val example = """
    199
    200
    208
    210
    200
    207
    240
    269
    260
    263""".trimIndent()

    override val result1 = "7"
    override val result2: String = "5"

    override val input: String = "/day01/input.txt"

    override val run1: (Sequence<String>) -> String = ::countIncreases

    override val run2: (Sequence<String>) -> String = ::countWindowed

    @Test
    fun `should be zero for one measurement only`() {
        check("123" to "0", run1)
    }

    @Test
    fun `should count the increases`() {
        val example = """
            1
            2
            3
        """.trimIndent()
        check(example to "2", run1)
    }

    @Test
    fun `should return zero if there is no measure`() {
        check("" to "0", run1)
    }

    @Test
    fun `should ignore the decreases`() {
        val example = """
            1
            2
            1
            3
        """.trimIndent()
        check(example to "2", run1)
    }
}