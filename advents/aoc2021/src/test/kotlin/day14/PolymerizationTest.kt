package com.gilpereda.adventsofcode.adventsofcode2021.day14

import com.gilpereda.adventsofcode.adventsofcode2021.BaseTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PolymerizationTest : BaseTest() {
    override val example: String = """
        NNCB

        CH -> B
        HH -> N
        CB -> H
        NH -> C
        HB -> C
        HC -> B
        HN -> C
        NN -> C
        BH -> H
        NC -> B
        NB -> B
        BN -> B
        BB -> N
        BC -> B
        CC -> N
        CN -> C
    """.trimIndent()

    private val initial = "NNCB"

    private val dict = mapOf(
        "CH" to 'B',
        "HH" to 'N',
        "CB" to 'H',
        "NH" to 'C',
        "HB" to 'C',
        "HC" to 'B',
        "HN" to 'C',
        "NN" to 'C',
        "BH" to 'H',
        "NC" to 'B',
        "NB" to 'B',
        "BN" to 'B',
        "BB" to 'N',
        "BC" to 'B',
        "CC" to 'N',
        "CN" to 'C',
    )

    override val result1: String = "1588"
    override val result2: String = "2188189693529"

    override val input: String = "/day14/input.txt"

    override val run1: (Sequence<String>) -> String = ::polymerization1
    override val run2: (Sequence<String>) -> String = ::polymerization2

    @Test
    fun `should parse the input`() {
        assertThat(parseInput(example.splitToSequence("\n"))).isEqualTo(Pair("NNCB", dict))
    }

    @Test
    fun `should find the next generation`() {
        assertThat(nextGen(initial.toList(), dict)).isEqualTo("NCNBCHB")
    }

}