package com.gilpereda.aoc2022

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


abstract class BaseTest {

    abstract val example: String
    open val example2: String
        get() = example

    abstract val resultExample1: String
    abstract val resultReal1: String
    abstract val resultExample2: String
    abstract val resultReal2: String
    abstract val input: String

    abstract val run1: Executable
    abstract val run2: Executable

    protected val inputSequence: Sequence<String>
        get() =
            BaseTest::class.java.getResourceAsStream(input)!!.bufferedReader().lineSequence()

    protected fun check(example: Pair<String, String>, run: Executable) {
        val (input, expected) = example
        assertThat(run(input.splitToSequence("\n"))).isEqualTo(expected)
    }

    @Test
    fun `should work with the example - part 1`() {
        check(example to resultExample1, run1)
    }

    @Test
    fun `should return the result - part 1`() {
        assertThat(run1(inputSequence)).isEqualTo(resultReal1)
    }

    @Test
    fun `should work with the example - part 2`() {
        check(example2 to resultExample2, run2)
    }

    @Test
    fun `should return the result - part 2`() {
        assertThat(run2(inputSequence)).isEqualTo(resultReal1)
    }
}
