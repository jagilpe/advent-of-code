package com.gilpereda.adventsofcode.adventsofcode2021.day16

import kotlin.math.pow


fun String.parse_old(trim: Boolean = true): List<Packet_Old> {
    return generateSequence(Step(this)) { step ->
        val data = step.rest
        when {
            data.isBlank() -> null
            else -> {
                val nextStep = nextStep(data)
                if (trim) nextStep.copy(rest = trim(data, nextStep.rest)) else nextStep
            }
        }
    }.flatMap { it.packets }.toList()
}


fun String.parse(count: Int): List<Step> {
    return generateSequence(Step(this)) { step -> nextStep(step.rest) }.take(count).toList()
}

fun nextStep(data: String): Step {
    val type = data.substring(3, 6)
    return when (type) {
        LITERAL -> nextLiteralStep(data)
        else -> {
            val lengthId = data.substring(6, 7)
            when (lengthId) {
                "0" -> nextBitsLengthStep(data)
                else -> nextPacketsLengthStep(data)
            }
        }
    }
}

fun nextLiteralStep(data: String): Step {
    val (version, _) = getVersionAndType(data)
    tailrec fun go(payload: String, acc: List<String>): Pair<String, List<String>> {
        val (digit, rest) = payload.splitAt(5)
        return if (digit.first() == '1') {
            go(rest, acc + digit.drop(1))
        } else {
            Pair(rest, acc + digit.drop(1))
        }
    }
    val (rest, _) = go(data.drop(6), emptyList())
    return Step(rest, listOf(Packet_Old(version)))
}

fun nextBitsLengthStep(data: String): Step {
    val (version, _) = getVersionAndType(data)
    val (messageLength, rest) = data.drop(7).splitAt(15)
    val (messageBody, rest1) = rest.splitAt(messageLength.binaryToInt)
    val packet = Packet_Old(version = version)
//    val subPackets = messageBody.parse(false)
//    return Step(rest1, listOf(packet, *subPackets.toTypedArray()))
    return TODO()
}

fun nextPacketsLengthStep(data: String): Step {
    val (version, _) = getVersionAndType(data)
    val (packetCountBin, rest) = data.drop(7).splitAt(11)
    val packetCount = packetCountBin.binaryToInt

    val packet = Packet_Old(version = version)
    val subSteps = rest.parse(packetCount)
    val subPackets = subSteps.flatMap { it.packets }.toTypedArray()
    return Step(subSteps.last().rest, listOf(packet, *subPackets))
}


private fun trim(data: String, rest: String): String =
    rest.drop(4 - (data.length - rest.length) % 4)

fun String.splitAt(i: Int): Pair<String, String> =
    Pair(substring(0, i), substring(i))

data class Step(val rest: String, val packets: List<Packet_Old> = emptyList())

val hexToBinMap = mapOf(
    '0' to "0000",
    '1' to "0001",
    '2' to "0010",
    '3' to "0011",
    '4' to "0100",
    '5' to "0101",
    '6' to "0110",
    '7' to "0111",
    '8' to "1000",
    '9' to "1001",
    'A' to "1010",
    'B' to "1011",
    'C' to "1100",
    'D' to "1101",
    'E' to "1110",
    'F' to "1111",
)

val binToHexMap = mapOf(
    "0000" to '0',
    "0001" to '1',
    "0010" to '2',
    "0011" to '3',
    "0100" to '4',
    "0101" to '5',
    "0110" to '6',
    "0111" to '7',
    "1000" to '8',
    "1001" to '9',
    "1010" to 'A',
    "1011" to 'B',
    "1100" to 'C',
    "1101" to 'D',
    "1110" to 'E',
    "1111" to 'F',
)

fun hexToBin(char: Char): String = hexToBinMap[char]!!

fun String.toHex(): String =
    chunked(4).map { binToHexMap[it]!! }.joinToString("")

fun String.hexToBin(): String =
    toList().asSequence().map(::hexToBin).joinToString("")

enum class IdLength(val length: Int) {
    ID_15(15),
    ID_11(11);

    companion object {
        fun fromBit(bit: String): IdLength =
            when (bit) {
                "0" -> ID_15
                "1" -> ID_11
                else -> throw IllegalArgumentException()
            }
    }
}


data class Packet_Old(
    val version: Int,
)

val String.binaryToInt: Int
    get() = reversed().mapIndexed { i, c -> if (c == '1') 2.0.pow(i).toInt() else 0 }.sum()

