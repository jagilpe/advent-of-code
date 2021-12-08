package com.gilpereda.adventsofcode.adventsofcode2021

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

abstract class BaseTest {

    abstract val example: String
    abstract val result: String
    abstract val input: String

    abstract val run: (Sequence<String>) -> String

    protected fun check(example: Pair<String, String>) {
        val (input, expected) = example
        assertThat(run(input.splitToSequence("\n"))).isEqualTo(expected)
    }

    @Test
    fun `should work with the example`() {
        check(example to result)
    }

    @Test
    fun `shoul return the result`() {
        val result = run(BaseTest::class.java.getResourceAsStream(input)!!.bufferedReader().lineSequence())

        assertThat(result).isNotNull
        println("Result: $result")
    }

}

