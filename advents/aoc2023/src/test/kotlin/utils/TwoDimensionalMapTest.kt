package com.gilpereda.aoc2022.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.TooManyListenersException
import java.util.stream.Stream

class TwoDimensionalMapTest {
    @ParameterizedTest
    @MethodSource("transformations")
    fun `should work`(initial: String, expected: String, transform: (TwoDimensionalMap<Char>) -> TwoDimensionalMap<Char>) {
        val initialMap = initial.toTwoDimensionalMap { it }
        val actualMap = transform(initialMap)

        assertThat(actualMap.dump()).isEqualTo(expected)
    }


    companion object {
        @JvmStatic
        fun transformations(): Stream<Arguments> = Stream.of(
            testCase(
                initial = """
                    OOOO.#OO..
                    OOO.#....#
                    OO..O##...
                    O..#.OO...
                    O.......#.
                    O.#....#.#
                    .....#.O..
                    ..........
                    #....###..
                    #....#....
                """.trimIndent(),
                expected = """
                    ..OO#.OOOO
                    #....#.OOO
                    ...##O..OO
                    ...OO.#..O
                    .#.......O
                    #.#....#.O
                    ..O.#.....
                    ..........
                    ..###....#
                    ....#....#
                """.trimIndent(),
                transform = { it.reflect() }
            ),
            testCase(
                initial = """
                    OOOO.#OO..
                    OOO.#....#
                    OO..O##...
                    O..#.OO...
                    O.......#.
                    O.#....#.#
                    .....#.O..
                    ..........
                    #....###..
                    #....#....
                """.trimIndent(),
                expected = """
                    .#...#....
                    ....#.....
                    O....#O.#.
                    O.#O....#.
                    #.#O..#.##
                    .#O.......
                    O..#......
                    OO...#....
                    OOO.......
                    OOOOOO..##
                    
                    ..OO#.OOOO
                    #....#.OOO
                    ...##O..OO
                    ...OO.#..O
                    .#.......O
                    #.#....#.O
                    ..O.#.....
                    ..........
                    ..###....#
                    ....#....#
                """.trimIndent(),
                transform = { it.transform(Orientation.SOUTH) }
            ),
        )

        fun testCase(
            initial: String,
            expected: String,
            transform: (TwoDimensionalMap<Char>) -> TwoDimensionalMap<Char>,
        ): Arguments =
            Arguments.of(initial, expected, transform)

    }
}