package com.gilpereda.adventsofcode.adventsofcode2021.day17

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class Day17Test {

    @Test
    fun `should work with the example`() {
        assertThat(part1(TargetZone(20..30, -10..-5))).isEqualTo(45)
    }


    @Test
    fun `should work with the example 2`() {
        assertThat(part2(TargetZone(20..30, -10..-5))).isEqualTo(112)
    }

    @Test
    fun `should return the result`() {
        val result = part1(TargetZone(34..67, -215..-186))
        assertThat(result).isNotNull
        println(result)
    }

    @Test
    fun `should return the result 2`() {
        val result = part2(TargetZone(34..67, -215..-186))
        assertThat(result).isNotNull
        println(result)
    }

    @ParameterizedTest
    @CsvSource(value = ["7,2", "6,3", "9,0"])
    fun `should detect if a path reaches the target`(xVel: Int, yVel: Int) {
        assertThat(Path(xVel, yVel).reachesTarget(TargetZone(20..30, -10..-5))).isTrue
    }

    @ParameterizedTest
    @CsvSource(value = ["7,2,3", "6,3,6", "9,0,0"])
    fun `should calculate the max y`(xVel: Int, yVel: Int, maxY: Int) {
        assertThat(Path(xVel, yVel).maxY).isEqualTo(maxY)
    }
}