package com.gilpereda.aoc2022.utils.math

fun List<Long>.leastCommonMultiple(): Long =
    if (isEmpty()) {
        throw IllegalArgumentException("List can not be empty")
    } else {
        drop(1).fold(first()) { acc, next -> lcm(acc, next) }
    }

@JvmName("lcmInt")
fun List<Int>.leastCommonMultiple(): Long =
    map { it.toLong() }.leastCommonMultiple()

fun lcm(one: Long, other: Long): Long =
    when {
        one == 0L && other == 0L -> 0
        else -> (one * other)/gcd(one, other)
    }


fun gcd(one: Long, other: Long): Long {
    var num1 = one
    var num2 = other
    while (num2 != 0L) {
        val temp = num2
        num2 = num1 % num2
        num1 = temp
    }
    return num1
}