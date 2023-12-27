package com.gilpereda.aoc2022.day11

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day11Test : BaseTest() {
    override val example: String = """Monkey 0:
  Starting items: 79, 98
  Operation: new = old * 19
  Test: divisible by 23
    If true: throw to monkey 2
    If false: throw to monkey 3

Monkey 1:
  Starting items: 54, 65, 75, 74
  Operation: new = old + 6
  Test: divisible by 19
    If true: throw to monkey 2
    If false: throw to monkey 0

Monkey 2:
  Starting items: 79, 60, 97
  Operation: new = old * old
  Test: divisible by 13
    If true: throw to monkey 1
    If false: throw to monkey 3

Monkey 3:
  Starting items: 74
  Operation: new = old + 3
  Test: divisible by 17
    If true: throw to monkey 0
    If false: throw to monkey 1"""

    override val result1: String = "10605"

    override val result2: String = "2713310158"

    override val input: String = "/day11/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask

    @Test
    fun `should parse a monkey`() {
        val input = """Monkey 0:
  Starting items: 79, 98
  Operation: new = old * 19
  Test: divisible by 23
    If true: throw to monkey 2
    If false: throw to monkey 3""".split("\n")

        val expected = Monkey(
            items = listOf(79, 98),
            operation = Multiply(19),
            divisibleBy = 23,
            ifTrueThrowToMonkey = 2,
            ifFalseThrowToMonkey = 3,
        )

        assertThat(parseMonkey(input)).isEqualTo(expected)
    }

    @Test
    fun `should parse all monkeys`() {
        val expected = listOf(
            Monkey(
                items = listOf(79, 98),
                operation = Multiply(19),
                divisibleBy = 23,
                ifTrueThrowToMonkey = 2,
                ifFalseThrowToMonkey = 3,
            ),
            Monkey(
                items = listOf(54, 65, 75, 74),
                operation = Sum(6),
                divisibleBy = 19,
                ifTrueThrowToMonkey = 2,
                ifFalseThrowToMonkey = 0,
            ),
            Monkey(
                items = listOf(79, 60, 97),
                operation = Square,
                divisibleBy = 13,
                ifTrueThrowToMonkey = 1,
                ifFalseThrowToMonkey = 3,
            ),
            Monkey(
                items = listOf(74),
                operation = Sum(3),
                divisibleBy = 17,
                ifTrueThrowToMonkey = 0,
                ifFalseThrowToMonkey = 1,
            ),
        )

        assertThat(example.parsed()).isEqualTo(expected)
    }

}