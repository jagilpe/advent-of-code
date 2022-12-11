package com.gilpereda.aoc2022.day09

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day09Test : BaseTest() {
    override val example: String = """R 4
U 4
L 3
D 1
R 4
D 1
L 5
R 2"""

    override val example2: String = """R 5
U 8
L 8
D 3
R 17
D 10
L 25
U 20"""

    override val result1: String = "13"

    override val result2: String = "36"

    override val input: String = "/day09/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask



    @Test
    fun `should work with antoher example - part 2`() {
        check(example to result2, run2)
    }

    @Test
    fun `should parse the lines`() {
        val parsed = example.splitToSequence("\n").flatMap(::parseLine).toList()
        val expected = listOf(
            Right, Right, Right, Right,
            Up, Up, Up, Up,
            Left, Left, Left,
            Down,
            Right, Right, Right, Right,
            Down,
            Left, Left, Left, Left, Left,
            Right, Right,
        )

        assertThat(parsed).isEqualTo(expected)
    }
}