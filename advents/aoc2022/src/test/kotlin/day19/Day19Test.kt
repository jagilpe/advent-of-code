package com.gilpereda.aoc2022.day19

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * 958 -> too low
 *
 * 4508 -> too low 2nd part
 * 5152 -> too low 2nd part
 * 5888 -> too low 2nd part
 * 7360 -> not the right answer
 */
class Day19Test : BaseTest() {
    override val example: String = """Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 2 ore. Each obsidian robot costs 3 ore and 14 clay. Each geode robot costs 2 ore and 7 obsidian.
Blueprint 2: Each ore robot costs 2 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 8 clay. Each geode robot costs 3 ore and 12 obsidian."""

    override val result1: String = "33"

    override val result2: String = "3472"

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

    @Test
    fun `should save the best processes`() {
        val threadPool = newFixedThreadPoolContext(4, "blueprints calculation")
        val blueprints = inputSequence.parsed().take(3).toList()
        val count = 50_000
        runBlocking {
            val firstDeferred = launch(threadPool) {
                blueprints.getOrNull(0)?.let {
                    it.saveBestProcesses(22, count)
                }
            }

            val secondDeferred = launch(threadPool) {
                blueprints.getOrNull(1)?.let {
                    it.saveBestProcesses(22, count)
                }
            }

            val thirdDeferred = launch(threadPool) {
                blueprints.getOrNull(2)?.let {
                    it.saveBestProcesses(22, count)
                }
            }

            firstDeferred.join()
            secondDeferred.join()
            thirdDeferred.join()
        }

        blueprints.forEach { it.saveBestProcesses(10, 100_000) }
    }

    @Test
    fun `should load the best processes`() {
        val blueprints = example.splitToSequence("\n").parsed()

        val loadedProcesses = blueprints.associate { it.id to it.loadBestProcesses(10, 20) }

        assertThat(loadedProcesses).isNotEmpty
    }

}