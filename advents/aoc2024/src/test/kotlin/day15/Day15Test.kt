package com.gilpereda.aoc2024.day15

import com.gilpereda.adventofcode.commons.geometry.Orientation
import com.gilpereda.aoc2024.BaseTest
import com.gilpereda.aoc2024.Executable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class Day15Test : BaseTest() {
    override val example: String =
        """
        ##########
        #..O..O.O#
        #......O.#
        #.OO..O.O#
        #..O@..O.#
        #O#..O...#
        #O..O..O.#
        #.OO.O.OO#
        #....O...#
        ##########

        <vv>^<v^>v>^vv^v>v<>v^v<v<^vv<<<^><<><>>v<vvv<>^v^>^<<<><<v<<<v^vv^v>^
        vvv<<^>^v^^><<>>><>^<<><^vv^^<>vvv<>><^^v>^>vv<>v<<<<v<^v>^<^^>>>^<v<v
        ><>vv>v^v^<>><>>>><^^>vv>v<^^^>>v^v^<^^>v^^>v^<^v>v<>>v^v^<v>v^^<^^vv<
        <<v<^>>^^^^>>>v^<>vvv^><v<<<>^^^vv^<vvv>^>v<^^^^v<>^>vvvv><>>v^<<^^^^^
        ^><^><>>><>^^<<^^v>>><^<v>^<vv>>v>>>^v><>^v><<<<v>>v<v<v>vvv>^<><<>^><
        ^>><>^v<><^vvv<^^<><v<<<<<><^v<<<><<<^^<v<^^^><^>>^<v^><<<^>>^v<v^v<v^
        >^>>^v>vv>^<<^v<>><<><<v<<v><>v<^vv<<<>^^v^>^^>>><<^v>>v^v><^^>>^<>vv^
        <><^^>^^^<><vvvvv^v<v<<>^v<v>v<<^><<><<><<<^^<<<^<<>><<><^^^>^^<>^>v<>
        ^^>vv<^v^v<vv>^<><v<^v>^^^>>>^^vvv^>vvv<>>>^<^>>>>>^<<^v>^vvv<>^<><<v>
        v^^>>><<^^<>>^v^<v^vv<>v^<<>^<^v^v><^<<<><<^<v><v<>vv>>v><v^<vv<>v^<<^
        """.trimIndent()

    override val resultExample1: String = "10092"

    override val resultReal1: String = "1349898"

    override val resultExample2: String = "9021"

    override val resultReal2: String = ""

    override val input: String = "/day15/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask

    @ParameterizedTest
    @MethodSource("test_cases")
    fun `should move the robot`(
        initial: String,
        orientation: Orientation,
        expected: String,
    ) {
        val initialGame = renderGame(initial)
        val expectedGame = renderGame(expected)

        assertThat(initialGame.move(orientation)).isEqualTo(expectedGame)
    }

    private fun test_cases(): Stream<Arguments> =
        Stream.of(
//            test_case(
//                """
//                ......
//                ..O@..
//                ......
//                """.trimIndent(),
//                Orientation.WEST,
//                """
//                ......
//                .O@...
//                ......
//                """.trimIndent(),
//            ),
//            test_case(
//                """
//                ......
//                ..[]..
//                ..@...
//                """.trimIndent(),
//                Orientation.NORTH,
//                """
//                ..[]..
//                ..@...
//                ......
//                """.trimIndent(),
//            ),
//            test_case(
//                """
//                #......
//                #[][]@.
//                #......
//                """.trimIndent(),
//                Orientation.WEST,
//                """
//                #......
//                #[][]@.
//                #......
//                """.trimIndent(),
//            ),
//            test_case(
//                """
//                ............
//                ..[][][]@...
//                ............
//                """.trimIndent(),
//                Orientation.WEST,
//                """
//                ............
//                .[][][]@....
//                ............
//                """.trimIndent(),
//            ),
//            test_case(
//                """
//                ......
//                ..[]..
//                ..[]..
//                ..@...
//                """.trimIndent(),
//                Orientation.NORTH,
//                """
//                ..[]..
//                ..[]..
//                ..@...
//                ......
//                """.trimIndent(),
//            ),
            test_case(
                """
                ......
                ..[]..
                .[][].
                ..[]..
                ..@...
                """.trimIndent(),
                Orientation.NORTH,
                """
                ..[]..
                .[][].
                ..[]..
                ..@...
                ......
                """.trimIndent(),
            ),
        )

    private fun test_case(
        initial: String,
        orientation: Orientation,
        expected: String,
    ): Arguments = Arguments.of(initial, orientation, expected)
}
