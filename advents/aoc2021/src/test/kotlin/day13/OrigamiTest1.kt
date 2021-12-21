package com.gilpereda.adventsofcode.adventsofcode2021.day13

import com.gilpereda.adventsofcode.adventsofcode2021.BaseTest
import com.gilpereda.adventsofcode.adventsofcode2021.Executable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class OrigamiTest1 : BaseTest() {
    override val example: String = """
        6,10
        0,14
        9,10
        0,3
        10,4
        4,11
        6,0
        6,12
        4,1
        0,13
        10,12
        3,4
        3,0
        8,4
        1,10
        2,14
        8,10
        9,0

        fold along y=7
        fold along x=5
    """.trimIndent()

    override val result1: String = "17"
    override val result2: String
        get() = TODO("Not yet implemented")

    override val input: String = "/day13/input.txt"

    override val run1: (Sequence<String>) -> String = ::origamiOneFold
    override val run2: Executable
        get() = TODO("Not yet implemented")

    private val initialPoints = setOf(
        Point(6, 10),
        Point(0, 14),
        Point(9, 10),
        Point(0, 3),
        Point(10 ,4),
        Point(4, 11),
        Point(6, 0),
        Point(6, 12),
        Point(4, 1),
        Point(0, 13),
        Point(10 ,12),
        Point(3, 4),
        Point(3, 0),
        Point(8, 4),
        Point(1, 10),
        Point(2, 14),
        Point(8, 10),
        Point(9, 0),
    )

    private val pointAfterFirstFold = setOf(
        Point(0, 0),
        Point(2, 0),
        Point(3, 0),
        Point(6, 0),
        Point(9, 0),
        Point(0, 1),
        Point(4, 1),
        Point(6, 2),
        Point(10, 2),
        Point(0, 3),
        Point(4, 3),
        Point(1, 4),
        Point(3, 4),
        Point(6, 4),
        Point(8, 4),
        Point(9, 4),
        Point(10, 4),
    )

    private val pointAfterSecondFold = setOf(
        Point(0, 0),
        Point(1, 0),
        Point(2, 0),
        Point(3, 0),
        Point(4, 0),
        Point(0, 1),
        Point(4, 1),
        Point(0, 2),
        Point(4, 2),
        Point(0, 3),
        Point(4, 3),
        Point(0, 4),
        Point(1, 4),
        Point(2, 4),
        Point(3, 4),
        Point(4, 4),
    )

    @Test
    fun `should parse the points of the input`() {
        assertThat(parseInput(example.splitToSequence("\n")).first)
            .isEqualTo(initialPoints)
    }

    @Test
    fun `should parse the foldings of the input`() {
        assertThat(parseInput(example.splitToSequence("\n")).second)
            .isEqualTo(listOf(
                VerticalFold(7),
                HorizontalFold(5),
            ))
    }

    @Test
    fun `should fold the points vertically`() {
        assertThat(VerticalFold(7).foldAlong(initialPoints)).isEqualTo(pointAfterFirstFold)
    }

    @Test
    fun `should fold the points horizontally`() {
        assertThat(HorizontalFold(5).foldAlong(pointAfterFirstFold)).isEqualTo(pointAfterSecondFold)
    }

    @Test
    fun `should get the resulting code for the example`() {
        val code = getCode(example.splitToSequence("\n"))
        assertThat(code).isNotNull

        println(code)
    }

    @Test
    fun `should get the resulting code for the input`() {
        val code = getCode(inputSequence)
        assertThat(code).isNotNull

        println(code)
    }
}