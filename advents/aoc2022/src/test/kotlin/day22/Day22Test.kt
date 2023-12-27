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
    override val example2: String = """
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

    override val result1: String = "6032"

    override val result2: String = "10006"

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
        val expectedCube = Cube(
            width = 4,
            sideA = Side(4, Point(4, 0), listOf(3 x 0, 1 x 1, 0 x 2)),
            sideB = Side(4, Point(8, 0), listOf(1 x 0, 2 x 2)),
            sideC = Side(4, Point(4, 4), listOf(3 x 0, 0 x 1, 2 x 3)),
            sideD = Side(4, Point(4, 8), listOf(3 x 0, 1 x 2)),
            sideE = Side(4, Point(0, 8), listOf(2 x 0)),
            sideF = Side(4, Point(0, 12), listOf(0 x 0, 2 x 1)),
        )

        assertThat(parseCube(example2.split("\n"))).isEqualTo(expectedCube)
    }

    @ParameterizedTest
    @MethodSource("lanesRocks")
    fun `the lanes should be right`(lane: (Cube) -> LanePosition, rocks: List<Int>, position: Int, way: Way) {
        val cube = parseCube(example2.split("\n"))

        assertThat(lane(cube).lane.rocksAt).hasSameElementsAs(rocks)
        assertThat(lane(cube).position).isEqualTo(position)
        assertThat(lane(cube).way).isEqualTo(way)
    }


    @ParameterizedTest
    @MethodSource
    fun `basic movement`(case: Int, starting: State, movement: Movement, expected: State) {
        assertThat(starting.next(movement)).isEqualTo(expected)
    }

    companion object {

        @JvmStatic
        fun lanesRocks(): Stream<Arguments> = Stream.of(
            of(
                { cube: Cube -> cube.getLanePosition(Point(4,0), DOWN)},
                listOf(2, 5, 15),
                0,
                Way.INC,
            ),
            of(
                { cube: Cube -> cube.getLanePosition(Point(5,2), RIGHT)},
                listOf(0, 6),
                1,
                Way.INC,
            ),
            of(
                { cube: Cube -> cube.getLanePosition(Point(10,1), UP)},
                listOf(2, 8, 13),
                1,
                Way.DEC,
            ),
            of(
                { cube: Cube -> cube.getLanePosition(Point(9,0), LEFT)},
                listOf(3, 5),
                5,
                Way.DEC,
            ),
            of(
                { cube: Cube -> cube.getLanePosition(Point(5,5), DOWN)},
                listOf(1, 10, 13),
                5,
                Way.INC
            ),
            of(
                { cube: Cube -> cube.getLanePosition(Point(6,6), RIGHT)},
                listOf(2, 8, 13),
                5,
                Way.DEC
            ),
            of(
                { cube: Cube -> cube.getLanePosition(Point(5,9), DOWN)},
                listOf(1, 10, 13),
                9,
                Way.INC
            ),
            of(
                { cube: Cube -> cube.getLanePosition(Point(6,10), LEFT)},
                listOf(1, 10),
                9,
                Way.INC
            ),
            of(
                { cube: Cube -> cube.getLanePosition(Point(2,9), UP)},
                listOf(2, 8, 13),
                9,
                Way.DEC
            ),
            of(
                { cube: Cube -> cube.getLanePosition(Point(0,8), LEFT)},
                listOf(8, 13),
                15,
                Way.INC
            ),
            of(
                { cube: Cube -> cube.getLanePosition(Point(2,13), UP)},
                listOf(2, 8, 13),
                13,
                Way.DEC
            ),
            of(
                { cube: Cube -> cube.getLanePosition(Point(0,15), RIGHT)},
                listOf(0, 4, 8),
                15,
                Way.DEC
            ),
        )

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
