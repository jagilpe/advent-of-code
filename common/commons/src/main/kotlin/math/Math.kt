package com.gilpereda.adventofcode.commons.math

fun List<Long>.leastCommonMultiple(): Long =
    if (isEmpty()) {
        throw IllegalArgumentException("List can not be empty")
    } else {
        drop(1).fold(first()) { acc, next -> leastCommonMultiple(acc, next) }
    }

@JvmName("lcmInt")
fun List<Int>.leastCommonMultiple(): Long =
    map { it.toLong() }.leastCommonMultiple()

fun leastCommonMultiple(one: Long, other: Long): Long =
    when {
        one == 0L && other == 0L -> 0
        else -> (one * other)/greatestCommonDivisor(one, other)
    }

fun greatestCommonDivisor(one: Long, other: Long): Long {
    var num1 = one
    var num2 = other
    while (num2 != 0L) {
        val temp = num2
        num2 = num1 % num2
        num1 = temp
    }
    return num1
}