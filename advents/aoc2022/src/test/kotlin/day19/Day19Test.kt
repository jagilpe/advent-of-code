package com.gilpereda.aoc2022.day19

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * 958 -> too low
 *
 * 4508 -> too low 2nd part
 * 5152 -> too low 2nd part
 * 5888 -> too low 2nd part
 */
class Day19Test : BaseTest() {
    override val example: String = """Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 2 ore. Each obsidian robot costs 3 ore and 14 clay. Each geode robot costs 2 ore and 7 obsidian.
Blueprint 2: Each ore robot costs 2 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 8 clay. Each geode robot costs 3 ore and 12 obsidian."""

    override val result1: String = "33"

    override val result2: String = "62"

    override val input: String = "/day19/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask

    @Test
    fun `should parse the input`() {
        val parsed = example.splitToSequence("\n").parsed()
        val expected = listOf(
            BluePrint(
                id = 1,
                oreRobot = Resources(ore = 4),
                clayRobot = Resources(ore = 2),
                obsidianRobot = Resources(ore = 3, clay = 14),
                geodeRobot = Resources(ore = 2, obsidian = 7),
            ),
            BluePrint(
                id = 2,
                oreRobot = Resources(ore = 2),
                clayRobot = Resources(ore = 3),
                obsidianRobot = Resources(ore = 3, clay = 8),
                geodeRobot = Resources(ore = 3, obsidian = 12),
            ),
        )

        assertThat(parsed.toList()).isEqualTo(expected)
    }

}