package com.gilpereda.aoc2022.day24

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable
import com.gilpereda.aoc2022.day24.Cell.Companion.D
import com.gilpereda.aoc2022.day24.Cell.Companion.L
import com.gilpereda.aoc2022.day24.Cell.Companion.R
import com.gilpereda.aoc2022.day24.Cell.Companion.U
import com.gilpereda.aoc2022.day24.Cell.Companion.W
import com.gilpereda.aoc2022.day24.Cell.Companion.o
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class Day24Test : BaseTest() {
    override val example: String = """#.######
#>>.<^<#
#.<..<<#
#>v.><>#
#<^v^^>#
######.#"""

    override val result1: String = "18"

    override val result2: String = "54"

    override val input: String = "/day24/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask

    @Test
    fun `should parsed the inputs`() {
        val expected =
            listOf(
                listOf(W, o, W, W, W, W, W, W),
                listOf(W, R, R, o, L, U, L, W),
                listOf(W, o, L, o, o, L, L, W),
                listOf(W, R, D, o, R, L, R, W),
                listOf(W, L, U, D, U, U, R, W),
                listOf(W, W, W, W, W, W, o, W),
            )

        assertThat(example.split("\n").parsed()).isEqualTo(expected)
    }

    @ParameterizedTest
    @MethodSource("blizzardEvolution")
    fun `should follow the evolution of the blizzards`(maze: Expedition, expected: String, round: Int) {
        assertThat(maze.mazeAt(round).toString()).isEqualTo(expected)
    }

    companion object {
        private val expectedEvolution: List<String> = listOf(
            """
                #E######
                #>>.<^<#
                #.<..<<#
                #>v.><>#
                #<^v^^>#
                ######.#
            """.trimIndent(),
            """
                #.######
                #E>3.<.#
                #<..<<.#
                #>2.22.#
                #>v..^<#
                ######.#
            """.trimIndent(),
            """
                #.######
                #.2>2..#
                #E^22^<#
                #.>2.^>#
                #.>..<.#
                ######.#
            """.trimIndent(),
            """
                #.######
                #<^<22.#
                #E2<.2.#
                #><2>..#
                #..><..#
                ######.#
            """.trimIndent(),
            """
                #.######
                #E<..22#
                #<<.<..#
                #<2.>>.#
                #.^22^.#
                ######.#
            """.trimIndent(),
            """
                #.######
                #2Ev.<>#
                #<.<..<#
                #.^>^22#
                #.2..2.#
                ######.#
            """.trimIndent(),
            """
                #.######
                #>2E<.<#
                #.2v^2<#
                #>..>2>#
                #<....>#
                ######.#
            """.trimIndent(),
            """
                #.######
                #.22^2.#
                #<vE<2.#
                #>>v<>.#
                #>....<#
                ######.#
            """.trimIndent(),
            """
                #.######
                #.<>2^.#
                #.E<<.<#
                #.22..>#
                #.2v^2.#
                ######.#
            """.trimIndent(),
            """
                #.######
                #<E2>>.#
                #.<<.<.#
                #>2>2^.#
                #.v><^.#
                ######.#
            """.trimIndent(),
            """
                #.######
                #.2E.>2#
                #<2v2^.#
                #<>.>2.#
                #..<>..#
                ######.#
            """.trimIndent(),
            """
                #.######
                #2^E^2>#
                #<v<.^<#
                #..2.>2#
                #.<..>.#
                ######.#
            """.trimIndent(),
            """
                #.######
                #>>.<^<#
                #.<E.<<#
                #>v.><>#
                #<^v^^>#
                ######.#
            """.trimIndent(),

            )

        @JvmStatic
        fun blizzardEvolution(): Stream<Arguments> = listOf(
            testCase(
                listOf(
                    listOf(W, o, W, W, W),
                    listOf(W, o, o, o, W),
                    listOf(W, o, R, o, W),
                    listOf(W, o, o, o, W),
                    listOf(W, W, W, o, W),
                ),
                listOf(
                    listOf(W, o, W, W, W),
                    listOf(W, o, o, o, W),
                    listOf(W, o, o, R, W),
                    listOf(W, o, o, o, W),
                    listOf(W, W, W, o, W),
                ),
            ),
            testCase(
                listOf(
                    listOf(W, o, W, W, W),
                    listOf(W, o, o, o, W),
                    listOf(W, o, L, o, W),
                    listOf(W, o, o, o, W),
                    listOf(W, W, W, o, W),
                ),
                listOf(
                    listOf(W, o, W, W, W),
                    listOf(W, o, o, o, W),
                    listOf(W, L, o, o, W),
                    listOf(W, o, o, o, W),
                    listOf(W, W, W, o, W),
                ),
            ),
            testCase(
                listOf(
                    listOf(W, o, W, W, W),
                    listOf(W, o, o, o, W),
                    listOf(W, o, U, o, W),
                    listOf(W, o, o, o, W),
                    listOf(W, W, W, o, W),
                ),
                listOf(
                    listOf(W, o, W, W, W),
                    listOf(W, o, U, o, W),
                    listOf(W, o, o, o, W),
                    listOf(W, o, o, o, W),
                    listOf(W, W, W, o, W),
                ),
            ),
            testCase(
                listOf(
                    listOf(W, o, W, W, W),
                    listOf(W, o, o, o, W),
                    listOf(W, o, D, o, W),
                    listOf(W, o, o, o, W),
                    listOf(W, W, W, o, W),
                ),
                listOf(
                    listOf(W, o, W, W, W),
                    listOf(W, o, o, o, W),
                    listOf(W, o, o, o, W),
                    listOf(W, o, D, o, W),
                    listOf(W, W, W, o, W),
                ),
            ),
            testCase(
                listOf(
                    listOf(W, o, W, W, W),
                    listOf(W, o, D, o, W),
                    listOf(W, R, D, L, W),
                    listOf(W, o, U, o, W),
                    listOf(W, W, W, o, W),
                ),
                listOf(
                    listOf(W, o, W, W, W),
                    listOf(W, o, o, o, W),
                    listOf(W, o, D + R + L + U, o, W),
                    listOf(W, o, D, o, W),
                    listOf(W, W, W, o, W),
                ),
            ),
            testCase(
                listOf(
                    listOf(W, o, W, W, W),
                    listOf(W, o, U, o, W),
                    listOf(W, o, o, o, W),
                    listOf(W, o, o, o, W),
                    listOf(W, W, W, o, W),
                ),
                listOf(
                    listOf(W, o, W, W, W),
                    listOf(W, o, o, o, W),
                    listOf(W, o, o, o, W),
                    listOf(W, o, U, o, W),
                    listOf(W, W, W, o, W),
                ),
            ),
            testCase(
                listOf(
                    listOf(W, o, W, W, W),
                    listOf(W, o, o, o, W),
                    listOf(W, o, o, o, W),
                    listOf(W, o, D, o, W),
                    listOf(W, W, W, o, W),
                ),
                listOf(
                    listOf(W, o, W, W, W),
                    listOf(W, o, D, o, W),
                    listOf(W, o, o, o, W),
                    listOf(W, o, o, o, W),
                    listOf(W, W, W, o, W),
                ),
            ),
            testCase(
                listOf(
                    listOf(W, o, W, W, W),
                    listOf(W, o, o, o, W),
                    listOf(W, o, o, R, W),
                    listOf(W, o, o, o, W),
                    listOf(W, W, W, o, W),
                ),
                listOf(
                    listOf(W, o, W, W, W),
                    listOf(W, o, o, o, W),
                    listOf(W, R, o, o, W),
                    listOf(W, o, o, o, W),
                    listOf(W, W, W, o, W),
                ),
            ),
            testCase(
                listOf(
                    listOf(W, o, W, W, W),
                    listOf(W, o, o, o, W),
                    listOf(W, L, o, o, W),
                    listOf(W, o, o, o, W),
                    listOf(W, W, W, o, W),
                ),
                listOf(
                    listOf(W, o, W, W, W),
                    listOf(W, o, o, o, W),
                    listOf(W, o, o, L, W),
                    listOf(W, o, o, o, W),
                    listOf(W, W, W, o, W),
                ),
            ),
            *expectedEvolution.mapIndexed { index, expected ->
                extendedEvolutionTestCase(expectedEvolution.first(), expected, index)
            }.toTypedArray()
        ).stream()

        private fun testCase(
            initial: MazeMap,
            expected: MazeMap,
            rounds: Int = 1,
        ): Arguments = Arguments.of(initial.evolution(rounds), Maze(expected).toString(), rounds)

        private fun extendedEvolutionTestCase(
            initial: String,
            expected: String,
            rounds: Int,
        ): Arguments = Arguments.of(initial.split("\n").parsed().evolution(rounds), expected.replace("E", "."), rounds)
    }

}