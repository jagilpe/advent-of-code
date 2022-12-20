package com.gilpereda.aoc2022.day20

import kotlin.math.absoluteValue

fun firstTask(input: Sequence<String>): String {
    val list = input
        .filter { it.isNotBlank() }
        .parsed()

    assert(list.size == list.distinct().size)

    val listSize = list.size
    return list.fold(list) { acc, value ->
        acc.move(value, listSize)
    }.result.toString()
}

fun secondTask(input: Sequence<String>): String {
    val decryptionKey = 811589153L

    val list = input
        .filter { it.isNotBlank() }
        .parsed().map { Value(it.value * decryptionKey) }

    val listSize = list.size
    val mixed = generateSequence(list) {
        list.fold(it) { acc, value ->
            acc.move(value, listSize)
        }
    }.take(11).toList()
    return mixed.last().result.toString()
}

private val List<Value>.result: Long
    get() {
        val zeroIndex = mapIndexed { i, v -> i to v }.first { (_, v) -> v.value == 0L }.first
        val thousandth = getAt(zeroIndex + 1000)
        val twoThousandth = getAt(zeroIndex + 2000)
        val threeThousandth = getAt(zeroIndex + 3000)
        return thousandth + twoThousandth + threeThousandth
    }

class Value(
    val value: Long,
) {
    override fun toString(): String = value.toString()
}

fun Sequence<String>.parsed(): List<Value> =
    map { Value(it.toLong()) }.toList()

fun List<Value>.move(value: Value, size: Int): List<Value> {
    val list = this - value
    val index = (indexOf(value) - 1 + value.value).toIndex(size - 1L)
    return list.insertAfter(value, index)
}

fun List<Value>.insertAfter(value: Value, index: Int): List<Value> =
    try {
        val start = this.slice(0..index)
        val end = this.slice(minOf(index + 1, size) until size)
        start + value + end
    } catch (ex: Exception) {
        throw ex
    }

private fun List<Value>.getAt(index: Int): Long = get(index % size).value

fun Long.toIndex(length: Long): Int =
    when {
        this in 0 until length -> this
        this >= length -> (this % length)
        else -> ((((this / length).absoluteValue + 1) * length + this) % length)
    }.toInt()
