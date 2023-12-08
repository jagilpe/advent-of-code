package com.gilpereda.aoc2022.day07

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day07Test : BaseTest() {
    override val example: String = """
        32T3K 765
        T55J5 684
        KK677 28
        KTJJT 220
        QQQJA 483
    """.trimIndent()

    override val example2: String = """
        4KK4J 350
        24Q8T 722
        77877 656
        TJTKQ 461
        2J224 383
        K9K89 123
        3A254 425
        335A5 981
        A3334 625
        A46Q3 169
        JJJJ8 618
        AJTQ6 771
        742J7 238
        TTT7T 935
        J9999 116
        567Q9 307
        6992J 274
        AT285 952
        6J83J 170
        5559T 302
        K9A43 81
        9TTT9 35
    """.trimIndent()

    override val result1: String = "6440"

//    override val result2: String = "5905"
    override val result2: String =
        listOf(
            722,
            425,
            307,
            81,
            169,
            952,
            771,
            981,
            123,
            302,
            170,
            274,
            238,
            461,
            625,
            350,
            35,
            383,
            656,
            935,
            618,
            116).mapIndexed { index, i -> (index + 1) * i }.sum().toString()

    override val input: String = "/day07/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask


//    @Test
//    fun `compare should work with jokers`() {
//        val one = Game.Companion.OnePair(cards = "2J543", bet = 0, withJokers = true)
//        val other = Game.Companion.OnePair(cards = "2TQ3J", bet = 0, withJokers = true)
//        assertThat(one).isLessThan(other)
//    }
//
//    @Test
//    fun `compare should work`() {
//        val one = Game.Companion.OnePair(cards = "2J543", bet = 0, withJokers = false)
//        val other = Game.Companion.OnePair(cards = "2TQ3J", bet = 0, withJokers = false)
//        assertThat(one).isGreaterThan(other)
//    }

    @Test
    fun `should parse the game`() {
        val game = Game.fromLineWithJokers("34542 123")

        assertThat(game is Game.Companion.OnePair).isTrue()
    }
}