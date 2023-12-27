package com.gilpereda.adventsofcode.adventsofcode2021.day18

import com.gilpereda.adventsofcode.adventsofcode2021.BaseTest
import com.gilpereda.adventsofcode.adventsofcode2021.Executable
import com.gilpereda.adventsofcode.adventsofcode2021.day18.Snailfish.Companion.snf
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestTemplate
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class Day18Test : BaseTest() {
    override val example: String = """
        [[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]]
        [[[5,[2,8]],4],[5,[[9,9],0]]]
        [6,[[[6,2],[5,6]],[[7,6],[4,7]]]]
        [[[6,[0,7]],[0,9]],[4,[9,[9,0]]]]
        [[[7,[6,4]],[3,[1,3]]],[[[5,5],1],9]]
        [[6,[[7,3],[3,2]]],[[[3,8],[5,7]],4]]
        [[[[5,4],[7,7]],8],[[8,3],8]]
        [[9,3],[[9,9],[6,[4,9]]]]
        [[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]]
        [[[[5,2],5],[8,[3,7]]],[[5,[7,5]],[4,4]]]
    """.trimIndent()

    override val result1: String = "4140"

    override val result2: String = "3993"

    override val input: String = "/day18/input.txt"
    override val run1: Executable = part1
    override val run2: Executable = part2

    @Test
    fun `should parse a simple pair`() {
        assertThat(parseSnailfish("[1,2]")).isEqualTo(Pair(snf(1, 2), ""))
        assertThat(parseSnailfish("[3,5]")).isEqualTo(Pair(snf(3, 5),""))
    }

    @Test
    fun `should parse nested snalfishes`() {
        val line = "[[6,2],[5,6]]"
        val expected = snf(
            snf(6, 2),
            snf(5, 6)
        )
        assertThat(parseSnailfish(line)).isEqualTo(Pair(expected, ""))
    }

    @Test
    fun `should make a simple sum`() {
        assertThat(snf(1, 2) + snf(3, 4)).isEqualTo(snf(snf(1, 2), snf(3, 4)))
    }

    @Test
    fun `should make multiple sums`() {
        val actual = snf(1, 2) + snf(3, 4) + snf(4, 5) + snf(6, 7)
        val expected = snf(snf(snf(snf(1, 2), snf(3, 4)), snf(4,5)), snf(6, 7))
        assertThat(actual).isEqualTo(expected)
    }

    @ParameterizedTest
    @MethodSource("reduction")
    fun `should reduce the snailfish`(original: Snailfish, reduced: Snailfish) {
        assertThat(original.reduced()).isEqualTo(reduced)
    }

    @ParameterizedTest
    @MethodSource("reduction")
    fun `should detect if reduction is needed`(original: Snailfish, ignored: Snailfish) {
        assertThat(original.isReduced).isFalse
    }
    
    @Test
    fun `should split the numbers bigger that 9`() {
        val initial = snf(snf(snf(snf(0,7),4),snf(15,snf(0,13))),snf(1,1))
        val expected = snf(snf(snf(snf(0,7),4),snf(snf(7,8),snf(6,0))),snf(8,1))

        assertThat(initial.reduced()).isEqualTo(expected)
    }
    
    @Test
    fun `should make the sum`() {
        val sum = snf(snf(snf(0,snf(4,5)),snf(0,0)),snf(snf(snf(4,5),snf(2,6)),snf(9,5))) + snf(7,snf(snf(snf(3,7),snf(4,3)),snf(snf(6,3),snf(8,8))))
        val expected = snf(snf(snf(snf(4,0),snf(5,4)),snf(snf(7,7),snf(6,0))),snf(snf(8,snf(7,7)),snf(snf(7,9),snf(5,0))))

        assertThat(sum.reduced()).isEqualTo(expected)
    }

    @Test
    fun `should make the sum 2`() {
        val sum = snf(snf(snf(snf(4,3),4),4),snf(7,snf(snf(8,4),9))) + snf(1,1)
        val expected = snf(snf(snf(snf(0,7),4),snf(snf(7,8),snf(6,0))),snf(8,1))

        assertThat(sum.reduced()).isEqualTo(expected)
    }

    @ParameterizedTest
    @MethodSource("sums")
    fun `should sum`(fishes: List<Snailfish>, expected: Snailfish) {
        val actual = fishes.reduce { one, two -> (one + two).reduced() }
        assertThat(actual).isEqualTo(expected)
    }

    companion object {
        @JvmStatic
        fun reduction(): Stream<Arguments> =
            Stream.of(
                Arguments.of(snf(snf(snf(snf(snf(9,8),1),2),3),4), snf(snf(snf(snf(0,9),2),3),4)),
                Arguments.of(snf(7,snf(6,snf(5,snf(4,snf(3,2))))), snf(7,snf(6,snf(5,snf(7,0))))),
                Arguments.of(snf(snf(6,snf(5,snf(4,snf(3,2)))),1), snf(snf(6,snf(5,snf(7,0))),3)),
                Arguments.of(snf(snf(3,snf(2,snf(8,0))),snf(9,snf(5,snf(4,snf(3,2))))), snf(snf(3,snf(2,snf(8,0))),snf(9,snf(5,snf(7,0))))),
            )

        @JvmStatic
        fun sums(): Stream<Arguments> =
            Stream.of(
//                Arguments.of(listOf(snf(1,1), snf(2,2), snf(3,3), snf(4,4)), snf(snf(snf(snf(1,1),snf(2,2)),snf(3,3)),snf(4,4))),
//                Arguments.of(listOf(snf(1,1), snf(2,2), snf(3,3), snf(4,4), snf(5,5)), snf(snf(snf(snf(3,0),snf(5,3)),snf(4,4)),snf(5,5))),
//                Arguments.of(listOf(snf(1,1), snf(2,2), snf(3,3), snf(4,4), snf(5,5), snf(6,6)), snf(snf(snf(snf(5,0),snf(7,4)),snf(5,5)),snf(6,6))),
                Arguments.of(listOf(snf(snf(snf(0,snf(4,5)),snf(0,0)),snf(snf(snf(4,5),snf(2,6)),snf(9,5))), snf(7,snf(snf(snf(3,7),snf(4,3)),snf(snf(6,3),snf(8,8))))), snf(snf(snf(snf(4,0),snf(5,4)),snf(snf(7,7),snf(6,0))),snf(snf(8,snf(7,7)),snf(snf(7,9),snf(5,0))))),
                Arguments.of(listOf(snf(snf(snf(snf(4,0),snf(5,4)),snf(snf(7,7),snf(6,0))),snf(snf(8,snf(7,7)),snf(snf(7,9),snf(5,0)))), snf(snf(2,snf(snf(0,8),snf(3,4))),snf(snf(snf(6,7),1),snf(7,snf(1,6))))), snf(snf(snf(snf(6,7),snf(6,7)),snf(snf(7,7),snf(0,7))),snf(snf(snf(8,7),snf(7,7)),snf(snf(8,8),snf(8,0))))),
            )
    }
}


