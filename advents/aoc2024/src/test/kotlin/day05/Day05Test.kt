package com.gilpereda.aoc2024.day05

import com.gilpereda.aoc2024.BaseTest
import com.gilpereda.aoc2024.Executable

class Day05Test : BaseTest() {
    override val example: String =
        """
        47|53
        97|13
        97|61
        97|47
        75|29
        61|13
        75|53
        29|13
        97|29
        53|29
        61|53
        97|53
        61|29
        47|13
        75|47
        97|75
        47|61
        75|61
        47|29
        75|13
        53|13

        97,13,75,29,47
        75,97,47,61,53
        75,47,61,53,29
        97,61,53,29,13
        75,29,13
        61,13,29
        """.trimIndent()

    override val resultExample1: String = "143"

    override val resultReal1: String = "3608"

    override val resultExample2: String = "123"

    override val resultReal2: String = "4922"

    override val input: String = "/day05/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask
}
