package com.gilpereda.aoc2022.day23

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day23Test : BaseTest() {
    override val example: String = """....#..
..###.#
#...#.#
.#...##
#.###..
##.#.##
.#..#.."""

    override val result1: String = "110"

    override val result2: String = "20"

    override val input: String = "/day23/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask

    @Test
    fun `should parse the input`() {
        val expected = mapOf(
            0 to mapOf(4 to Elf(4, 0)),
            1 to mapOf(2 to Elf(2, 1), 3 to Elf(3, 1), 4 to Elf(4, 1), 6 to Elf(6, 1)),
            2 to mapOf(0 to Elf(0, 2), 4 to Elf(4, 2), 6 to Elf(6, 2)),
            3 to mapOf(1 to Elf(1, 3), 5 to Elf(5, 3), 6 to Elf(6, 3)),
            4 to mapOf(0 to Elf(0, 4), 2 to Elf(2, 4), 3 to Elf(3, 4), 4 to Elf(4, 4)),
            5 to mapOf(0 to Elf(0, 5), 1 to Elf(1, 5), 3 to Elf(3, 5), 5 to Elf(5, 5), 6 to Elf(6, 5)),
            6 to mapOf(1 to Elf(1, 6), 4 to Elf(4, 6)),
        ).let(::Field)
        assertThat(example.splitToSequence("\n").parsed()).isEqualTo(expected)
    }

    @Test
    fun `should work with a small example`() {
        val smallExample = """
            .....
            ..##.
            ..#..
            .....
            ..##.
            .....
        """.trimIndent().splitToSequence("\n").parsed()

        generateSequence(smallExample) { it.next }
            .onEachIndexed { index, field ->
                println(field); println()
            }
            .take(5).toList()
    }

}