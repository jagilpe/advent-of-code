package com.gilpereda.aoc2022.day12

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class Day12Test : BaseTest() {
    override val example: String = """
        ???.### 1,1,3
        .??..??...?##. 1,1,3
        ?#?#?#?#?#?#?#? 1,3,1,6
        ????.#...#... 4,1,1
        ????.######..#####. 1,6,5
        ?###???????? 3,2,1
    """.trimIndent()

//    override val example: String = """
//        ?###???????? 3,2,1
//    """.trimIndent()

    override val result1: String = "21"

    override val result2: String = "525152"

    override val input: String = "/day12/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask

    val test = "?###??????????###??????????###??????????###??????????###???????? 3,2,1,3,2,1,3,2,1,3,2,1,3,2,1"


//    @ParameterizedTest
//    @MethodSource("conditionMatching")
//    fun `should work`(conditions: String, expected: List<Int>, match: Boolean) {
//        assertThat(conditions.canMatch(expected, expected.max())).isEqualTo(match)
//    }
//
//    companion object {
//        @JvmStatic
//        fun conditionMatching(): Stream<Arguments> = Stream.of(
////            Arguments.of(".??..??...?##.", listOf(1,1,3), true),
////            Arguments.of(".#?..??...?##.", listOf(1,1,3), true),
////            Arguments.of(".....??...?##.", listOf(1,1,3), false),
//            Arguments.of(".##..??...?##.", listOf(1,1,3), false),
////            Arguments.of("..#..??...?##.", listOf(1,1,3), true),
//        )
//    }
}