package com.gilpereda.aoc2022

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

abstract class BaseTest {

    abstract val example: String
    open val example2: String
        get() = example

    abstract val result1: String
    abstract val result2: String
    abstract val input: String

    abstract val run1: Executable
    abstract val run2: Executable

    private val inputSequence: Sequence<String>
        get() =
            BaseTest::class.java.getResourceAsStream(input)!!.bufferedReader().lineSequence()

    protected fun check(example: Pair<String, String>, run: Executable) {
        val (input, expected) = example
        assertThat(run(input.splitToSequence("\n"))).isEqualTo(expected)
    }

    @Test
    fun `should work with the example - part 1`() {
        check(example to result1, run1)
    }

    @Test
    fun `should work with the example - part 2`() {
        check(example2 to result2, run2)
    }

    @Test
    fun `should return the result - part 1`() {
        val result = run1(inputSequence)

        assertThat(result).isNotNull
        println("Result: $result")
    }

    @Test
    fun `should return the result - part 2`() {
        val result = run2(inputSequence)

        assertThat(result).isNotNull
        println("Result: $result")
    }
}
