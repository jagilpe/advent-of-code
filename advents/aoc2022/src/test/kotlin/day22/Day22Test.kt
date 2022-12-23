package com.gilpereda.aoc2022.day22

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable
import com.gilpereda.aoc2022.day22.Orientation.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.of
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class Day22Test : BaseTest() {
    override val example: String = """
        ...#
        .#..
        #...
        ....
...#.......#
........#...
..#....#....
..........#.
        ...#....
        .....#..
        .#......
        ......#.

10R5L5R10L4R5L5
"""

    override val result1: String = "6032"

    override val result2: String
        get() = TODO()

    override val input: String = "/day22/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask

    @Test
    fun `should parse the rows`() {
        val (mace, _) = example.splitToSequence("\n").parsed()
        val expectedRows = listOf(
            Line(8, 11, listOf(11)),
            Line(8, 11, listOf(9)),
            Line(8, 11, listOf(8)),
            Line(8, 11),
            Line(0, 11, listOf(3, 11)),
            Line(0, 11, listOf(8)),
            Line(0, 11, listOf(2, 7)),
            Line(0, 11, listOf(10)),
            Line(8, 15, listOf(11)),
            Line(8, 15, listOf(13)),
            Line(8, 15, listOf(9)),
            Line(8, 15, listOf(14)),
        )

        assertThat(mace.rows).isEqualTo(expectedRows)
    }

    @Test
    fun `should parse the columns`() {
        val (mace, _) = example.splitToSequence("\n").parsed()
        val expectedRows = listOf(
            Line(4, 7),
            Line(4, 7),
            Line(4, 7, listOf(6)),
            Line(4, 7, listOf(4)),
            Line(4, 7),
            Line(4, 7),
            Line(4, 7),
            Line(4, 7, listOf(6)),
            Line(0, 11, listOf(2, 5)),
            Line(0, 11, listOf(1, 10)),
            Line(0, 11, listOf(7)),
            Line(0, 11, listOf(0, 4, 8)),
            Line(8, 11),
            Line(8, 11, listOf(9)),
            Line(8, 11, listOf(11)),
            Line(8, 11),
        )

        assertThat(mace.columns).isEqualTo(expectedRows)
    }

    @Test
    fun `should parse the movements`() {
        val (_, movements) = example.splitToSequence("\n").parsed()
        val expectedMovements = listOf(
            Go(10), TurnRight, Go(5), TurnLeft, Go(5),
            TurnRight, Go(10), TurnLeft, Go(4), TurnRight, Go(5), TurnLeft, Go(5)
        )

        assertThat(movements).isEqualTo(expectedMovements)
    }


    @ParameterizedTest
    @MethodSource
    fun `basic movement`(case: Int, starting: State, movement: Movement, expected: State) {
        assertThat(starting.next(movement)).isEqualTo(expected)
    }

    companion object {
        private val simpleMaze = Maze(
            rows = List(5) { Line(0, 4) },
            columns = List(5) { Line(0, 4) },
        )

        private val simpleStart = State(simpleMaze, 2, 2, RIGHT)

        @JvmStatic
        fun `basic movement`(): Stream<Arguments> = listOf(
            of(1, simpleStart.dir(RIGHT), TurnRight, simpleStart.dir(DOWN)),
            of(2, simpleStart.dir(DOWN), TurnRight, simpleStart.dir(LEFT)),
            of(3, simpleStart.dir(LEFT), TurnRight, simpleStart.dir(UP)),
            of(4, simpleStart.dir(UP), TurnRight, simpleStart.dir(RIGHT)),
            of(5, simpleStart.dir(RIGHT), TurnLeft, simpleStart.dir(UP)),
            of(6, simpleStart.dir(DOWN), TurnLeft, simpleStart.dir(RIGHT)),
            of(7, simpleStart.dir(LEFT), TurnLeft, simpleStart.dir(DOWN)),
            of(8, simpleStart.dir(UP), TurnLeft, simpleStart.dir(LEFT)),

            of(9, simpleStart.dir(RIGHT), Go(1), simpleStart.dir(RIGHT).x(3)),
            of(10, simpleStart.dir(LEFT), Go(1), simpleStart.dir(LEFT).x(1)),
            of(11, simpleStart.dir(UP), Go(1), simpleStart.dir(UP).y(1)),
            of(12, simpleStart.dir(DOWN), Go(1), simpleStart.dir(DOWN).y(3)),


            of(13, simpleStart.dir(RIGHT), Go(4), simpleStart.dir(RIGHT).x(1)),
            of(14, simpleStart.dir(LEFT), Go(4), simpleStart.dir(LEFT).x(3)),
            of(15, simpleStart.dir(UP), Go(4), simpleStart.dir(UP).y(3)),
            of(16, simpleStart.dir(DOWN), Go(4), simpleStart.dir(DOWN).y(1)),

            *with(parseMaze("...#...")) {
                arrayOf(
                    of(16, simpleStart.dir(DOWN), Go(4), simpleStart.dir(DOWN).y(1))
                )
            }

        ).stream()
    }
}

private fun State.dir(orientation: Orientation): State = copy(orientation = orientation)
private fun State.x(x: Int): State = copy(x = x)
private fun State.y(y: Int): State = copy(y = y)
