package com.gilpereda.aoc2024.day14

import com.gilpereda.adventofcode.commons.geometry.Point
import com.gilpereda.aoc2024.BaseTest
import com.gilpereda.aoc2024.Executable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day14Test : BaseTest() {
    override val example: String =
        """
        w=11 h=7
        p=0,4 v=3,-3
        p=6,3 v=-1,-3
        p=10,3 v=-1,2
        p=2,0 v=2,-1
        p=0,0 v=1,3
        p=3,0 v=-2,-2
        p=7,6 v=-1,-3
        p=3,0 v=-1,-2
        p=9,3 v=2,3
        p=7,3 v=-1,2
        p=2,4 v=2,-3
        p=9,5 v=-3,-3
        """.trimIndent()

    override val resultExample1: String = "12"

    override val resultReal1: String = ""

    override val resultExample2: String = ""

    override val resultReal2: String = ""

    override val input: String = "/day14/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask

    @Test
    fun `should calculate the robot movement`() {
        val robot = Robot(Point.from(2, 4), Point.from(2, -3))
        val width = 11
        val height = 7

        val robots = generateSequence(robot) { it.next(width, height) }.take(6).toList()

        assertThat(robots).hasSize(6)
    }
}
