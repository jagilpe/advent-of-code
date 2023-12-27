package com.gilpereda.aoc2022.day20

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable

class Day20Test : BaseTest() {
    override val example: String = """
        broadcaster -> a
        %a -> inv, con
        &inv -> b
        %b -> con
        &con -> output
    """.trimIndent()

    override val resultExample1: String ="11687500"

    override val resultExample2: String = ""

    override val resultReal1: String = "898731036"

    override val resultReal2: String = "229414480926893"

    override val input: String = "/day20/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask
}