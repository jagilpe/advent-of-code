package com.gilpereda.aoc2022.day13

import com.gilpereda.aoc2022.BaseTest
import com.gilpereda.aoc2022.Executable
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.`in`
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.of
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class Day13Test : BaseTest() {
    override val example: String = """[1,1,3,1,1]
[1,1,5,1,1]

[[1],[2,3,4]]
[[1],4]

[9]
[[8,7,6]]

[[4,4],4,4]
[[4,4],4,4,4]

[7,7,7,7]
[7,7,7]

[]
[3]

[[[]]]
[[]]

[1,[2,[3,[4,[5,6,7]]]],8,9]
[1,[2,[3,[4,[5,6,0]]]],8,9]"""

    override val result1: String = "13"

    override val result2: String = "140"

    override val input: String = "/day13/input"

    override val run1: Executable = ::firstTask

    override val run2: Executable = ::secondTask

    @ParameterizedTest
    @MethodSource("tokenizer")
    fun `should tokenize the string`(input: String, expected: List<String>) {
        assertThat(tokenize(input)).isEqualTo(expected)
    }

    @ParameterizedTest
    @MethodSource("parseToken")
    fun `should parse a token`(input: String, expected: PackageItem) {
        assertThat(parseToken(input)).isEqualTo(expected)
    }

    @ParameterizedTest
    @MethodSource("inOrder")
    fun `should check if it is ordered`(input: String, expected: Boolean) {
        assertThat(parsePair(input).inOrder).isEqualTo(expected)
    }

    companion object {
        @JvmStatic
        fun inOrder(): Stream<Arguments> = Stream.of(
            of("[1,1,3,1,1]\n[1,1,5,1,1]", true),
            of("[[1],[2,3,4]]\n[[1],4]", true),
            of("[9]\n[[8,7,6]]", false),
            of("[[4,4],4,4]\n[[4,4],4,4,4]", true),
            of("[7,7,7,7]\n[7,7,7]", false),
            of("[]\n[3]", true),
            of("[[[]]]\n[[]]", false),
            of("[1,[2,[3,[4,[5,6,7]]]],8,9]\n[1,[2,[3,[4,[5,6,0]]]],8,9]", false),
        )


        @JvmStatic
        fun parseToken(): Stream<Arguments> = Stream.of(
            of("11", SingleItem(11)),
            of("[1]", ofItems(1)),
            of("[]", ofItems()),
            of(
                "[2,[3,[4,[5,6,7]]]]", ItemList(
                    listOf(
                        SingleItem(2),
                        ItemList(
                            listOf(
                                SingleItem(3),
                                ItemList(
                                    listOf(
                                        SingleItem(4),
                                        ofItems(5, 6, 7)
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )

        @JvmStatic
        fun tokenizer(): Stream<Arguments> = Stream.of(
            of("11,1,3,1,1", listOf("11", "1", "3", "1", "1")),
            of("[1],[2,3,4]", listOf("[1]", "[2,3,4]")),
            of("1,[2,[3,[4,[5,6,7]]]],8,9", listOf("1", "[2,[3,[4,[5,6,7]]]]", "8", "9")),
        )

        @JvmStatic
        fun packageParsing(): Stream<Arguments> = Stream.of(
            of(
                "[1,1,3,1,1]", ofItems(1, 1, 3, 1),
                of("[1,1,5,1,1]", ofItems(1, 1, 5, 1, 1)),
                of("[[1],[2,3,4]]", ItemList(listOf(ofItems(1), ofItems(2, 3, 4)))),
                of(
                    "[[1],4]", ItemList(listOf(ofItems(1), SingleItem(4))),
                    of("[9]", ofItems(9)),
                    of("[[8,7,6]]", ItemList(listOf(ofItems(8, 7, 6)))),
                    of("[[4,4],4,4]", ItemList(listOf(ofItems(4, 4), SingleItem(4), SingleItem(4)))),
                    of(
                        "[[4,4],4,4,4]",
                        ItemList(listOf(ofItems(4, 4), SingleItem(4), SingleItem(4), SingleItem(4)))
                    ),
                    of("[7,7,7,7]", ofItems(7, 7, 7, 7)),
                    of("[7,7,7]", ofItems(7, 7, 7)),
                    of("[]", ItemList(emptyList())),
                    of("[3]", ofItems(3)),
                    of("[[[]]]", ItemList(listOf(ItemList(listOf(ItemList(emptyList())))))),
                    of("[[]]", ItemList(listOf(ItemList(emptyList())))),
                    of(
                        "[1,[2,[3,[4,[5,6,7]]]],8,9]",
                        ItemList(
                            listOf(
                                SingleItem(1),
                                ItemList(
                                    listOf(
                                        SingleItem(2),
                                        ItemList(
                                            listOf(
                                                SingleItem(3),
                                                ItemList(
                                                    listOf(
                                                        SingleItem(4),
                                                        ofItems(5, 6, 7)
                                                    )
                                                )
                                            ),
                                        )
                                    )
                                ),
                                SingleItem(8),
                                SingleItem(9)
                            ),
                        )
                    ),
                    of(
                        "[1,[2,[3,[4,[5,6,0]]]],8,9]",
                        ItemList(
                            listOf(
                                SingleItem(1),
                                ItemList(
                                    listOf(
                                        SingleItem(2),
                                        ItemList(
                                            listOf(
                                                SingleItem(3),
                                                ItemList(
                                                    listOf(
                                                        SingleItem(4),
                                                        ofItems(5, 6, 0)
                                                    )
                                                )
                                            ),
                                        )
                                    )
                                ),
                                SingleItem(8),
                                SingleItem(9)
                            ),
                        )
                    ),
                )
            )
        )
    }
}

fun ofItems(vararg items: Int): ItemList =
    ItemList(items.map(::SingleItem))