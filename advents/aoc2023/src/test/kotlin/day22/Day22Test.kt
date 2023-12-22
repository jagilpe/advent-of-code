package com.gilpereda.aoc2022.day22

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable

class Day22Test : BaseTest() {
    override val example: String = """
        1,0,1~1,2,1
        0,0,2~2,0,2
        0,2,3~2,2,3
        0,0,4~0,2,4
        2,0,5~2,2,5
        0,1,6~2,1,6
        1,1,8~1,1,9
    """.trimIndent()

    override val resultExample1: String = "5"

    override val resultExample2: String = "7"

    override val resultReal1: String = "401"

    override val resultReal2: String = "63491"

    override val input: String = "/day22/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask
}