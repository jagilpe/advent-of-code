package com.gilpereda.aoc2022.day12

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * 382 -> too high
 * 376 -> too high
 */
class Day12Test : BaseTest() {
    override val example: String = """Sabqponm
abcryxxl
accszExk
acctuvwj
abdefghi"""

    override val result1: String = "31"

    override val result2: String = "29"

    override val input: String = "/day12/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask

    @Test
    fun `should parse the map`() {
        val expectedMap = listOf(
            listOf('a', 'a', 'b', 'q', 'p', 'o', 'n', 'm',),
            listOf('a', 'b', 'c', 'r', 'y', 'x', 'x', 'l',),
            listOf('a', 'c', 'c', 's', 'z', 'z', 'x', 'k',),
            listOf('a', 'c', 'c', 't', 'u', 'v', 'w', 'j',),
            listOf('a', 'b', 'd', 'e', 'f', 'g', 'h', 'i',),
        )
        val expected = HeightsMap(
            heightsMap = expectedMap,
            start = Point(0, 0),
            goal = Point(5, 2),
        )

        assertThat(example.splitToSequence("\n").parsed()).isEqualTo(expected)
    }
}