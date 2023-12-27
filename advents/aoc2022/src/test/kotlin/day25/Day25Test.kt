package com.gilpereda.aoc2022.day25

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.of
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class Day25Test : BaseTest() {
    override val example: String = """1=-0-2
12111
2=0=
21
2=01
111
20012
112
1=-1=
1-12
12
1=
122"""

    override val result1: String = "2=-1=0"

    override val result2: String
        get() = TODO()

    override val input: String = "/day25/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask

//    @ParameterizedTest
//    @MethodSource("snafuConversion")
//    fun `should convert base10 to base5`(base10: Long) {
//        assertThat(base10.base10toBase5.baseSnafuToBase10).isEqualTo(base10)
//    }

    @Test
    fun showBase5() {
        (0L..1000).forEach { println("$it - ${it.base10toBase5}") }
    }

    @ParameterizedTest
    @MethodSource("snafuConversion")
    fun `should convert from decimal to snafu`(decimal: Long, snafu: String) {
        assertThat(decimal.decimalToSnafu).isEqualTo(snafu)
    }

    @ParameterizedTest
    @MethodSource("snafuConversion")
    fun `should convert from snafu to decimal`(decimal: Long, snafu: String) {
        assertThat(snafu.snafuToDecimal).isEqualTo(decimal)
    }

    companion object {
        @JvmStatic
        fun snafuConversion(): Stream<Arguments> = Stream.of(
            of(1, "1"),
            of(2, "2"),
            of(3, "1="),
            of(4, "1-"),
            of(5, "10"),
            of(6, "11"),
            of(7, "12"),
            of(8, "2="),
            of(9, "2-"),
            of(10, "20"),
            of(15, "1=0"),
            of(20, "1-0"),
            of(2022, "1=11-2"),
            of(12345, "1-0---0"),
            of(314159265, "1121-1110-1=0"),
            of(1747, "1=-0-2"),
            of(906, "12111"),
            of(198, "2=0="),
            of(11, "21"),
            of(201, "2=01"),
            of(31, "111"),
            of(1257, "20012"),
            of(32, "112"),
            of(353, "1=-1="),
            of(107, "1-12"),
            of(7, "12"),
            of(3, "1="),
            of(37, "122"),
        )
    }

}