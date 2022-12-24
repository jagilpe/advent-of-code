package com.gilpereda.aoc2022.day21

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

/**
 * 8506900824105 -> too high
 * 8506900824111264E12
 * 8506900824110
 * 8506900824117
 * 8506900824097
 */
class Day21Test : BaseTest() {
    override val example: String = """root: pppw + sjmn
dbpl: 5
cczh: sllz + lgvd
zczc: 2
ptdq: humn - dvpt
dvpt: 3
lfqf: 4
humn: 5
ljgn: 2
sjmn: drzm * dbpl
sllz: 4
pppw: cczh / lfqf
lgvd: ljgn * ptdq
drzm: hmdt - zczc
hmdt: 32"""

    override val result1: String = "152"

    override val result2: String = "301"

    override val input: String = "/day21/input"

    override val run1: Executable = ::firstTask2

    override val run2: Executable = ::secondTask


//    @Test
//    fun `should parse the input`() {
//        val actual = example.splitToSequence("\n").parsed()
//        val expected = listOf(
//            CalculatingMonkey("root", Sum, "pppw", "sjmn"),
//            YellingMonkey("dbpl", 5),
//            CalculatingMonkey("cczh", Sum, "sllz", "lgvd"),
//            YellingMonkey("zczc", 2),
//            CalculatingMonkey("ptdq", Subtract, "humn", "dvpt"),
//            YellingMonkey("dvpt",  3),
//            YellingMonkey("lfqf",  4),
//            YellingMonkey("humn",  5),
//            YellingMonkey("ljgn",  2),
//            CalculatingMonkey("sjmn", Multiply, "drzm", "dbpl"),
//            YellingMonkey("sllz",  4),
//            CalculatingMonkey("pppw", Divide, "cczh", "lfqf"),
//            CalculatingMonkey("lgvd", Multiply, "ljgn", "ptdq"),
//            CalculatingMonkey("drzm", Subtract, "hmdt", "zczc"),
//            YellingMonkey("hmdt",  32),
//        )
//        assertThat(actual).isEqualTo(expected)
//    }

    @Test
    fun `the results should match`() {
        val monkeyTree = inputSequence.parseTree()
        val (firstResults, monkeyFinder) = calculate(monkeyTree.map(Tree::toMonkey))

        val expectedRootValue = firstResults[ROOT]!!

        val (secondResults) = calculate(monkeyTree.traverse(HUMN, expectedRootValue).map(Tree::toMonkey))

        val results = followResults(monkeyFinder, firstResults, secondResults)

        assertThat(results.filter { (_, value) -> value.first != value.second }).isEmpty()
    }

    private fun followResults(
        monkeyFinder: MonkeyFinder,
        firstResults: Map<String, BigDecimal>,
        secondResults: Map<String, BigDecimal>
    ): Map<Monkey, Pair<BigDecimal, BigDecimal>> {
        tailrec fun go(
            monkey: Monkey,
            acc: Map<Monkey, Pair<BigDecimal, BigDecimal>>
        ): Map<Monkey, Pair<BigDecimal, BigDecimal>> =
            when (monkey) {
                is YellingMonkey -> acc + mapOf(monkey to Pair(firstResults[monkey.name]!!, secondResults[monkey.name]!!))
                is CustomYellingMonkey -> throw Exception("illegal type CustomYellingMonkey")
                is CalculatingMonkey -> {
                    val next = monkey.dependentWith(HUMN)
                    go(next, acc + mapOf(monkey to Pair(firstResults[monkey.name]!!, secondResults[monkey.name]!!)))
                }
            }

        return go(monkeyFinder.find(ROOT), mapOf())
    }

}