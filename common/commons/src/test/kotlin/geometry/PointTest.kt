package geometry

import com.gilpereda.adventofcode.commons.geometry.Point
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class PointTest {
    @ParameterizedTest
    @MethodSource
    fun `should find the direct paths between two points`(
        one: Point,
        other: Point,
        expected: List<List<Point>>,
    ) {
        assertThat(one.pathsTo(other)).containsExactlyInAnyOrderElementsOf(expected)
    }

    fun `should find the direct paths between two points`(): Stream<Arguments> =
        Stream.of(
//            directPathsTestCases(
//                Point.from(0, 0),
//                Point.from(0, 1),
//                emptyList(),
//            ),
//            directPathsTestCases(
//                Point.from(0, 0),
//                Point.from(1, 0),
//                emptyList(),
//            ),
            directPathsTestCases(
                Point.from(0, 0),
                Point.from(1, 1),
                listOf(listOf(Point.from(0, 1)), listOf(Point.from(1, 0))),
            ),
            directPathsTestCases(
                Point.from(0, 0),
                Point.from(0, 2),
                listOf(listOf(Point.from(0, 1))),
            ),
            directPathsTestCases(
                Point.from(0, 0),
                Point.from(2, 0),
                listOf(listOf(Point.from(1, 0))),
            ),
            directPathsTestCases(
                Point.from(0, 0),
                Point.from(0, 3),
                listOf(listOf(Point.from(0, 1), Point.from(0, 2))),
            ),
            directPathsTestCases(
                Point.from(0, 0),
                Point.from(3, 0),
                listOf(listOf(Point.from(1, 0), Point.from(2, 0))),
            ),
            directPathsTestCases(
                Point.from(0, 0),
                Point.from(2, 1),
                listOf(
                    listOf(Point.from(1, 0), Point.from(2, 0)),
                    listOf(Point.from(1, 0), Point.from(1, 1)),
                    listOf(Point.from(0, 1), Point.from(1, 1)),
                ),
            ),
            directPathsTestCases(
                Point.from(0, 0),
                Point.from(1, 2),
                listOf(
                    listOf(Point.from(0, 1), Point.from(0, 2)),
                    listOf(Point.from(0, 1), Point.from(1, 1)),
                    listOf(Point.from(1, 0), Point.from(1, 1)),
                ),
            ),
            directPathsTestCases(
                Point.from(2, 0),
                Point.from(0, 1),
                listOf(
                    listOf(Point.from(1, 0), Point.from(0, 0)),
                    listOf(Point.from(1, 0), Point.from(1, 1)),
                    listOf(Point.from(2, 1), Point.from(1, 1)),
                ),
            ),
            directPathsTestCases(
                Point.from(1, 0),
                Point.from(0, 2),
                listOf(
                    listOf(Point.from(0, 0), Point.from(0, 1)),
                    listOf(Point.from(1, 1), Point.from(0, 1)),
                    listOf(Point.from(1, 1), Point.from(1, 2)),
                ),
            ),
        )

    private fun directPathsTestCases(
        one: Point,
        other: Point,
        expected: List<List<Point>>,
    ): Arguments = Arguments.of(one, other, expected)
}
