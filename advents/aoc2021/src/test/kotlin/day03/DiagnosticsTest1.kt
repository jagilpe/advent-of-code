package com.gilpereda.adventsofcode.adventsofcode2021.day03

import com.gilpereda.adventsofcode.adventsofcode2021.BaseTest

class DiagnosticsTest1 : BaseTest() {
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

    override val result1: String = "198"
    override val result2: String = "230"

    override val input: String = "/day03/input.txt"

    override val run1: (Sequence<String>) -> String = ::runDiagnostics
    override val run2: (Sequence<String>) -> String = ::runOxygenDiagnostics

}