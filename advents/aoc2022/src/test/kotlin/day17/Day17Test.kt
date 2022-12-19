package com.gilpereda.aoc2022.day17

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.of
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

/**
 * 3064 -> too low
 */
class Day17Test : BaseTest() {
    override val example: String = Day17Test.example

    override val result1: String = "3068"

    override val result2: String = "1514285714288"

    override val input: String = "/day17/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask

    @Test
    fun `should parse the input`() {
        val parsed = ">>><<><>>".parsed()
        val expected = listOf(
            Right, Right, Right, Left, Left, Right, Left, Right, Right
        )

        assertThat(parsed).isEqualTo(expected)
    }

    @ParameterizedTest
    @MethodSource("movements")
    fun `should move the rocks`(after: Int, expected: Game) {
        val actual = generateSequence(initialState.copy()) { game ->
            game.next()
                .also { it.dump() }
        }.take(after + 1).last()

        assertThat(actual).isEqualTo(expected)
    }

    @ParameterizedTest
    @MethodSource("rocksInitialPosition")
    fun `should position the rocks correctly`(nextRock: Rock, expected: Point, previous: SqueezedRocks) {
        val game = initialState.copy(
            rocks =  listOf(nextRock),
            squeezedRocks = previous,
        )

        assertThat(game.next().movingRock).isEqualTo(nextRock.pos(expected.x, expected.y))
    }

    @ParameterizedTest
    @MethodSource("rocksMovements")
    fun `should move the rocks correctly`(initialPos: RockPosition, movement: Movement, expected: Point) {
        val game = initialState.copy(
            movements = listOf(movement),
            movingRock = initialPos,
        )

        assertThat(game.next().movingRock!!.point).isEqualTo(expected)
    }

    @ParameterizedTest
    @MethodSource("movementOrchestration")
    fun `should orchestrate the movements`(initial: Game, expected: Game) {
        val actual = initial.next()
        assertThat(actual).isEqualTo(expected)
    }

    @ParameterizedTest
    @MethodSource("positionOverlaps")
    fun `should detect an overlap`(one: RockPosition, other: RockPosition, expected: Boolean) {
        assertThat(one.overlaps(SqueezedRocks(other.points.toMutableSet()))).isEqualTo(expected)
    }

    companion object {
//        const val example: String = ">>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>"
        const val example: String =   ">>><<><>"
        private val initialState = Game(movements = example.parsed())

        @JvmStatic
        fun positionOverlaps(): Stream<Arguments> = Stream.of(
            of(HorizontalLine.pos(0, 0), VerticalLine.pos(0,0), true),
            of(HorizontalLine.pos(0, 0), VerticalLine.pos(1,0), true),
            of(HorizontalLine.pos(0, 0), VerticalLine.pos(2,0), true),
            of(HorizontalLine.pos(0, 0), VerticalLine.pos(3,0), true),
            of(HorizontalLine.pos(0, 0), VerticalLine.pos(4,0), false),
            of(HorizontalLine.pos(0, 0), VerticalLine.pos(5,0), false),
            of(HorizontalLine.pos(0, 0), VerticalLine.pos(6,0), false),
        )

        @JvmStatic
        fun movements(): Stream<Arguments> = with(initialState.copy()) {
            Stream.of(
                of(
                    1, copy(
                        movementIndex = 0,
                        rockIndex = 1,
                        movingRock = HorizontalLine.pos(2, 3)
                    )
                ),
                of(
                    2, copy(
                        movementIndex = 1,
                        rockIndex = 1,
                        movingRock = HorizontalLine.pos(3, 2)
                    )
                ),
                of(
                    3, copy(
                        movementIndex = 2,
                        rockIndex = 1,
                        movingRock = HorizontalLine.pos(3, 1)
                    )
                ),
                of(
                    4, copy(
                        movementIndex = 3,
                        rockIndex = 1,
                        movingRock = HorizontalLine.pos(3, 0)
                    )
                ),
                of(
                    5, copy(
                        movementIndex = 4,
                        rockIndex = 1,
                        movingRock = HorizontalLine.pos(2, 0).stop()
                    )
                ),
            )
        }

        @JvmStatic
        fun movementOrchestration(): Stream<Arguments> = Stream.of(
            testCase {
                val previousRock = HorizontalLine.pos(0, 0).stop()
                val initial = copy(
                    rockIndex = 1,
                    movingRock = previousRock,
                    squeezedRocks = SqueezedRocks()
                )
                val expected = copy(
                    rockIndex = 2,
                    movingRock = Plus.pos(4),
                    squeezedRocks = SqueezedRocks.of(previousRock.points),
                    stoppedRocks = 1,
                )
                initial to expected
            },
            testCase {
                val initial = copy(
                    movements = listOf(Left, Right),
                    movementIndex = 1,
                    squeezedRocks = SqueezedRocks(),
                    movingRock = HorizontalLine.pos(0, 2)
                )
                val expected = copy(
                    movements = listOf(Left, Right),
                    movementIndex = 0,
                    squeezedRocks = SqueezedRocks(),
                    movingRock = HorizontalLine.pos(1, 1)
                )
                initial to expected
            },
            testCase {
                val otherRocks = SqueezedRocks.of(HorizontalLine.pos(3, 1).points)
                val initial = copy(
                    movements = listOf(Left, Right),
                    movementIndex = 1,
                    squeezedRocks = otherRocks,
                    movingRock = Plus.pos(0, 1)
                )
                val expected = initial.copy(
                    movements = listOf(Left, Right),
                    movementIndex = 0,
                    squeezedRocks = otherRocks,
                    movingRock = Plus.pos(1, 1).stop()
                )
                initial to expected
            },
            testCase {
                val otherRocks = SqueezedRocks.of(HorizontalLine.pos(2, 0).points)
                val initial = copy(
                    movements = listOf(Right, Left),
                    movementIndex = 1,
                    squeezedRocks = otherRocks,
                    movingRock = Plus.pos(2, 1)
                )
                val expected = copy(
                    movements = listOf(Right, Left),
                    movementIndex = 0,
                    squeezedRocks = otherRocks,
                    movingRock =  Plus.pos(1, 1).stop()
                )
                initial to expected
            },
        )

        @JvmStatic
        fun rocksInitialPosition(): Stream<Arguments> = Stream.of(
            of(HorizontalLine, Point(2, 3), SqueezedRocks()),
            of(HorizontalLine, Point(2, 4), SqueezedRocks.of(HorizontalLine.pos(0, 0).points)),
            of(HorizontalLine, Point(2, 5), SqueezedRocks.of(HorizontalLine.pos(3, 1).points)),
            of(HorizontalLine, Point(2, 6), SqueezedRocks.of(HorizontalLine.pos(2, 2).points)),
            of(Plus, Point(2, 3), SqueezedRocks()),
            of(Plus, Point(2, 5), SqueezedRocks.of(HorizontalLine.pos(0, 1).points)),
            of(InvertedL, Point(2, 3), SqueezedRocks()),
            of(InvertedL, Point(2, 5), SqueezedRocks.of(HorizontalLine.pos(0, 1).points)),
            of(VerticalLine, Point(2, 3), SqueezedRocks()),
            of(VerticalLine, Point(2, 5), SqueezedRocks.of(HorizontalLine.pos(0, 1).points)),
            of(Square, Point(2, 3), SqueezedRocks()),
            of(Square, Point(2, 5), SqueezedRocks.of(HorizontalLine.pos(0, 1).points)),

            of(HorizontalLine, Point(2, 6), SqueezedRocks.of(Plus.pos(0, 0).points)),
            of(HorizontalLine, Point(2, 8), SqueezedRocks.of(Plus.pos(0, 2).points)),

            of(HorizontalLine, Point(2, 6), SqueezedRocks.of(InvertedL.pos(0, 0).points)),
            of(HorizontalLine, Point(2, 8), SqueezedRocks.of(InvertedL.pos(0, 2).points)),

            of(HorizontalLine, Point(2, 7), SqueezedRocks.of(VerticalLine.pos(0, 0).points)),
            of(HorizontalLine, Point(2, 9), SqueezedRocks.of(VerticalLine.pos(0, 2).points)),

            of(HorizontalLine, Point(2, 5), SqueezedRocks.of(Square.pos(0, 0).points)),
            of(HorizontalLine, Point(2, 7), SqueezedRocks.of(Square.pos(0, 2).points)),
        )

        @JvmStatic
        fun rocksMovements(): Stream<Arguments> = Stream.of(
            of(HorizontalLine.pos(0, 0), Right, Point(1, 0)),
            of(HorizontalLine.pos(1, 0), Right, Point(2, 0)),
            of(HorizontalLine.pos(2, 0), Right, Point(3, 0)),
            of(HorizontalLine.pos(3, 0), Right, Point(3, 0)),
            of(HorizontalLine.pos(0, 0), Left, Point(0, 0)),
            of(HorizontalLine.pos(1, 0), Left, Point(0, 0)),
            of(HorizontalLine.pos(2, 0), Left, Point(1, 0)),
            of(HorizontalLine.pos(3, 0), Left, Point(2, 0)),

            of(Plus.pos(0, 0), Right, Point(1, 0)),
            of(Plus.pos(1, 0), Right, Point(2, 0)),
            of(Plus.pos(2, 0), Right, Point(3, 0)),
            of(Plus.pos(3, 0), Right, Point(4, 0)),
            of(Plus.pos(4, 0), Right, Point(4, 0)),
            of(Plus.pos(0, 0), Left, Point(0, 0)),
            of(Plus.pos(1, 0), Left, Point(0, 0)),
            of(Plus.pos(2, 0), Left, Point(1, 0)),
            of(Plus.pos(3, 0), Left, Point(2, 0)),
            of(Plus.pos(4, 0), Left, Point(3, 0)),

            of(InvertedL.pos(0, 0), Right, Point(1, 0)),
            of(InvertedL.pos(1, 0), Right, Point(2, 0)),
            of(InvertedL.pos(2, 0), Right, Point(3, 0)),
            of(InvertedL.pos(3, 0), Right, Point(4, 0)),
            of(InvertedL.pos(4, 0), Right, Point(4, 0)),
            of(InvertedL.pos(0, 0), Left, Point(0, 0)),
            of(InvertedL.pos(1, 0), Left, Point(0, 0)),
            of(InvertedL.pos(2, 0), Left, Point(1, 0)),
            of(InvertedL.pos(3, 0), Left, Point(2, 0)),
            of(InvertedL.pos(4, 0), Left, Point(3, 0)),

            of(VerticalLine.pos(0, 0), Right, Point(1, 0)),
            of(VerticalLine.pos(1, 0), Right, Point(2, 0)),
            of(VerticalLine.pos(2, 0), Right, Point(3, 0)),
            of(VerticalLine.pos(3, 0), Right, Point(4, 0)),
            of(VerticalLine.pos(4, 0), Right, Point(5, 0)),
            of(VerticalLine.pos(5, 0), Right, Point(6, 0)),
            of(VerticalLine.pos(6, 0), Right, Point(6, 0)),
            of(VerticalLine.pos(0, 0), Left, Point(0, 0)),
            of(VerticalLine.pos(1, 0), Left, Point(0, 0)),
            of(VerticalLine.pos(2, 0), Left, Point(1, 0)),
            of(VerticalLine.pos(3, 0), Left, Point(2, 0)),
            of(VerticalLine.pos(4, 0), Left, Point(3, 0)),
            of(VerticalLine.pos(5, 0), Left, Point(4, 0)),
            of(VerticalLine.pos(6, 0), Left, Point(5, 0)),

            of(Square.pos(0, 0), Right, Point(1, 0)),
            of(Square.pos(1, 0), Right, Point(2, 0)),
            of(Square.pos(2, 0), Right, Point(3, 0)),
            of(Square.pos(3, 0), Right, Point(4, 0)),
            of(Square.pos(4, 0), Right, Point(5, 0)),
            of(Square.pos(5, 0), Right, Point(5, 0)),
            of(Square.pos(0, 0), Left, Point(0, 0)),
            of(Square.pos(1, 0), Left, Point(0, 0)),
            of(Square.pos(2, 0), Left, Point(1, 0)),
            of(Square.pos(3, 0), Left, Point(2, 0)),
            of(Square.pos(4, 0), Left, Point(3, 0)),
            of(Square.pos(5, 0), Left, Point(4, 0)),
        )

        private fun testCase(
            initial: Game.() -> Pair<Game, Game>,
        ): Arguments =
            initial(initialState.copy()).let { (initial, expected) ->
                of(initial, expected)
            }
    }


}
