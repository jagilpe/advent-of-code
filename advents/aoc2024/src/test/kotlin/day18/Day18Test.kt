package com.gilpereda.aoc2024.day18

import com.gilpereda.aoc2024.BaseTest
import com.gilpereda.aoc2024.Executable

class Day18Test : BaseTest() {
    override val example: String =
        """
        7,7,12
        
        5,4
        4,2
        4,5
        3,0
        2,1
        6,3
        2,4
        1,5
        0,6
        3,3
        2,6
        5,1
        1,2
        5,5
        2,5
        6,5
        1,4
        0,4
        6,4
        1,1
        6,1
        1,0
        0,5
        1,6
        2,0
        """.trimIndent()

    override val resultExample1: String = "22"

    override val resultReal1: String = "262"

    override val resultExample2: String = "6,1"

    override val resultReal2: String = "22,20"

    override val input: String = "/day18/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask
}
