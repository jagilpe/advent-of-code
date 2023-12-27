package com.gilpereda.aoc2022.day15

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable
import com.gilpereda.aoc2022.day15.Rectangle.Companion.rct
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

/**
 * 1461548630 -> too low
 * 1461548630
 */
class Day15Test : BaseTest() {
    override val example: String = """Sensor at x=2, y=18: closest beacon is at x=-2, y=15
Sensor at x=9, y=16: closest beacon is at x=10, y=16
Sensor at x=13, y=2: closest beacon is at x=15, y=3
Sensor at x=12, y=14: closest beacon is at x=10, y=16
Sensor at x=10, y=20: closest beacon is at x=10, y=16
Sensor at x=14, y=17: closest beacon is at x=10, y=16
Sensor at x=8, y=7: closest beacon is at x=2, y=10
Sensor at x=2, y=0: closest beacon is at x=2, y=10
Sensor at x=0, y=11: closest beacon is at x=2, y=10
Sensor at x=20, y=14: closest beacon is at x=25, y=17
Sensor at x=17, y=20: closest beacon is at x=21, y=22
Sensor at x=16, y=7: closest beacon is at x=15, y=3
Sensor at x=14, y=3: closest beacon is at x=15, y=3
Sensor at x=20, y=1: closest beacon is at x=15, y=3"""

    override val result1: String = "26"

    override val result2: String = "56000011"

    override val input: String = "/day15/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask

    @Test
    fun `should parse the input`() {
        val expected = listOf(
            Sensor(x=2, y=18, beacon = Point(x=-2, y=15)),
            Sensor(x=9, y=16, beacon = Point(x=10, y=16)),
            Sensor(x=13, y=2, beacon = Point(x=15, y=3)),
            Sensor(x=12, y=14, beacon = Point(x=10, y=16)),
            Sensor(x=10, y=20, beacon = Point(x=10, y=16)),
            Sensor(x=14, y=17, beacon = Point(x=10, y=16)),
            Sensor(x=8, y=7, beacon = Point(x=2, y=10)),
            Sensor(x=2, y=0, beacon = Point(x=2, y=10)),
            Sensor(x=0, y=11, beacon = Point(x=2, y=10)),
            Sensor(x=20, y=14, beacon = Point(x=25, y=17)),
            Sensor(x=17, y=20, beacon = Point(x=21, y=22)),
            Sensor(x=16, y=7, beacon = Point(x=15, y=3)),
            Sensor(x=14, y=3, beacon = Point(x=15, y=3)),
            Sensor(x=20, y=1, beacon = Point(x=15, y=3)),
        )

        val parsed = example.splitToSequence("\n").parsed()
        assertThat(parsed).isEqualTo(expected)
    }

    @Test
    fun testSomething() {
        val rectangles = Sensor(0, 0, Point(200, 200)).rectangles(100)

        assertThat(rectangles).isNotNull

    }

    @ParameterizedTest
    @MethodSource("rectangleSubtraction")
    fun `should subtract rectangles`(case: String, other: Rectangle, expected: List<Rectangle>) {
        val rectangle = Rectangle(0, 5, 20, 25)

        assertThat(rectangle - other).isEqualTo(expected)
    }

    companion object {
        @JvmStatic
        fun rectangleSubtraction(): Stream<Arguments> = Stream.of(
            // 1
            Arguments.of(
                "1",
                rct(0, 5, 20, 25),
                emptyList<Rectangle>(),
            ),
            // 2
            Arguments.of(
                "2",
                rct(-1, 4, 15, 26),
                listOf(rct(16, 5, 20, 25)),
            ),
            // 3
            Arguments.of(
                "3",
                rct(5, 4, 21, 26),
                listOf(rct(0, 5, 4, 25)),
            ),
            // 4
            Arguments.of(
                "4",
                rct(-1, 4, 21, 20),
                listOf(rct(0, 21, 20, 25)),
            ),
            // 5
            Arguments.of(
                "5",
                rct(-1, 10, 21, 26),
                listOf(rct(0, 5, 20, 9)),
            ),
            // 6
            Arguments.of(
                "6",
                rct(5, 10, 15, 20),
                listOf(
                    rct(0, 5, 20, 9),
                    rct(0, 10, 4, 20),
                    rct(16, 10, 20, 20),
                    rct(0, 21, 20, 25),
                )
            ),
            // 7
            Arguments.of(
                "7",
                rct(-1, 4, 15, 20),
                listOf(
                    rct(16, 5, 20, 20),
                    rct(0, 21, 20, 25),
                )
            ),
            // 8
            Arguments.of(
                "8",
                rct(5, 4, 21, 20),
                listOf(
                    rct(0, 5, 4, 25),
                    rct(5, 21, 20, 25),
                )
            ),
            // 9
            Arguments.of(
                "9",
                rct(5, 10, 21, 26),
                listOf(
                    rct(0, 5, 20, 9),
                    rct(0, 10, 4, 25),
                )
            ),
            // 10
            Arguments.of(
                "10",
                rct(-1, 10, 15, 26),
                listOf(
                    rct(0, 5, 20, 9),
                    rct(16, 10, 20, 25),
                )
            ),
            // 11
            Arguments.of(
                "11",
                rct(-1, 10, 15, 20),
                listOf(
                    rct(0, 5, 20, 9),
                    rct(0, 21, 20, 25),
                    rct(16, 10, 20, 20),
                )
            ),
            // 12
            Arguments.of(
                "12",
                rct(5, 10, 21, 20),
                listOf(
                    rct(0, 5, 20, 9),
                    rct(0, 21, 20, 25),
                    rct(0, 10, 4, 20),
                )
            ),
            // 13
            Arguments.of(
                "13",
                rct(5, 4, 15, 20),
                listOf(
                    rct(0, 5, 4, 20),
                    rct(16, 5, 20, 20),
                    rct(0, 21, 20, 25),
                )
            ),
            // 14
            Arguments.of(
                "14",
                rct(5, 10, 15, 26),
                listOf(
                    rct(0, 5, 20, 9),
                    rct(0, 10, 4, 25),
                    rct(16, 10, 20, 25),
                )
            ),

            // 15
            Arguments.of(
                "15",
                rct(-1, 10, 21, 20),
                listOf(
                    rct(0, 5, 20, 9),
                    rct(0, 21, 20, 25),
                )
            ),
            // 16
            Arguments.of(
                "16",
                rct(5, 4, 15, 26),
                listOf(
                    rct(0, 5, 4, 25),
                    rct(16, 5, 20, 25),
                )
            ),
            // 17
            Arguments.of(
                "day17",
                rct(-15, 5, -5, 15),
                listOf(Rectangle(0, 5, 20, 25))
            ),
            // 18
            Arguments.of(
                "18",
                rct(30, -5, 40, 15),
                listOf(Rectangle(0, 5, 20, 25))
            ),
            // 19
            Arguments.of(
                "19",
                rct(5, -15, 15, -5),
                listOf(Rectangle(0, 5, 20, 25))
            ),
            // 20
            Arguments.of(
                "day17",
                rct(5, 35, 15, 45),
                listOf(Rectangle(0, 5, 20, 25))
            ),
        )
    }

}