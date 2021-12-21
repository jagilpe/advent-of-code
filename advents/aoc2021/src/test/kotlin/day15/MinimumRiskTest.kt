package com.gilpereda.adventsofcode.adventsofcode2021.day15

import com.gilpereda.adventsofcode.adventsofcode2021.BaseTest
import com.gilpereda.adventsofcode.adventsofcode2021.Executable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MinimumRiskTest : BaseTest() {
    override val example: String = """
        1163751742
        1381373672
        2136511328
        3694931569
        7463417111
        1319128137
        1359912421
        3125421639
        1293138521
        2311944581
    """.trimIndent()

    override val result1: String = "40"
    override val result2: String = "315"

    override val input: String = "/day15/input.txt"

    override val run1: Executable = ::minimumRisk
    override val run2: Executable = ::minimumRisk2

    @Test
    fun `should parse the input`() {
        val input = """
            1342
            3321
            4432
        """.trimIndent()
        val expected = RiskMap(
            map = listOf(
                listOf(1, 3, 4, 2),
                listOf(3, 3, 2, 1),
                listOf(4, 4, 3, 2),
            ),
            width = 4,
            height = 3,
        )

        assertThat(parseInput(input.splitToSequence("\n"))).isEqualTo(expected)
    }

    @Test
    fun `should calculate the whole map 1`() {
        val input = """
            8
        """.trimIndent()
        val expected = """
            89123
            91234
            12345
            23456
            34567
        """.trimIndent()
        val initial = parseInput(input.splitToSequence("\n"))

        assertThat(initial.wholeMap.asString).isEqualTo(expected)
    }

    @Test
    fun `should calculate the whole map 2`() {
        val input = """
            89
            34
        """.trimIndent()
        val expected = """
            8991122334
            3445566778
            9112233445
            4556677889
            1223344556
            5667788991
            2334455667
            6778899112
            3445566778
            7889911223
        """.trimIndent()
        val initial = parseInput(input.splitToSequence("\n"))

        assertThat(initial.wholeMap.asString).isEqualTo(expected)
    }

    private val RiskMap.asString: String
        get() = this.map.joinToString("\n") { it.joinToString("") }
}