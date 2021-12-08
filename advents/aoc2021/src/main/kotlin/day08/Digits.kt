package com.gilpereda.adventsofcode.adventsofcode2021

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

    val s1 = findS1(one, four)

    val zeroSixOrNineCodes = line.inputs.filter { it.length == 6 }
    val twoThreeOrFiveCodes = line.inputs.filter { it.length == 5 }
    val winner = permutations
        .asSequence()
        .filter { one.matches(it) }
        .filter { four.matches(it) }
        .filter { seven.matches(it) }
        .filter { eight.matches(it) }
        .filter { matchesTwoSixOrNine(it, twoThreeOrFiveCodes) }
        .filter { matchesZeroSixOrNineCodes(it, zeroSixOrNineCodes) }.first()

//    return line.outputs.map { code -> Digit.getDigit(winner, code) }.joinToString("").toInt()
    return 10
}

fun findS1(one: One, four: Four): Char


fun matchesTwoSixOrNine(permutation: String, codes: List<String>): Boolean = TODO()

fun matchesZeroSixOrNineCodes(permutation: String, codes: List<String>): Boolean = TODO()

sealed interface Digit {
    val segments: List<Int>
    val code: String
    val digit: Char

    fun matches(permutation: String): Boolean =
        permutation.filterIndexed { i, _ -> i in segments }.all { it in code }

    companion object {
        fun getDigit(winner: String, code: String): Digit = TODO()
    }
}

data class Zero(override val code: String) : Digit {
    override val segments: List<Int> = listOf(0, 1, 2, 4, 5, 6)
    override val digit: Char = '0'
}

data class One(override val code: String) : Digit {
    override val segments: List<Int> = listOf(2, 5)
    override val digit: Char = '1'
}

data class Two(override val code: String) : Digit {
    override val segments: List<Int> = listOf(0, 2, 3, 4, 6)
    override val digit: Char = '2'
}

data class Three(override val code: String) : Digit {
    override val segments: List<Int> = listOf(0, 2, 3, 5, 6)
    override val digit: Char = '3'
}

data class Four(override val code: String) : Digit {
    override val segments: List<Int> = listOf(1, 2, 3, 5)
    override val digit: Char = '4'
}

data class Five(override val code: String) : Digit {
    override val segments: List<Int> = listOf(0, 1, 3, 5, 6)
    override val digit: Char = '5'
}

data class Six(override val code: String) : Digit {
    override val segments: List<Int> = listOf(0, 1, 3, 4, 5, 6)
    override val digit: Char = '6'
}

data class Seven(override val code: String) : Digit {
    override val segments: List<Int> = listOf(0, 2, 5)
    override val digit: Char = '7'
}

data class Eight(override val code: String) : Digit {
    override val segments: List<Int> = listOf(0, 1, 2, 3, 4, 5, 6)
    override val digit: Char = '8'
}

data class Nine(override val code: String) : Digit {
    override val segments: List<Int> = listOf(0, 1, 2, 3, 5, 6)
    override val digit: Char = '9'
}

fun parseLine(line: String): Line =
    line.split("|").let { (inputStr, outputStr) ->
        Line(inputStr.split(" "), outputStr.split(" "))
    }

val permutations: List<String> = "abcdefg".toList().permutations().map { it.joinToString("") }

fun <T> List<T>.permutations(): List<List<T>> {
    val perms = mutableListOf<List<T>>()

    fun generate(k: Int, values: List<T>) {
        if (k == 1) {
            perms.add(values)
        } else {
            generate(k - 1, values)

            (0 until k).forEach { i ->
                val newValues = if (k % 2 == 1) {
                    values.swap(i, k - 1)
                } else {
                    values.swap(0, k - 1)
                }
                generate(k - 1, newValues)
            }
        }
    }
    generate(this.size, this)
    return perms
}

fun <T> List<T>.swap(x: Int, y: Int): List<T> =
    mapIndexed { i, v ->
        when (i) {
            x -> this[y]
            y -> this[x]
            else -> v
        }
    }

data class Line(val inputs: List<String>, val outputs: List<String>)

data class Segments(
    val s0: Char? = null,
    val s1: Char? = null,
    val s2: Char? = null,
    val s3: Char? = null,
    val s4: Char? = null,
    val s5: Char? = null,
    val s6: Char? = null,
    val s7: Char? = null,
    val s8: Char? = null,
    val s9: Char? = null,
) {
    val full: Boolean = (s0 != null) && (s1 != null) && (s2 != null) && (s3 != null) && (s4 != null) && (s5 != null) && (s6 != null) && (s7 != null) && (s8 != null) && (s9 != null)
}

