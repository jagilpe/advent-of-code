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

class Day23Test : BaseTest() {

    override val example: String = """
        #############
        #...........#
        ###B#C#B#D###
        ###A#D#C#A###
        #############
        """.trimIndent()

    override val result1: String = ""

    override val result2: String
        get() = TODO("Not yet implemented")
    override val input: String = "/day23/input.txt"

    override val run1: Executable = part1

    override val run2: Executable = part2

    @Test
    fun `should parse the input`() {
        val expected = Game(listOf(
            listOf(Wall, Wall, Wall, Wall, Wall, Wall, Wall, Wall, Wall, Wall, Wall, Wall, Wall),
            listOf(Wall, Hall(), Hall(), Door, Hall(), Door, Hall(), Door, Hall(), Door, Hall(), Hall(), Wall),
            listOf(Wall, Wall, Wall, Room(AMBER, BRONZE), Wall, Room(BRONZE, COPPER), Wall, Room(COPPER, BRONZE), Wall, Room(DESERT, DESERT), Wall, Wall, Wall),
            listOf(Wall, Wall, Wall, Room(AMBER, AMBER), Wall, Room(BRONZE, DESERT), Wall, Room(COPPER, COPPER), Wall, Room(DESERT, AMBER), Wall, Wall, Wall),
            listOf(Wall, Wall, Wall, Wall, Wall, Wall, Wall, Wall, Wall, Wall, Wall, Wall, Wall),
        ))
        assertThat(parse(example.splitToSequence("\n"))).isEqualTo(expected)
    }

    @ParameterizedTest
    @MethodSource("finished")
    fun `should check if the game is finished`(game: Game, finished: Boolean) {
        assertThat(game.finished).isEqualTo(finished)
    }

    @ParameterizedTest
    @MethodSource("movementsTo")
    fun `should get if a movement is possible`(m: String, game: Game, origin: Point, destination: Point, possible: Boolean) {
        assertThat(game.canMoveTo(origin, destination)).isEqualTo(possible)
    }


    @ParameterizedTest
    @MethodSource("movementsThrough")
    fun `should get if a movement through a cell is possible`(m: String, game: Game, cell: Point, possible: Boolean) {
        assertThat(game.canMoveThrough(cell)).isEqualTo(possible)
    }

    @Test
    fun `should not continue if no movement is possible`() {
        val deadGame = initialGame.cellMap.map { row -> row.map { cell ->
            when (cell) {
                is Room -> cell.apply { revisited = true }
                else -> cell
            }
        }}.let(::Game)

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

private val initialGame: Game = """
            #############
            #...........#
            ###B#C#B#D###
            ###A#D#C#A###
            #############
        """.trimIndent().splitToSequence("\n").let(::parse)

private val otherGame: Game = """
            #############
            #.A...B...C.#
            ###.#.#.#D###
            ###A#D#.#A###
            #############
        """.trimIndent().splitToSequence("\n").let(::parse)

private val finishedGame: Game = """
            #############
            #...........#
            ###A#B#C#D###
            ###A#B#C#D###
            #############
        """.trimIndent().splitToSequence("\n").let(::parse)

private val finishedGamePart2: Game = """
            #############
            #...........#
            ###A#B#C#D###
            ###A#B#C#D###
            ###A#B#C#D###
            ###A#B#C#D###
            #############
        """.trimIndent().splitToSequence("\n").let(::parse)