package com.gilpereda.adventsofcode.adventsofcode2021.day16

import kotlin.math.pow


const val LITERAL = "100"

@Suppress("FunctionName")
fun day16_1(input: Sequence<String>): String =
    input.first().hexToBin()
        .parse().sumOf(Packet::versionSum).toString()

fun day16_2(input: Sequence<String>): String {
    val operator = input.first().hexToBin()
        .parse()
    return operator.first().value.toString()
}


fun String.parse(): List<Packet> =
    generateSequence(Pair(listOf<Packet>(), this)) { (packets, data) ->
        if (data.length > 11) {
            val (packet, consumed) = data.parsePacket()
            Pair(packets + packet, data.substring(consumed.length))
        } else {
            null
        }
    }.last().first


fun String.parsePacket(): Pair<Packet, String> {
    val type = substring(3, 6)
    val packet = when (type) {
        LITERAL -> LiteralPacket.parse(this)
        else -> OperatorPacket.parse(this)
    }
    return packet
}


sealed interface Packet : Operation {
    val versionSum: Int
}

data class LiteralPacket(val version: Int, override val value: Long) : Packet {
    override val packets: List<Packet> = emptyList()
    override val versionSum: Int = version

    override fun toString(): String = value.toString()

    companion object {
        fun parse(data: String): Pair<Packet, String> {
            val version = getVersionAndType(data).first
            tailrec fun go(payload: String, acc: List<String>): Pair<String, List<String>> {
                val (digit, rest) = payload.splitAt(5)
                return if (digit.first() == '1') {
                    go(rest, acc + digit.drop(1))
                } else {
                    Pair(rest, acc + digit.drop(1))
                }
            }
            val (rest, digits) = go(data.drop(6), emptyList())
            val value = digits.joinToString("").binaryToLong
            val consumed = data.length - rest.length
            val toIgnore = 4 - (consumed % 4)
            return Pair(LiteralPacket(version, value.toLong()), data.substring(0, consumed))
        }
    }
}

data class OperatorPacket(
    val version: Int,
    val operation: Operation
) : Packet, Operation by (operation) {
    override val versionSum: Int
        get() = version + packets.sumOf { it.versionSum }

    override fun toString(): String = operation.toString()

    companion object {
        fun parse(data: String): Pair<Packet, String> =
            when (data.substring(6, 7)) {
                "0" -> parseFromLength(data)
                else -> parseFromCount(data)
            }

        private fun parseFromLength(data: String): Pair<Packet, String> {
            val (version, type) = getVersionAndType(data)
            val messageLength = data.substring(7, 22).binaryToInt
            val messageBody = data.substring(22, 22 + messageLength)
            val (packets, rest) = generateSequence(Pair(emptyList<Packet>(), messageBody)) { (packets, body) ->
                if (body.length > 10) {
                    val (packet, consumed) = body.parsePacket()
                    val rest = body.substring(consumed.length)
                    Pair(packets + packet, rest)
                } else {
                    null
                }
            }.last()

            val packet = OperatorPacket(version = version, operation = Operation.fromType(type, packets))
            val consumed = data.substring(0, 22 + messageLength)
            return Pair(packet, consumed)
        }

        private fun parseFromCount(data: String): Pair<Packet, String> {
            val (version, type) = getVersionAndType(data)
            val messageCount = data.substring(7, 18).binaryToInt
            val messageBody = data.substring(18)

            val (packets, rest) = generateSequence(Pair(emptyList<Packet>(), messageBody)) { (packets, body) ->
                val (packet, consumed) = body.parsePacket()
                val rest = body.substring(consumed.length)
                Pair(packets + packet, rest)
            }.first { (packets, _) -> packets.size == messageCount }

            val packet = OperatorPacket(
                version = version,
                operation = Operation.fromType(type, packets)
            )
            val consumed = data.substring(0, data.length - rest.length)
            return Pair(packet, consumed)
        }
    }
}

sealed interface Operation {
    val value: Long
    val packets: List<Packet>

    companion object {
        fun fromType(type: Int, packets: List<Packet>) =
            when (type) {
                0 -> Sum(packets)
                1 -> Product(packets)
                2 -> Minimum(packets)
                3 -> Maximum(packets)
                5 -> GreaterThan(packets)
                6 -> LessThan(packets)
                7 -> Equal(packets)
                else -> throw IllegalArgumentException("Unknown type $type")
            }
    }
}

data class Sum(override val packets: List<Packet>) : Operation {
    override val value: Long
        get() = packets.sumOf { it.value }

    override fun toString(): String = "sum(${packets.joinToString(", ")})"
}

data class Product(override val packets: List<Packet>) : Operation {
    override val value: Long
        get() = packets.map { it.value }.reduce { a, b -> a * b }

    override fun toString(): String = "product(${packets.joinToString(", ")})"
}

data class Minimum(override val packets: List<Packet>) : Operation {
    override val value: Long = packets.minOf { it.value }

    override fun toString(): String = "minimum(${packets.joinToString(", ")})"
}

data class Maximum(override val packets: List<Packet>) : Operation {
    override val value: Long = packets.maxOf { it.value }

    override fun toString(): String = "maximum(${packets.joinToString(", ")})"
}

data class GreaterThan(override val packets: List<Packet>) : Operation {
    override val value: Long
        get() {
            assert(packets.size == 2)
            return if (packets[0].value > packets[1].value) 1 else 0
        }

    override fun toString(): String = "greaterThan(${packets.joinToString(", ")})"
}

data class LessThan(override val packets: List<Packet>) : Operation {
    override val value: Long
        get() {
            assert(packets.size == 2)
            return if (packets[0].value < packets[1].value) 1 else 0
        }

    override fun toString(): String = "lessThan(${packets.joinToString(", ")})"
}

data class Equal(override val packets: List<Packet>) : Operation {
    override val value: Long
        get() {
            assert(packets.size == 2)
            return if (packets[0].value == packets[1].value) 1 else 0
        }

    override fun toString(): String = "equal(${packets.joinToString(", ")})"
}

fun getVersionAndType(data: String): Pair<Int, Int> =
    Pair(data.substring(0, 3).binaryToInt, data.substring(3, 6).binaryToInt)



val String.binaryToLong: Long
    get() = reversed().mapIndexed { i, c -> if (c == '1') 2.0.pow(i).toLong() else 0 }.sum()
