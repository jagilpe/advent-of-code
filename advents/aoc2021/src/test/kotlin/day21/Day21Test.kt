package com.gilpereda.adventsofcode.adventsofcode2021.day21

import com.gilpereda.adventsofcode.adventsofcode2021.BaseTest
import com.gilpereda.adventsofcode.adventsofcode2021.Executable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day21Test {

    @Test
    fun `should work with the example - part 1`() {
        assertThat(part1(4, 8)).isEqualTo(739785)
    }

    @Test
    fun `should calculate the result - part 1`() {
        val result = part1(4, 2)
        assertThat(result).isNotNull

        println(result)
    }

    @Test
    fun `should work with the example - part 2`() {
        assertThat(part2(4, 8)).isEqualTo(Pair(444356092776315L, 341960390180808L))
    }

    @Test
    fun `should calculate the result - part 2`() {
        val result = part2(4, 2)
        assertThat(result).isNotNull

        println(result)
    }
}