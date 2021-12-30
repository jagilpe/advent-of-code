package com.gilpereda.adventsofcode.adventsofcode2021.day23

import com.gilpereda.adventsofcode.adventsofcode2021.BaseTest
import com.gilpereda.adventsofcode.adventsofcode2021.Executable
import com.gilpereda.adventsofcode.adventsofcode2021.day23.Amphipod.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.of
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class Day23OldTest : BaseTest() {

    override val example: String = """
        #############
        #...........#
        ###B#C#B#D###
        ###A#D#C#A###
        #############
        """.trimIndent()

    override val result1: String = "12521"

    override val result2: String = "44169"

    override val input: String = "/day23/input.txt"

    override val run1: Executable = part1Old

    override val run2: Executable = part2Old

    @Test
    fun `should parse the input`() {
        val expected = OldGame(listOf(
            listOf(Wall, Wall, Wall, Wall, Wall, Wall, Wall, Wall, Wall, Wall, Wall, Wall, Wall),
            listOf(Wall, OldHall(), OldHall(), Door, OldHall(), Door, OldHall(), Door, OldHall(), Door, OldHall(), OldHall(), Wall),
            listOf(Wall, Wall, Wall, OldRoom(AMBER, BRONZE), Wall, OldRoom(BRONZE, COPPER), Wall, OldRoom(COPPER, BRONZE), Wall, OldRoom(DESERT, DESERT), Wall, Wall, Wall),
            listOf(Wall, Wall, Wall, OldRoom(AMBER, AMBER), Wall, OldRoom(BRONZE, DESERT), Wall, OldRoom(COPPER, COPPER), Wall, OldRoom(DESERT, AMBER), Wall, Wall, Wall),
            listOf(Wall, Wall, Wall, Wall, Wall, Wall, Wall, Wall, Wall, Wall, Wall, Wall, Wall),
        ))
        assertThat(parse(example.splitToSequence("\n"))).isEqualTo(expected)
    }

    @ParameterizedTest
    @MethodSource("finished")
    fun `should check if the game is finished`(game: OldGame, finished: Boolean) {
        assertThat(game.finished).isEqualTo(finished)
    }

    @ParameterizedTest
    @MethodSource("movementsTo")
    fun `should get if a movement is possible`(m: String, game: OldGame, origin: Point, destination: Point, possible: Boolean) {
        assertThat(game.canMoveTo(origin, destination)).isEqualTo(possible)
    }


    @ParameterizedTest
    @MethodSource("movementsThrough")
    fun `should get if a movement through a cell is possible`(m: String, game: OldGame, cell: Point, possible: Boolean) {
        assertThat(game.canMoveThrough(cell)).isEqualTo(possible)
    }

    @Test
    fun `should not continue if no movement is possible`() {
        val deadGame = initialGame.cellMap.map { row -> row.map { cell ->
            when (cell) {
                is OldRoom -> cell.apply { revisited = true }
                else -> cell
            }
        }}.let(::OldGame)

        assertThat(deadGame.nextMoves().toList()).isEmpty()
    }

    companion object {
        @JvmStatic
        fun movementsTo(): Stream<Arguments> = Stream.of(
            of("From wall", initialGame, Point(2, 0), Point(4, 1), false),
            of("From room to free hall", initialGame, Point(3, 2), Point(4, 1), true),
            of("From hall valid cell", otherGame, Point(10, 1), Point(7, 3), true),
            of("From hall invalid cell", otherGame, Point(6, 1), Point(7, 3), false),
            of("From hall to partially occupied room invalid", otherGame, Point(6, 1), Point(5, 2), false),
            of("From hall to partially occupied room valid", otherGame, Point(2, 1), Point(3, 2), true),
            of("To a room's door", otherGame, Point(2, 1), Point(3, 1), false),
            of("Between hall points", otherGame, Point(2, 1), Point(4, 1), false),
        )

        @JvmStatic
        fun movementsThrough(): Stream<Arguments> = Stream.of(
            of("Through wall", initialGame, Point(2, 0), false),
            of("Through empty room cell", otherGame, Point(3, 2), true),
            of("Through occupied room cell", otherGame, Point(9, 2), false),
            of("Through room door", otherGame, Point(5, 1), true),
            of("Through free hall cell", otherGame, Point(4, 1), true),
            of("Through occupied hall cell", otherGame, Point(6, 1), false),
        )

        @JvmStatic
        fun finished(): Stream<Arguments> = Stream.of(
            of(initialGame, false),
            of(otherGame, false),
            of(finishedGame, true),
            of(finishedGamePart2, true),
        )
    }

}

private val initialGame: OldGame = """
            #############
            #...........#
            ###B#C#B#D###
            ###A#D#C#A###
            #############
        """.trimIndent().splitToSequence("\n").let(::parse)

private val otherGame: OldGame = """
            #############
            #.A...B...C.#
            ###.#.#.#D###
            ###A#D#.#A###
            #############
        """.trimIndent().splitToSequence("\n").let(::parse)

private val finishedGame: OldGame = """
            #############
            #...........#
            ###A#B#C#D###
            ###A#B#C#D###
            #############
        """.trimIndent().splitToSequence("\n").let(::parse)

private val finishedGamePart2: OldGame = """
            #############
            #...........#
            ###A#B#C#D###
            ###A#B#C#D###
            ###A#B#C#D###
            ###A#B#C#D###
            #############
        """.trimIndent().splitToSequence("\n").let(::parse)