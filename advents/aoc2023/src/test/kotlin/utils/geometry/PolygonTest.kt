package com.gilpereda.aoc2022.utils.geometry

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class PolygonTest {

    @ParameterizedTest
    @MethodSource("area")
    fun `should calculate the area of a polygon`(testCase: String, polygon: Polygon, expected: Double) {
        assertThat(polygon.area).isEqualTo(expected)
    }

    companion object {
        @JvmStatic
        fun area(): Stream<Arguments> = Stream.of(
            regularPolygon(),
            correctedPolygonClockwise(),
            correctedPolygonCounterclockwise(),
        )

        private fun regularPolygon() = Arguments.of(
            "regular polygon",
            Polygon(
                listOf(
                    Point.from(2, 2),
                    Point.from(4, 10),
                    Point.from(9, 7),
                    Point.from(11, 2)
                )
            ),
            45.5
        )

        private fun correctedPolygonClockwise() = Arguments.of(
            "corrected clockwise polygon",
            Polygon(
                listOf(
                    Point.from(0, 0),
                    Point.from(6, 0),
                    Point.from(6, 5),
                    Point.from(4, 5),
                    Point.from(4, 7),
                    Point.from(6, 7),
                    Point.from(6, 9),
                    Point.from(1, 9),
                    Point.from(1, 7),
                    Point.from(0, 7),
                    Point.from(0, 5),
                    Point.from(2, 5),
                    Point.from(2, 2),
                    Point.from(0, 2),
                    Point.from(0, 0),
                ),
            ).external(),
            62.0
        )

        private fun correctedPolygonCounterclockwise() = Arguments.of(
            "corrected counterclockwise polygon",
            Polygon(
                listOf(
                    Point.from(0, 0),
                    Point.from(6, 0),
                    Point.from(6, 5),
                    Point.from(4, 5),
                    Point.from(4, 7),
                    Point.from(6, 7),
                    Point.from(6, 9),
                    Point.from(1, 9),
                    Point.from(1, 7),
                    Point.from(0, 7),
                    Point.from(0, 5),
                    Point.from(2, 5),
                    Point.from(2, 2),
                    Point.from(0, 2),
                    Point.from(0, 0),
                ).reversed(),
            ).external(),
            62.0
        )
    }
}