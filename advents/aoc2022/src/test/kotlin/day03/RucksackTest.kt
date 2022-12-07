package com.gilpereda.aoc2022.day03

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable

class RucksackTest : BaseTest() {
    override val example: String = """
        vJrwpWtwJgWrhcsFMMfFFhFp
        jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL
        PmmdzqPrVvPwwTWBwg
        wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn
        ttgJtRGJQctTZtZT
        CrZsJsPPZsGzwwsLwLmpwMDw
    """.trimIndent()

    override val result1: String = "157"

    override val result2: String = "70"

    override val input: String = "/day03/input"

    override val run1: Executable = ::rucksack

    override val run2: Executable = ::badges
}