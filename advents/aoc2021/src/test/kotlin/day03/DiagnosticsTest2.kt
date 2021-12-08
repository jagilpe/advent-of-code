package com.gilpereda.adventsofcode.adventsofcode2021.day03

import com.gilpereda.adventsofcode.adventsofcode2021.BaseTest

class DiagnosticsTest2 : BaseTest() {
    override val example: String = """
        00100
        11110
        10110
        10111
        10101
        01111
        00111
        11100
        10000
        11001
        00010
        01010
    """.trimIndent()

    override val result: String = "230"

    override val input: String = "/day03/input.txt"

    override val run: (Sequence<String>) -> String = ::runOxygenDiagnostics
}