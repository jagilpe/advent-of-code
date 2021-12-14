package com.gilpereda.adventsofcode.adventsofcode2021.day10

import com.gilpereda.adventsofcode.adventsofcode2021.BaseTest

class NavigationSyntaxTest1 : BaseTest() {
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

    override val result: String = "26397"

    override val input: String = "/day10/input.txt"

    override val run: (Sequence<String>) -> String = ::checkSyntax
}