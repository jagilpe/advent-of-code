package com.gilpereda.adventsofcode.adventsofcode2021.day25

import arrow.core.left
import arrow.core.right
import com.gilpereda.adventsofcode.adventsofcode2021.BaseTest
import com.gilpereda.adventsofcode.adventsofcode2021.Executable
import com.gilpereda.adventsofcode.adventsofcode2021.day24.Add.Companion.add
import com.gilpereda.adventsofcode.adventsofcode2021.day24.Div.Companion.div
import com.gilpereda.adventsofcode.adventsofcode2021.day24.Eql.Companion.eql
import com.gilpereda.adventsofcode.adventsofcode2021.day24.Inp.Companion.inp
import com.gilpereda.adventsofcode.adventsofcode2021.day24.Mod.Companion.mod
import com.gilpereda.adventsofcode.adventsofcode2021.day24.Mul.Companion.mul
import com.gilpereda.adventsofcode.adventsofcode2021.day24.Register.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class Day25Test : BaseTest() {
    override val example: String = """
        v...>>.vv>
        .vv>>.vv..
        >>.>v>...v
        >>v>>.>.v.
        v>v.vv.v..
        >.>>..v...
        .vv..>.>v.
        v.v..>>v.v
        ....v..v.>
    """.trimIndent()

    override val result1: String = "58"

    override val result2: String
        get() = TODO("Not yet implemented")
    override val input: String = "/day25/input.txt"
    override val run1: Executable = part1
    override val run2: Executable = part2

    @Test
    fun `should move the east cucumbers`() {
        val initial = """
            ...>...
            .......
            ......>
            v.....>
            ......>
            .......
            ..vvv..
        """.trimIndent().splitToSequence("\n").parseInput()
        val expected = """
            ....>..
            .......
            >......
            v.....>
            >......
            .......
            ..vvv..
        """.trimIndent().splitToSequence("\n").parseInput()

        assertThat(initial.nextEast()).isEqualTo(expected)
    }

    @Test
    fun `should move the south cucumbers`() {
        val initial = """
            ....>..
            .......
            >......
            v.....>
            >......
            .......
            ..vvv..
        """.trimIndent().splitToSequence("\n").parseInput()
        val expected = """
            ..vv>..
            .......
            >......
            v.....>
            >......
            .......
            ....v..
        """.trimIndent().splitToSequence("\n").parseInput()

        assertThat(initial.nextSouth()).isEqualTo(expected)
    }
}