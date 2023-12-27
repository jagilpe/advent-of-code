package com.gilpereda.adventsofcode.adventsofcode2021.day16

import com.gilpereda.adventsofcode.adventsofcode2021.BaseTest
import com.gilpereda.adventsofcode.adventsofcode2021.Executable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class Day16Test1 : BaseTest() {
    //    override val example: String = "8A004A801A8002F478620080001611562C8802118E34C0015000016115A2E0802F182340A0016C880162017C3686B18A3D4780"
    override val example: String = "8A004A801A8002F478"

    override val result1: String = "${16 + 12 + 23 + 31}"
    override val result2: String
        get() = TODO()

    override val input: String = "/day16/input.txt"

    override val run1: Executable = ::day16_1
    override val run2: Executable = ::day16_2

    @Test
    fun `should decode a literal message`() {
        val data = "110100101111111000101000"
        val packet = LiteralPacket(version = 6, value = 2021)
        val consumed = "110100101111111000101"

        assertThat(data.parsePacket()).isEqualTo(Pair(packet, consumed))
    }

    @ParameterizedTest
    @MethodSource("sumVersions")
    fun `should sum the versions of an operator`(input: String, result: Int) {
        assertThat(day16_1(input.splitToSequence("\n"))).isEqualTo(result.toString())
    }

    @Test
    fun `should decode a bits length operator`() {
        val data = "00111000000000000110111101000101001010010001001000000000"
        val packets = listOf(LiteralPacket(6, 10), LiteralPacket(2, 20))
        val packet = OperatorPacket(version = 1, Operation.fromType(6, packets))
        val consumed = "0011100000000000011011110100010100101001000100100"

        assertThat(data.parsePacket()).isEqualTo(Pair(packet, consumed))
    }

    @Test
    fun `should decode a subpackets count operator`() {
        val data = "11101110000000001101010000001100100000100011000001100000"
        val packets = listOf(LiteralPacket(2, 1), LiteralPacket(4, 2), LiteralPacket(1, 3))
        val packet = OperatorPacket(version = 7, Operation.fromType(3, packets))
        val consumed = "111011100000000011010100000011001000001000110000011"

        assertThat(data.parsePacket()).isEqualTo(Pair(packet, consumed))
    }

    @Test
    fun `should get the next step`() {
        val data = "11101110000000001101010000001100100000100011000001100000"
        val rest = "00000"
        val packet = Packet_Old(7)

        assertThat(nextStep(data)).isEqualTo(Step(rest, listOf(packet)))
    }

    @ParameterizedTest
    @MethodSource("calculateResults")
    fun `should find the result`(input: String, result: Int) {
        assertThat(day16_2(input.splitToSequence("\n"))).isEqualTo(result.toString())
    }

    fun sum(vararg value: Long): Long = value.sum()
    fun product(vararg value: Long): Long = value.fold(1) { acc, next -> acc * next }
    fun lessThan(one: Long, other: Long): Long = if (one < other) 1 else 0
    fun greaterThan(one: Long, other: Long): Long = if (one > other) 1 else 0
    fun equal(one: Long, other: Long): Long = if (one == other) 1 else 0
    fun minimum(vararg value: Long): Long = value.minOrNull()!!
    fun maximum(vararg value: Long): Long = value.maxOrNull()!!

    @Test
    fun test() {
        val result = sum(
            product(
                425542,
                lessThan(247, 247)
            ),
            sum(121, 21236),
            product(
                greaterThan(
                    sum(11, 12, 11),
                    sum(7, 10, 7)
                ),
                32566
            ),
            product(
                lessThan(
                    sum(8, 7, 15),
                    sum(6, 11, 10)
                ),
                4507180
            ),
            minimum(
                product(
                    product(
                        sum(
                            product(
                                maximum(
                                    product(
                                        minimum(
                                            product(
                                                minimum(
                                                    product(
                                                        maximum(
                                                            sum(
                                                                sum(
                                                                    maximum(sum(sum(minimum(product(product(130))))))
                                                                )
                                                            )
                                                        )
                                                    )
                                                )
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            ),
            344341710,
            product(greaterThan(52, 667118), 10),
            602147,
            maximum(62199),
            product(14849899, lessThan(11716, 26963)),
            product(4083, greaterThan(135, 135)),
            product(135, 217, 224),
            73,
            product(sum(13, 4, 9), sum(12, 15, 7), sum(13, 10, 9)),
            minimum(194),
            product(182, 197, 136, 2, 242),
            product(226, 142, 34, 124),
            maximum(4025, 186042),
            minimum(30059, 126119002),
            minimum(9, 260, 162),
            product(lessThan(4, 4), 28699),
            product(1945, equal(1714, 1714)),
            product(7, lessThan(1545, 108)),
            sum(12),
            product(200, greaterThan(31050, 655605)),
            3154,
            product(3, lessThan(64896, 116)),
            3055,
            product(13),
            minimum(48082, 226938, 1175, -641701822),
            sum(66, 15, 181, 1380642642, 11831587),
            product(241, 59),
            product(150, greaterThan(2742, 113)),
            500686583,
            maximum(52444, 11, 13008816, 2935),
            20723,
            8,
            product(5, greaterThan(6241732, 759708)),
            sum(product(15, 7, 4), product(14, 2, 12), product(13, 6, 6)),
            sum(2877, 229333, 655820, 1020971),
            sum(39581, 2, 14),
            maximum(982557, 44, 31),
            68,
            product(equal(11530, 3492), 41177),
            product(equal(236, 918711093), 3937),
            maximum(903466, 228, 6, 25989131, 4028),
            229,
            minimum(299875, 10969849, 11481, 2281, 13),
            product(55300721, greaterThan(63, 63)),
            product(244, greaterThan(sum(7, 13, 7), sum(12, 5, 14))),
            product(4494263, equal(sum(4, 15, 4), sum(3, 3, 14))),
            product(lessThan(45, 3307915), 58514),
            product(-698436604, lessThan(sum(3, 12, 4), sum(9, 11, 2)))
        )

        assertThat(result).isNotNull
        println(result)
    }

    companion object {
        @JvmStatic
        fun sumVersions(): Stream<Arguments> = Stream.of(
            Arguments.of("8A004A801A8002F478", 16),
            Arguments.of("620080001611562C8802118E34", 12),
            Arguments.of("C0015000016115A2E0802F182340", 23),
            Arguments.of("A0016C880162017C3686B18A3D4780", 31),
        )

        @JvmStatic
        fun calculateResults(): Stream<Arguments> = Stream.of(
            Arguments.of("C200B40A82", 3),
            Arguments.of("04005AC33890", 54),
            Arguments.of("880086C3E88112", 7),
            Arguments.of("CE00C43D881120", 9),
            Arguments.of("D8005AC2A8F0", 1),
            Arguments.of("F600BC2D8F", 0),
            Arguments.of("9C005AC2F8F0", 0),
            Arguments.of("9C0141080250320F1802104A08", 1),
        )
    }
}