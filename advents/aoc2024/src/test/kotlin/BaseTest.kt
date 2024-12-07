package com.gilpereda.aoc2024

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

    open val run1: Executable = { TODO() }
    open val run2: Executable = { TODO() }

    protected open fun runExample1(sequence: Sequence<String>): String = run1(sequence)

    protected open fun runReal1(sequence: Sequence<String>): String = run1(sequence)

    protected open fun runExample2(sequence: Sequence<String>): String = run2(sequence)

    protected open fun runReal2(sequence: Sequence<String>): String = run2(sequence)

    protected val inputSequence: Sequence<String>
        get() =
            BaseTest::class.java
                .getResourceAsStream(input)!!
                .bufferedReader()
                .lineSequence()

    protected fun check(
        example: Pair<String, String>,
        run: Executable,
    ) {
        val (input, expected) = example
        assertThat(run(input.splitToSequence("\n"))).isEqualTo(expected)
    }

//    @Test
    fun `should work with the example - part 1`() {
        check(example to resultExample1, ::runExample1)
    }

//    @Test
    fun `should return the result - part 1`() {
        assertThat(runReal1(inputSequence)).isEqualTo(resultReal1)
    }

//    @Test
    fun `should work with the example - part 2`() {
        check(example2 to resultExample2, ::runExample2)
    }

    @Test
    fun `should return the result - part 2`() {
        assertThat(runReal2(inputSequence)).isEqualTo(resultReal2)
    }
}
