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

    override val result2: String
        get() = TODO()

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
        val actual = generateSequence(initialState) { it.next() }.take(after + 1).last()

        assertThat(actual).isEqualTo(expected)
    }

    @ParameterizedTest
    @MethodSource("rocksInitialPosition")
    fun `should position the rocks correctly`(nextRock: Rock, expected: Point, previous: List<RockPosition>) {
        val game = initialState.copy(
            nextRocks =  listOf(nextRock),
            rocksPositions = previous
        )

        assertThat(game.next().rocksPositions.first()).isEqualTo(nextRock.pos(expected.x, expected.y))
    }

    @ParameterizedTest
    @MethodSource("rocksMovements")
    fun `should move the rocks correctly`(initialPos: RockPosition, movement: Movement, expected: Point) {
        val game = initialState.copy(
            nextMovements = listOf(movement),
            nextRocks =  emptyList(),
            rocksPositions = listOf(initialPos)
        )

        assertThat(game.next().rocksPositions.first().point).isEqualTo(expected)
    }

    @ParameterizedTest
    @MethodSource("movementOrchestration")
    fun `should orchestrate the movements`(initial: Game, expected: Game) {
        val actual = initial.next()
        initial.rocksPositions.dump("initial")
        expected.rocksPositions.dump("expected")
        actual.rocksPositions.dump("actual")
        assertThat(actual).isEqualTo(expected)
    }

    @ParameterizedTest
    @MethodSource("positionOverlaps")
    fun `should detect an overlap`(one: RockPosition, other: RockPosition, expected: Boolean) {
        assertThat(one.overlaps(other)).isEqualTo(expected)
    }

    @Test
    fun testSomething() {
        generateSequence(initialState) {
            it.next()
        }.onEach { it.rocksPositions.dump() }.take(100).count()

    }


    companion object {
        const val example: String = ">>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>"
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
        fun movements(): Stream<Arguments> = with(initialState) {
            Stream.of(
                of(
                    1, copy(
                        nextMovements = nextMovements,
                        nextRocks = nextRocks.drop(1),
                        rocksPositions = listOf(HorizontalLine.pos(2, 3))
                    )
                ),
                of(
                    2, copy(
                        nextMovements = nextMovements.drop(1),
                        nextRocks = nextRocks.drop(1),
                        rocksPositions = listOf(HorizontalLine.pos(3, 2))
                    )
                ),
                of(
                    3, copy(
                        nextMovements = nextMovements.drop(2),
                        nextRocks = nextRocks.drop(1),
                        rocksPositions = listOf(HorizontalLine.pos(3, 1))
                    )
                ),
                of(
                    4, copy(
                        nextMovements = nextMovements.drop(3),
                        nextRocks = nextRocks.drop(1),
                        rocksPositions = listOf(HorizontalLine.pos(3, 0))
                    )
                ),
                of(
                    5, copy(
                        nextMovements = nextMovements.drop(4),
                        nextRocks = nextRocks.drop(1),
                        rocksPositions = listOf(HorizontalLine.pos(2, 0).stop())
                    )
                ),
            )
        }

        @JvmStatic
        fun movementOrchestration(): Stream<Arguments> = Stream.of(
            testCase {
                val initialPositions = listOf(HorizontalLine.pos(0, 0).stop())
                val initial = copy(
                    nextRocks = listOf(Plus),
                    rocksPositions = initialPositions
                )
                val expected = copy(
                    nextRocks = rocksOrder,
                    rocksPositions = initialPositions + Plus.pos(4)
                )
                initial to expected
            },
            testCase {
                val initial = copy(
                    nextMovements = listOf(Right),
                    rocksPositions = listOf(HorizontalLine.pos(0, 2))
                )
                val expected = copy(
                    nextMovements = movements,
                    rocksPositions = listOf(HorizontalLine.pos(1, 1))
                )
                initial to expected
            },
            testCase {
                val otherRocks = listOf(HorizontalLine.pos(3, 1).stop())
                val initial = copy(
                    nextMovements = listOf(Right),
                    rocksPositions = otherRocks + Plus.pos(0, 1)
                )
                val expected = copy(
                    nextMovements = movements,
                    rocksPositions =  otherRocks + Plus.pos(1, 1).stop()
                )
                initial to expected
            },
            testCase {
                val otherRocks = listOf(HorizontalLine.pos(2, 0).stop())
                val initial = copy(
                    nextMovements = listOf(Left),
                    rocksPositions = otherRocks + Plus.pos(2, 1)
                )
                val expected = copy(
                    nextMovements = movements,
                    rocksPositions =  otherRocks + Plus.pos(1, 1).stop()
                )
                initial to expected
            },
        )

        @JvmStatic
        fun rocksInitialPosition(): Stream<Arguments> = Stream.of(
            of(HorizontalLine, Point(2, 3), emptyList<RockPosition>()),
            of(HorizontalLine, Point(2, 4), listOf(HorizontalLine.pos(0, 0).stop())),
            of(HorizontalLine, Point(2, 5), listOf(HorizontalLine.pos(3, 1).stop())),
            of(HorizontalLine, Point(2, 6), listOf(HorizontalLine.pos(2, 2).stop())),
            of(Plus, Point(2, 3), emptyList<RockPosition>()),
            of(Plus, Point(2, 5), listOf(HorizontalLine.pos(0, 1).stop())),
            of(InvertedL, Point(2, 3), emptyList<RockPosition>()),
            of(InvertedL, Point(2, 5), listOf(HorizontalLine.pos(0, 1).stop())),
            of(VerticalLine, Point(2, 3), emptyList<RockPosition>()),
            of(VerticalLine, Point(2, 5), listOf(HorizontalLine.pos(0, 1).stop())),
            of(Square, Point(2, 3), emptyList<RockPosition>()),
            of(Square, Point(2, 5), listOf(HorizontalLine.pos(0, 1).stop())),

            of(HorizontalLine, Point(2, 6), listOf(Plus.pos(0, 0).stop())),
            of(HorizontalLine, Point(2, 8), listOf(Plus.pos(0, 2).stop())),

            of(HorizontalLine, Point(2, 6), listOf(InvertedL.pos(0, 0).stop())),
            of(HorizontalLine, Point(2, 8), listOf(InvertedL.pos(0, 2).stop())),

            of(HorizontalLine, Point(2, 7), listOf(VerticalLine.pos(0, 0).stop())),
            of(HorizontalLine, Point(2, 9), listOf(VerticalLine.pos(0, 2).stop())),

            of(HorizontalLine, Point(2, 5), listOf(Square.pos(0, 0).stop())),
            of(HorizontalLine, Point(2, 7), listOf(Square.pos(0, 2).stop())),
        )

        @JvmStatic
        fun moveRockHorizontal(): Stream<Arguments> = Stream.of(
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
            of(InvertedL.pos(4, 0), Right, Point(5, 0)),
            of(InvertedL.pos(5, 0), Right, Point(5, 0)),
            of(InvertedL.pos(0, 0), Left, Point(0, 0)),
            of(InvertedL.pos(1, 0), Left, Point(0, 0)),
            of(InvertedL.pos(2, 0), Left, Point(1, 0)),
            of(InvertedL.pos(3, 0), Left, Point(2, 0)),
            of(InvertedL.pos(4, 0), Left, Point(3, 0)),
            of(InvertedL.pos(5, 0), Left, Point(4, 0)),

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
            initial(initialState).let { (initial, expected) ->
                of(initial, expected)
            }
    }


}
