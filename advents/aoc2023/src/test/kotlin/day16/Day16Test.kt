package com.gilpereda.aoc2022.day16

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable
import com.gilpereda.aoc2022.day16.Tile.*
import com.gilpereda.aoc2022.utils.Orientation.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.of
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class Day16Test : BaseTest() {
    override val example: String = """
        .|...\....
        |.-.\.....
        .....|-...
        ........|.
        ..........
        .........\
        ..../.\\..
        .-.-/..|..
        .|....-|.\
        ..//.|....
    """.trimIndent()

    override val resultExample1: String = "46"

    override val resultExample2: String = "51"

    override val resultReal1: String = "8539"

    override val resultReal2: String = "8674"

    override val input: String = "/day16/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask

    @ParameterizedTest
    @MethodSource("tilesEncoding")
    fun `should encode the tiles correctly`(tile: Tile, byte: Int) {
        assertThat(Tile.tileToByte(tile)).isEqualTo(byte.toUByte())
    }

    @ParameterizedTest
    @MethodSource("tilesEncoding")
    fun `should decode the tiles correctly`(tile: Tile, byte: Int) {
        assertThat(Tile.byteToTile(byte.toUByte())).isEqualTo(tile)
    }

    @ParameterizedTest
    @MethodSource("tilesEncoding")
    fun `should encode and decode the tiles correctly`(tile: Tile, byte: Int) {
        assertThat(Tile.byteToTile(Tile.tileToByte(tile))).isEqualTo(tile)
    }

    companion object {
        @JvmStatic
        fun tilesEncoding(): Stream<Arguments> = Stream.of(
            of(Empty(false, emptySet()), 0 + 0 * 8 + 0 * 16),
            of(Empty(false, setOf(EAST)), 0 + 0 * 8 + 1 * 16),
            of(Empty(false, setOf(WEST)), 0 + 0 * 8 + 2 * 16),
            of(Empty(false, setOf(SOUTH)), 0 + 0 * 8 + 4 * 16),
            of(Empty(false, setOf(NORTH)), 0 + 0 * 8 + 8 * 16),
            of(Empty(false, setOf(EAST, WEST)), 0 + 0 * 8 + 3 * 16),
            of(Empty(false, setOf(WEST, NORTH)), 0 + 0 * 8 + 10 * 16),
            of(Empty(false, setOf(EAST, NORTH, SOUTH, WEST)), 0 + 0 * 8 + 15 * 16),
            of(Empty(false, setOf(SOUTH, EAST)), 0 + 0 * 8 + 5 * 16),
            of(Empty(true, emptySet()), 0 + 1 * 8 + 0 * 16),
            of(Empty(true, setOf(EAST)), 0 + 1 * 8 + 1 * 16),
            of(Empty(true, setOf(WEST)), 0 + 1 * 8 + 2 * 16),
            of(Empty(true, setOf(SOUTH)), 0 + 1 * 8 + 4 * 16),
            of(Empty(true, setOf(NORTH)), 0 + 1 * 8 + 8 * 16),
            of(Empty(true, setOf(EAST, WEST)), 0 + 1 * 8 + 3 * 16),
            of(Empty(true, setOf(WEST, NORTH)), 0 + 1 * 8 + 10 * 16),
            of(Empty(true, setOf(EAST, NORTH, SOUTH, WEST)), 0 + 1 * 8 + 15 * 16),
            of(Empty(true, setOf(SOUTH, EAST)), 0 + 1 * 8 + 5 * 16),
            of(MirrorRight(false, emptySet()), 1 + 0 * 8 + 0 * 16),
            of(MirrorRight(false, setOf(EAST)), 1 + 0 * 8 + 1 * 16),
            of(MirrorRight(false, setOf(WEST)), 1 + 0 * 8 + 2 * 16),
            of(MirrorRight(false, setOf(SOUTH)), 1 + 0 * 8 + 4 * 16),
            of(MirrorRight(false, setOf(NORTH)), 1 + 0 * 8 + 8 * 16),
            of(MirrorRight(false, setOf(EAST, WEST)), 1 + 0 * 8 + 3 * 16),
            of(MirrorRight(false, setOf(WEST, NORTH)), 1 + 0 * 8 + 10 * 16),
            of(MirrorRight(false, setOf(EAST, NORTH, SOUTH, WEST)), 1 + 0 * 8 + 15 * 16),
            of(MirrorRight(false, setOf(SOUTH, EAST)), 1 + 0 * 8 + 5 * 16),
            of(MirrorRight(true, emptySet()), 1 + 1 * 8 + 0 * 16),
            of(MirrorRight(true, setOf(EAST)), 1 + 1 * 8 + 1 * 16),
            of(MirrorRight(true, setOf(WEST)), 1 + 1 * 8 + 2 * 16),
            of(MirrorRight(true, setOf(SOUTH)), 1 + 1 * 8 + 4 * 16),
            of(MirrorRight(true, setOf(NORTH)), 1 + 1 * 8 + 8 * 16),
            of(MirrorRight(true, setOf(EAST, WEST)), 1 + 1 * 8 + 3 * 16),
            of(MirrorRight(true, setOf(WEST, NORTH)), 1 + 1 * 8 + 10 * 16),
            of(MirrorRight(true, setOf(EAST, NORTH, SOUTH, WEST)), 1 + 1 * 8 + 15 * 16),
            of(MirrorRight(true, setOf(SOUTH, EAST)), 1 + 1 * 8 + 5 * 16),
            of(MirrorLeft(false, emptySet()), 2 + 0 * 8 + 0 * 16),
            of(MirrorLeft(false, setOf(EAST)), 2 + 0 * 8 + 1 * 16),
            of(MirrorLeft(false, setOf(WEST)), 2 + 0 * 8 + 2 * 16),
            of(MirrorLeft(false, setOf(SOUTH)), 2 + 0 * 8 + 4 * 16),
            of(MirrorLeft(false, setOf(NORTH)), 2 + 0 * 8 + 8 * 16),
            of(MirrorLeft(false, setOf(EAST, WEST)), 2 + 0 * 8 + 3 * 16),
            of(MirrorLeft(false, setOf(WEST, NORTH)), 2 + 0 * 8 + 10 * 16),
            of(MirrorLeft(false, setOf(EAST, NORTH, SOUTH, WEST)), 2 + 0 * 8 + 15 * 16),
            of(MirrorLeft(false, setOf(SOUTH, EAST)), 2 + 0 * 8 + 5 * 16),
            of(MirrorLeft(true, emptySet()), 2 + 1 * 8 + 0 * 16),
            of(MirrorLeft(true, setOf(EAST)), 2 + 1 * 8 + 1 * 16),
            of(MirrorLeft(true, setOf(WEST)), 2 + 1 * 8 + 2 * 16),
            of(MirrorLeft(true, setOf(SOUTH)), 2 + 1 * 8 + 4 * 16),
            of(MirrorLeft(true, setOf(NORTH)), 2 + 1 * 8 + 8 * 16),
            of(MirrorLeft(true, setOf(EAST, WEST)), 2 + 1 * 8 + 3 * 16),
            of(MirrorLeft(true, setOf(WEST, NORTH)), 2 + 1 * 8 + 10 * 16),
            of(MirrorLeft(true, setOf(EAST, NORTH, SOUTH, WEST)), 2 + 1 * 8 + 15 * 16),
            of(MirrorLeft(true, setOf(SOUTH, EAST)), 2 + 1 * 8 + 5 * 16),
            of(SplitHorizontal(false, emptySet()), 3 + 0 * 8 + 0 * 16),
            of(SplitHorizontal(false, setOf(EAST)), 3 + 0 * 8 + 1 * 16),
            of(SplitHorizontal(false, setOf(WEST)), 3 + 0 * 8 + 2 * 16),
            of(SplitHorizontal(false, setOf(SOUTH)), 3 + 0 * 8 + 4 * 16),
            of(SplitHorizontal(false, setOf(NORTH)), 3 + 0 * 8 + 8 * 16),
            of(SplitHorizontal(false, setOf(EAST, WEST)), 3 + 0 * 8 + 3 * 16),
            of(SplitHorizontal(false, setOf(WEST, NORTH)), 3 + 0 * 8 + 10 * 16),
            of(SplitHorizontal(false, setOf(EAST, NORTH, SOUTH, WEST)), 3 + 0 * 8 + 15 * 16),
            of(SplitHorizontal(false, setOf(SOUTH, EAST)), 3 + 0 * 8 + 5 * 16),
            of(SplitHorizontal(true, emptySet()), 3 + 1 * 8 + 0 * 16),
            of(SplitHorizontal(true, setOf(EAST)), 3 + 1 * 8 + 1 * 16),
            of(SplitHorizontal(true, setOf(WEST)), 3 + 1 * 8 + 2 * 16),
            of(SplitHorizontal(true, setOf(SOUTH)), 3 + 1 * 8 + 4 * 16),
            of(SplitHorizontal(true, setOf(NORTH)), 3 + 1 * 8 + 8 * 16),
            of(SplitHorizontal(true, setOf(EAST, WEST)), 3 + 1 * 8 + 3 * 16),
            of(SplitHorizontal(true, setOf(WEST, NORTH)), 3 + 1 * 8 + 10 * 16),
            of(SplitHorizontal(true, setOf(EAST, NORTH, SOUTH, WEST)), 3 + 1 * 8 + 15 * 16),
            of(SplitHorizontal(true, setOf(SOUTH, EAST)), 3 + 1 * 8 + 5 * 16),
            of(SplitVertical(false, emptySet()), 4 + 0 * 8 + 0 * 16),
            of(SplitVertical(false, setOf(EAST)), 4 + 0 * 8 + 1 * 16),
            of(SplitVertical(false, setOf(WEST)), 4 + 0 * 8 + 2 * 16),
            of(SplitVertical(false, setOf(SOUTH)), 4 + 0 * 8 + 4 * 16),
            of(SplitVertical(false, setOf(NORTH)), 4 + 0 * 8 + 8 * 16),
            of(SplitVertical(false, setOf(EAST, WEST)), 4 + 0 * 8 + 3 * 16),
            of(SplitVertical(false, setOf(WEST, NORTH)), 4 + 0 * 8 + 10 * 16),
            of(SplitVertical(false, setOf(EAST, NORTH, SOUTH, WEST)), 4 + 0 * 8 + 15 * 16),
            of(SplitVertical(false, setOf(SOUTH, EAST)), 4 + 0 * 8 + 5 * 16),
            of(SplitVertical(true, emptySet()), 4 + 1 * 8 + 0 * 16),
            of(SplitVertical(true, setOf(EAST)), 4 + 1 * 8 + 1 * 16),
            of(SplitVertical(true, setOf(WEST)), 4 + 1 * 8 + 2 * 16),
            of(SplitVertical(true, setOf(SOUTH)), 4 + 1 * 8 + 4 * 16),
            of(SplitVertical(true, setOf(NORTH)), 4 + 1 * 8 + 8 * 16),
            of(SplitVertical(true, setOf(EAST, WEST)), 4 + 1 * 8 + 3 * 16),
            of(SplitVertical(true, setOf(WEST, NORTH)), 4 + 1 * 8 + 10 * 16),
            of(SplitVertical(true, setOf(EAST, NORTH, SOUTH, WEST)), 4 + 1 * 8 + 15 * 16),
            of(SplitVertical(true, setOf(SOUTH, EAST)), 4 + 1 * 8 + 5 * 16),
            )
    }

}