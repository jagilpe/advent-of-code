package com.gilpereda.aoc2022.day18

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
 * 3335 -> too high
 * 3329 -> too high
 * 2040 -> too low
 * 2565 -> not correct
 */
class Day18Test : BaseTest() {
    override val example: String = """2,2,2
1,2,2
3,2,2
2,1,2
2,3,2
2,2,1
2,2,3
2,2,4
2,2,6
1,2,5
3,2,5
2,1,5
2,3,5
"""

    override val result1: String = "64"

    override val result2: String = "58"

    override val input: String = "/day18/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask

    @Test
    fun `should parse the input`() {
        val parsed = example.splitToSequence("\n").parsed()
        val expected = listOf(
            Cube(x = 2, y = 2, z = 2),
            Cube(x = 1, y = 2, z = 2),
            Cube(x = 3, y = 2, z = 2),
            Cube(x = 2, y = 1, z = 2),
            Cube(x = 2, y = 3, z = 2),
            Cube(x = 2, y = 2, z = 1),
            Cube(x = 2, y = 2, z = 3),
            Cube(x = 2, y = 2, z = 4),
            Cube(x = 2, y = 2, z = 6),
            Cube(x = 1, y = 2, z = 5),
            Cube(x = 3, y = 2, z = 5),
            Cube(x = 2, y = 1, z = 5),
            Cube(x = 2, y = 3, z = 5),
        )

        assertThat(parsed.toList()).isEqualTo(expected)
    }

    @ParameterizedTest
    @MethodSource("secondTask")
    fun `should work with a small example`(input: String, expectedFirst: String, expectedSecond: String) {
        assertThat(firstTask(input.splitToSequence("\n"))).isEqualTo(expectedFirst)
    }

    @ParameterizedTest
    @MethodSource("secondTask")
    fun `second task should work with a smaller example`(input: String, expectedFirst: String, expectedSecond: String) {
        assertThat(secondTask(input.splitToSequence("\n"))).isEqualTo(expectedSecond)
    }

    @ParameterizedTest
    @MethodSource("connections")
    fun `should detect connections`(one: Face, other: Face, expected: Boolean) {
        assertThat(one isConnectedWith other).isEqualTo(expected)
    }

    companion object {
        @JvmStatic
        fun connections(): Stream<Arguments> = Stream.of(
            of(XFace(0, 0, 0), XFace(1, 0,0), false),
            of(XFace(0, 0, 0), XFace(0, -1, 0), true),
            of(XFace(0, 0, 0), XFace(0, 1,0), true),
            of(XFace(0, 0, 0), XFace(0, 1,1),false),
            of(XFace(0, 0, 0), XFace(0, 0,1),true),
            of(XFace(0, 0, 0), XFace(0, 0,-1),true),

            of(XFace(0, 0, 0), YFace(0, 0,0),true),
            of(XFace(0, 0, 0), YFace(-1, 0,0),true),
            of(XFace(0, 0, 0), YFace(-1, 1,0),true),
            of(XFace(0, 0, 0), YFace(0, 1,0),true),
            of(XFace(0, 0, 0), YFace(-1, 1,1),false),
            of(XFace(0, 0, 0), YFace(0, -1,0),false),
            of(XFace(0, 0, 0), YFace(1, 1,0),false),

            of(XFace(0, 0, 0), ZFace(0, 0,0),true),
            of(XFace(0, 0, 0), ZFace(-1, 0,0),true),
            of(XFace(0, 0, 0), ZFace(-1, 0,1),true),
            of(XFace(0, 0, 0), ZFace(0, 0,1),true),
            of(XFace(0, 0, 0), ZFace(-1, 1,1),false),
            of(XFace(0, 0, 0), ZFace(0, 0,-1),false),
            of(XFace(0, 0, 0), ZFace(1, 0,1),false),

            of(YFace(0, 0, 0), YFace(0,1,0), false),
            of(YFace(0, 0, 0), YFace(-1,0, 0), true),
            of(YFace(0, 0, 0), YFace(1,0,0), true),
            of(YFace(0, 0, 0), YFace(1,0,1),false),
            of(YFace(0, 0, 0), YFace(0,0,1),true),
            of(YFace(0, 0, 0), YFace(0,0,-1),true),

            of(YFace(0, 0, 0), ZFace(0, 0,0),true),
            of(YFace(0, 0, 0), ZFace(0, -1,0),true),
            of(YFace(0, 0, 0), ZFace(0, -1,1),true),
            of(YFace(0, 0, 0), ZFace(0, 0,1),true),
            of(YFace(0, 0, 0), ZFace(1, -1,1),false),
            of(YFace(0, 0, 0), ZFace(0, 0,-1),false),
            of(YFace(0, 0, 0), ZFace(0, 1,1),false),

            of(ZFace(0, 0, 0), ZFace( 0,0, 1), false),
            of(ZFace(0, 0, 0), ZFace( -1, 0, 0), true),
            of(ZFace(0, 0, 0), ZFace( 1,0, 0), true),
            of(ZFace(0, 0, 0), ZFace( 1,1, 0),false),
            of(ZFace(0, 0, 0), ZFace( 0,1, 0),true),
            of(ZFace(0, 0, 0), ZFace( 0,-1, 0),true),
        )


        @JvmStatic
        fun secondTask(): Stream<Arguments> = Stream.of(
//            of("1,1,1\n2,1,1", "10", "10"),
            of("""
                0,0,-1
                -1,-1,0
                -1,0,0
                -1,1,0
                0,-1,0
                0,1,0
                1,-1,0
                1,0,0
                1,1,0
                0,0,1
            """.trimIndent(), "44", "38"),
            of("""
                -1,-1,-1
                -1,0,-1
                -1,1,-1
                0,-1,-1
                0,0,-1
                0,1,-1
                1,-1,-1
                1,0,-1
                1,1,-1
                -1,-1,0
                -1,0,0
                -1,1,0
                0,-1,0
                0,1,0
                1,-1,0
                1,0,0
                1,1,0
                -1,-1,1
                -1,0,1
                -1,1,1
                0,-1,1
                0,0,1
                0,1,1
                1,-1,1
                1,0,1
                1,1,1
            """.trimIndent(), "60", "54"),
            of("""
                -1,-1,-1
                -1,0,-1
                -1,1,-1
                0,-1,-1
                0,0,-1
                0,1,-1
                1,-1,-1
                1,0,-1
                1,1,-1
                -1,-1,0
                -1,0,0
                -1,1,0
                0,-1,0
                0,1,0
                1,-1,0
                1,1,0
                -1,-1,1
                -1,0,1
                -1,1,1
                0,-1,1
                0,0,1
                0,1,1
                1,-1,1
                1,0,1
                1,1,1
            """.trimIndent(), "62", "62"),
        )
    }
}