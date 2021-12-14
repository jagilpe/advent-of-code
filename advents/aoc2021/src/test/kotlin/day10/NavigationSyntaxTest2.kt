package com.gilpereda.adventsofcode.adventsofcode2021.day10

import com.gilpereda.adventsofcode.adventsofcode2021.BaseTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class NavigationSyntaxTest2 : BaseTest() {
    override val example: String = """
        [({(<(())[]>[[{[]{<()<>>
        [(()[<>])]({[<{<<[]>>(
        {([(<{}[<>[]}>{[]{[(<()>
        (((({<>}<{<{<>}{[]{[]{}
        [[<[([]))<([[{}[[()]]]
        [{[{({}]{}}([{[{{{}}([]
        {<[[]]>}<{[{[{[]{()[[[]
        [<(<(<(<{}))><([]([]()
        <{([([[(<>()){}]>(<<{{
        <{([{{}}[<[[[<>{}]]]>[]]
    """.trimIndent()

    override val result: String = "288957"

    override val input: String = "/day10/input.txt"

    override val run: (Sequence<String>) -> String = ::checkIncomplete

    @ParameterizedTest
    @CsvSource(value = ["<{([,294", "[({([[{{,288957", "({[<{(,5566"])
    fun `should get the points of the rest`(chars: String, result: Int) {
        assertThat(chars.toList().points).isEqualTo(result)
    }
}