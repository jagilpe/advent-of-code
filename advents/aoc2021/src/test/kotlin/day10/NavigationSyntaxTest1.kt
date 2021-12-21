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

    override val result1: String = "26397"
    override val result2: String = "288957"

    override val input: String = "/day10/input.txt"

    override val run1: (Sequence<String>) -> String = ::checkSyntax
    override val run2: (Sequence<String>) -> String = ::checkIncomplete
}