package com.gilpereda.adventsofcode.adventsofcode2021.day24

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

class Day24Test : BaseTest() {
    override val example: String
        get() = TODO("Not yet implemented")
    override val result1: String
        get() = TODO("Not yet implemented")
    override val result2: String
        get() = TODO("Not yet implemented")
    override val input: String = "/day24/input.txt"
    override val run1: Executable = part1
    override val run2: Executable = part2


    @Test
    fun `should make the calculations`() {
        val program = listOf(inp(X), mul(X, -1))

        assertThat(program.result(Alu(input = listOf(10))).x).isEqualTo(-10)
    }

    @Test
    fun `should convert to binary`() {
        val program =
            listOf(
                inp(W),
                add(Z, W),
                mod(Z, 2),
                div(W, 2),
                add(Y, W),
                mod(Y, 2),
                div(W, 2),
                add(X, W),
                mod(X, 2),
                div(W, 2),
                mod(W, 2),
            )

        val result = program.result(Alu(input = listOf(10)))
        assertThat(result.x).isEqualTo(0)
        assertThat(result.y).isEqualTo(1)
        assertThat(result.z).isEqualTo(0)
        assertThat(result.w).isEqualTo(1)
    }

    @ParameterizedTest
    @MethodSource("parsing")
    fun `it should parse a line`(line: String, instruction: Instruction) {
        assertThat(parseLine(line)).isEqualTo(instruction)
    }

    companion object {
        @JvmStatic
        fun parsing(): Stream<Arguments> = Stream.of(
            Arguments.of("inp x", inp(X)),
            Arguments.of("inp y", inp(Y)),
            Arguments.of("inp z", inp(Z)),
            Arguments.of("inp w", inp(W)),
            Arguments.of("add x y", add(X, Y)),
            Arguments.of("add z 10", add(Z, 10)),
            Arguments.of("add x y", add(X, Y)),
            Arguments.of("mul z 10", mul(Z, 10)),
            Arguments.of("mul x y", mul(X, Y)),
            Arguments.of("div z 140", div(Z, 140)),
            Arguments.of("div w x", div(W, X)),
            Arguments.of("mod z 10", mod(Z, 10)),
            Arguments.of("mod x y", mod(X, Y)),
            Arguments.of("eql z 10", eql(Z, 10)),
            Arguments.of("eql z w", eql(Z, W)),
            Arguments.of("add x -10", add(X, -10)),
        )
    }
}