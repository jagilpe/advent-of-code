package com.gilpereda.adventsofcode.adventsofcode2021.day02

import com.gilpereda.adventsofcode.adventsofcode2021.BaseTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class SubmarineCommandsTest : BaseTest() {
    override val example = """
        forward 5
        down 5
        forward 8
        up 3
        down 8
        forward 2
    """.trimIndent()

    override val result: String = "150"

    override val input: String = "/day02/input.txt"

    override val run: (Sequence<String>) -> String = ::moveSubmarine

    @ParameterizedTest
    @MethodSource("testData")
    fun `forward should increase horizontal position`(commands: String, result: String) {
        check(commands to result)
    }

    companion object {
        @JvmStatic
        fun testData(): Stream<Arguments> =
            Stream.of(
                Arguments.of("forward 5\ndown 1", "5"),
                Arguments.of("forward 10\ndown 1", "10"),
                Arguments.of("forward 10\ndown 10\ndown 10", "200"),
                Arguments.of("forward 10\ndown 10\nup 5", "50"),
            )
    }
}