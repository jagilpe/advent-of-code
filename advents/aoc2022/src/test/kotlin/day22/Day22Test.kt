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

/**
 * 120018 -> too high
 * 26558
 */
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

    override val result2: String = "5031"

    override val input: String = "/day22/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask

    @Test
    fun `should parse the rows`() {
        val (maze, _) = example.split("\n").parsed()
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

        assertThat(maze.rows).isEqualTo(expectedRows)
    }

    @Test
    fun `should parse the columns`() {
        val (maze, _) = example.split("\n").parsed()
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

        assertThat(maze.columns).isEqualTo(expectedRows)
    }

    @Test
    fun `should parse the movements`() {
        val (_, movements) = example.split("\n").parsed()
        val expectedMovements = listOf(
            Go(10), TurnRight, Go(5), TurnLeft, Go(5),
            TurnRight, Go(10), TurnLeft, Go(4), TurnRight, Go(5), TurnLeft, Go(5)
        )

        assertThat(movements).isEqualTo(expectedMovements)
    }

    @Test
    fun `traverse should work`() {
        val initial = """
        ...#...#...#
        .#...#...#..
        #...#...#...
        ............
...#.....#.#.#
......#.......
..#...........
........#.#.#.
...#....
.....#..
.#......
......#.
""".split("\n").filter { it.isNotBlank() }
        val expected = """
    ........
    ......#.
    ..#.....
    #...#...
    ........
    .....#..
    .#.....#
    ........
..#....#
.#..#...
.......#
#...#...
..#....#
.#..#...
....
#...
..#.
.#..
....
#...
""".split("\n").filter { it.isNotBlank() }

        assertThat(initial.traversed).isEqualTo(expected)
    }


    @Test
    fun `traverse should work bis`() {
        val initial = """
 .
 .
 .
 .
..
..
..
..
.
.
.
.   
""".split("\n").filter { it.isNotBlank() }
        val expected = """
    ........
........
""".split("\n").filter { it.isNotBlank() }

        assertThat(initial.traversed).isEqualTo(expected)
    }
    
    @Test
    fun `should parse the cube sides`() {
        val example2 = """
    ...#.#..
    .#......
    #.....#.
    ........
    ...#
    #...
    ....
    ..#.
..#....#
........
.....#..
........
#...
..#.
....
....

10R5L5R10L4R5L5
"""
        val expectedSides = mapOf(
            SideId.A to Side(listOf(3 x 0, 1 x 1, 0 x 2)),
            SideId.B to Side(listOf(1 x 0, 2 x 2)),
            SideId.C to Side(listOf(3 x 0, 0 x 1, 2 x 3)),
            SideId.D to Side(listOf(3 x 0, 1 x 2)),
            SideId.E to Side(listOf(2 x 0,)),
            SideId.E to Side(listOf(0 x 0, 2 x 1)),
        )

        assertThat(parseSides(example2.split("\n"))).isEqualTo(expectedSides)
    }


    @ParameterizedTest
    @MethodSource
    fun `basic movement`(case: Int, starting: State, movement: Movement, expected: State) {
        assertThat(starting.next(movement)).isEqualTo(expected)
    }

    companion object {

        @JvmStatic
        fun `basic movement`(): Stream<Arguments> = listOf(
            *with(State(parseMaze(List(5) { "....." }.joinToString("\n")), 2, 2, RIGHT)) {
                arrayOf(
                    of(1, dir(RIGHT), TurnRight, dir(DOWN)),
                    of(2, dir(DOWN), TurnRight, dir(LEFT)),
                    of(3, dir(LEFT), TurnRight, dir(UP)),
                    of(4, dir(UP), TurnRight, dir(RIGHT)),
                    of(5, dir(RIGHT), TurnLeft, dir(UP)),
                    of(6, dir(DOWN), TurnLeft, dir(RIGHT)),
                    of(7, dir(LEFT), TurnLeft, dir(DOWN)),
                    of(8, dir(UP), TurnLeft, dir(LEFT)),

                    of(9, dir(RIGHT), Go(1), dir(RIGHT).x(3)),
                    of(10, dir(LEFT), Go(1), dir(LEFT).x(1)),
                    of(11, dir(UP), Go(1), dir(UP).y(1)),
                    of(12, dir(DOWN), Go(1), dir(DOWN).y(3)),


                    of(13, dir(RIGHT), Go(4), dir(RIGHT).x(1)),
                    of(14, dir(LEFT), Go(4), dir(LEFT).x(3)),
                    of(15, dir(UP), Go(4), dir(UP).y(3)),
                    of(16, dir(DOWN), Go(4), dir(DOWN).y(1)),
                )
            },
            *with(parseMaze("...#...").state(1, 0, RIGHT)) {
                arrayOf(
                    of(17, x(1), Go(4), x(2)),
                    of(18, x(4), Go(4), x(1)),
                    of(19, x(2).dir(LEFT), Go(4), x(5).dir(LEFT)),
                    of(20, x(5).dir(LEFT), Go(4), x(4).dir(LEFT)),
                )
            },
            *with(parseMaze("    ...#...").state(1, 0, RIGHT)) {
                arrayOf(
                    of(21, x(5), Go(4), x(6)),
                    of(22, x(8), Go(4), x(5)),
                    of(23, x(6).dir(LEFT), Go(4), x(9).dir(LEFT)),
                    of(24, x(9).dir(LEFT), Go(4), x(8).dir(LEFT)),
                )
            }

        ).stream()
    }
}

private fun Maze.state(x: Int, y: Int, orientation: Orientation): State =
    State(maze = this, x = x, y = y, orientation = orientation)

private fun State.dir(orientation: Orientation): State = copy(orientation = orientation)
private fun State.x(x: Int): State = copy(x = x)
private fun State.y(y: Int): State = copy(y = y)
