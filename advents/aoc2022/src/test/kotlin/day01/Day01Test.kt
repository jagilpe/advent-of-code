package com.gilpereda.aoc2022.day01

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day01Test : BaseTest() {
    override val example: String = """
        1000
        2000
        3000

        4000

        5000
        6000

        7000
        8000
        9000

        10000
    """.trimIndent()

    override val result1: String = "24000"

    override val result2: String = "45000"

    override val input: String = "/day01/input"

    override val run1: Executable = ::elvesFood01

    override val run2: Executable = ::elvesFood02

}
