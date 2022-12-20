package com.gilpereda.aoc2022.day20

fun firstTask(input: Sequence<String>): String {
    val list = input.parsed()

    list.foldIndexed(list) { index, acc, num ->
        when {
            num == 0 -> acc
            num > 0 -> {
                val newIndex = (index + num) % acc.size
                when {
                    newIndex == index -> acc
                    newIndex < index -> acc.subList(0, newIndex) + num + acc.subList(newIndex, index) + acc.subList(index + 1, acc.size - 1)
                    else -> acc.subList(0, index) + acc.subList(index, newIndex) + num + acc.subList(newIndex + 1, acc.size - 1)
                }
            }
            else -> {
                val newIndex = (index - num)
            }
        }
    }
}

fun secondTask(input: Sequence<String>): String = TODO()

fun Sequence<String>.parsed(): List<Int> =
    map { it.toInt() }.toList()

