package com.gilpereda.adventsofcode.adventsofcode2021.day08

fun countDigits(input: Sequence<String>): String =
    input.map(::parseLine)
        .flatMap { it.outputs }
        .filter { it.length in listOf(2, 3, 4, 7) }
        .count().toString()

fun decodeDigits(input: Sequence<String>): String =
    input.map(::parseLine)
        .map(::findCode)
        .sum().toString()

fun findCode(line: Line): Int {
    val one = line.inputs.first { it.length == 2 }.let(::One)
    val four = line.inputs.first { it.length == 4 }.let(::Four)
    val seven = line.inputs.first { it.length == 3 }.let(::Seven)
    val eight = line.inputs.first { it.length == 7 }.let(::Eight)
    val nine = findNine(line.inputs, four)

    val three = findThree(line.inputs, one)
    val (zero, six) = findZeroSix(line.inputs, one, nine)
//    val s0 = findS0(one, seven)
    val s2 = findS2(six)
//    val s3 = findS3(zero)
//    val s4 = findS4(nine)

    val (five, two) = findTwoAndFive(line.inputs, three, s2)


    return line.outputs.map { code ->
        when (code.sorted) {
            zero.code -> '0'
            one.code -> '1'
            two.code -> '2'
            three.code -> '3'
            four.code -> '4'
            five.code -> '5'
            six.code -> '6'
            seven.code -> '7'
            eight.code -> '8'
            nine.code -> '9'
            else -> throw Exception("code $code not found")
        }
    }.joinToString("").toInt()
}

fun findS0(one: One, seven: Seven): Char =
    seven.segments.first { it !in one.segments }

fun findS3(zero: Zero): Char =
    "abcdefg".toList().first { it !in zero.segments }

fun findZeroSix(inputs: List<String>, one: One, nine: Nine): Pair<Zero, Six> =
    inputs.filter { it.length == 6 }
        .filter { it.sorted != nine.code }
        .partition { code -> code.filter { it in one.segments }.length == 1 }.let { (six, zero) -> zero.first().let(::Zero) to six.first().let(::Six) }

fun findS2(six: Six): Char =
    "abcdefg".toList().first { it !in six.segments }

fun findS1(one: One, four: Four, s3: Char): Char =
    four.segments.first { it !in one.segments && it != s3 }

fun findS4(nine: Nine): Char =
    "abcdefg".toList().first { it !in nine.segments }

fun findS2(five: Five, s4: Char): Char =
    "abcdefg".toList().first { it != s4 && it !in five.segments }

fun findThree(inputs: List<String>, one: One): Three =
    inputs.filter { it.length == 5 }
        .first { code -> code.filter { it in one.segments }.length == 2 }.let(::Three)

fun findTwoAndFive(inputs: List<String>, three: Three, s2: Char): Pair<Five, Two> =
    inputs.filter { it.length == 5 && it.sorted != three.code }.partition { s2 in it }.let { (two, five) -> five.first().let(::Five) to two.first().let(::Two) }

fun findNine(inputs: List<String>, four: Digit): Nine =
    inputs.first { input -> input.length == 6 && four.segments.all { it in input } }.let(::Nine)

//fun findZeroSix(inputs: List<String>, s2: Char): Pair<Zero, Six> =
//    inputs.filter { it.length == 6 }.partition { s2 in it }.let { (zero, six) -> zero.first().let(::Zero) to six.first().let(::Six) }

fun findTwoAndThree(inputs: List<String>, s1: Char, s4: Char): Pair<Two, Three> =
    inputs.filter { it.length == 5 && s1 !in it }.partition { s4 in it }.let { (two, three) -> two.first().let(::Two) to three.first().let(::Three) }

sealed interface Digit {
    val code: String
    val digit: Char

    val segments: List<Char>
        get() = code.toList()
}

data class Zero(private val _code: String) : Digit {
    override val digit: Char = '0'
    override val code: String = _code.sorted
}

data class One(private val _code: String) : Digit {
    override val digit: Char = '1'
    override val code: String = _code.sorted
}

data class Two(private val _code: String) : Digit {
    override val digit: Char = '2'
    override val code: String = _code.sorted
}

data class Three(private val _code: String) : Digit {
    override val digit: Char = '3'
    override val code: String = _code.sorted
}

data class Four(private val _code: String) : Digit {
    override val digit: Char = '4'
    override val code: String = _code.sorted
}

data class Five(private val _code: String) : Digit {
    override val digit: Char = '5'
    override val code: String = _code.sorted
}

data class Six(private val _code: String) : Digit {
    override val digit: Char = '6'
    override val code: String = _code.sorted
}

data class Seven(private val _code: String) : Digit {
    override val digit: Char = '7'
    override val code: String = _code.sorted
}

data class Eight(private val _code: String) : Digit {
    override val digit: Char = '8'
    override val code: String = _code.sorted
}

data class Nine(private val _code: String) : Digit {
    override val digit: Char = '9'
    override val code: String = _code.sorted
}

fun parseLine(line: String): Line =
    line.split(" | ").let { (inputStr, outputStr) ->
        Line(inputStr.split(" "), outputStr.split(" "))
    }

data class Line(val inputs: List<String>, val outputs: List<String>)

val String.sorted: String
    get() = toList().sorted().joinToString("")

