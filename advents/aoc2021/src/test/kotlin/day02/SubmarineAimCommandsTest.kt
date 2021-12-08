package com.gilpereda.adventsofcode.adventsofcode2021.day02

import com.gilpereda.adventsofcode.adventsofcode2021.BaseTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class SubmarineAimCommandsTest : BaseTest() {
    override val example = """
        forward 5
        down 5
        forward 8
        up 3
        down 8
        forward 2
    """.trimIndent()
    override val result: String = "900"

    override val input: String = "/day02/input.txt"

    override val run: (Sequence<String>) -> String = ::aimAndMoveSubmarine
}