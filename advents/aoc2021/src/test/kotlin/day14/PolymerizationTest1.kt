package com.gilpereda.adventsofcode.adventsofcode2021.day14

import com.gilpereda.adventsofcode.adventsofcode2021.BaseTest

class PolymerizationTest1 : BaseTest() {
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

    override val result: String = "1588"

    override val input: String = "/day14/input.txt"

    override val run: (Sequence<String>) -> String = ::polymerization1
}