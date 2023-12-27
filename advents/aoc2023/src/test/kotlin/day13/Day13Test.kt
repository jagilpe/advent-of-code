package com.gilpereda.aoc2022.day13

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class Day13Test : BaseTest() {
    override val example: String = """
        #.##..##.
        ..#.##.#.
        ##......#
        ##......#
        ..#.##.#.
        ..##..##.
        #.#.##.#.

        #...##..#
        #....#..#
        ..##..###
        #####.##.
        #####.##.
        ..##..###
        #....#..#
    """.trimIndent()

    override val resultExample1: String = "405"

    override val resultExample2: String = "400"

    override val resultReal1: String = "30158"

    override val resultReal2: String = "36474"

    override val input: String = "/day13/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask

    @ParameterizedTest
    @MethodSource("blocks")
    fun `should calculate the horizontalPoints`(block: String, expected: List<Long>, ignoreMe: List<Long>, ignoreMeToo: Long) {
        assertThat(Block.parsed(block).horizontalMirrors).isEqualTo(expected)
    }

    @ParameterizedTest
    @MethodSource("blocks")
    fun `should calculate the verticalPoints`(block: String, ignoreMe: List<Long>, expected: List<Long>, ignoreMeToo: Long) {
        assertThat(Block.parsed(block).verticalMirrors).isEqualTo(expected)
    }

    @ParameterizedTest
    @MethodSource("blocks")
    fun `should calculate the alternative`(block: String, ignoreMe: List<Long>, ignoreMeToo: List<Long>, expected: Long) {
        assertThat(Block.parsed(block).alternative).isEqualTo(expected)
    }

    companion object {
        @JvmStatic
        fun blocks(): Stream<Arguments> = Stream.of(
            Arguments.of(
                """
                    #.##..##.
                    ..#.##.#.
                    ##......#
                    ##......#
                    ..#.##.#.
                    ..##..##.
                    #.#.##.#.
                """.trimIndent(),
                listOf<Long>(),
                listOf(5),
                300,
            ),
//            Arguments.of(
//                """
//                    #...##..#
//                    #....#..#
//                    ..##..###
//                    #####.##.
//                    #####.##.
//                    ..##..###
//                    #....#..#
//                """.trimIndent(),
//                400,
//                0,
//                100,
//            ),
//            Arguments.of(
//                """
//                    #####.#.#.#..#.#.
//                    #..###.###.##.###
//                    ....#...#..##..#.
//                    #..####..#....#..
//                    #..###..#.####.#.
//                    #####.#.#..##..#.
//                    #####.#.#####.##.
//                    .##..###..####..#
//                    #..#.#..########.
//                    #..##.#####..####
//                    ####...#..#..#..#
//                """.trimIndent(),
//                0,
//                2,
//                12,
//            )
        )
    }
}