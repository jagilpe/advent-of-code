package com.gilpereda.aoc2022.day20

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day20Test : BaseTest() {
    override val example: String = """1
2
-3
3
-2
0
4"""

    override val result1: String = "3"

    override val result2: String
        get() = TODO()

    override val input: String = "/day20/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask

}