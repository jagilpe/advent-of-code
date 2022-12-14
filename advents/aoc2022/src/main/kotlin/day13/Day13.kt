package com.gilpereda.aoc2022.day13

fun firstTask(input: Sequence<String>): String {
    val parsed = input.joinToString("\n").parsed()
    return parsed
        .mapIndexed { index, pair -> index to pair.inOrder}
        .filter { (_, inOrder) -> inOrder }
        .sumOf { it.first + 1 }.toString()
}

fun secondTask(input: Sequence<String>): String {
    val control1 = ItemList(SingleItem(2).asItemList)
    val control2 = ItemList(SingleItem(6).asItemList)
    val controlPackages = listOf(control1, control2,)
    val packages = (input.joinToString("\n").parsed().flatMap { it.packagesInOrder } + controlPackages).sorted()

    val indexOfControl1 = packages.indexOf(control1) + 1
    val indexOfControl2 = packages.indexOf(control2) + 1
    return (indexOfControl1 * indexOfControl2).toString()
}


fun String.parsed(): List<PackagePair> =
    split("\n\n")
        .filter { it.isNotBlank() }
        .map(::parsePair)

fun parsePair(string: String): PackagePair =
    string.split("\n")
        .let { (first, second) ->
            PackagePair(parseToken(first) as ItemList, parseToken(second) as ItemList,)
        }

fun parseToken(token: String): PackageItem? =
    if (token.startsWith('[')) {
        tokenize(token.removePrefix("[").removeSuffix("]"))
            .mapNotNull { parseToken(it) }
            .let { ItemList(it) }
    } else {
        if (token.isNotBlank()) {
            SingleItem(token.toInt())
        } else {
            null
        }
    }

fun tokenize(string: String): List<String> {
    tailrec fun go(acc: List<String>, rest: String, current: String, open: Int): List<String> =
        if (rest.isEmpty()) {
            acc + current
        } else {
            when (val head = rest.first()) {
                '[' -> go(acc, rest.drop(1), current + head, open + 1)
                ']' -> go(acc, rest.drop(1), current + head, open - 1)
                ',' -> if (open == 0) {
                    go(acc + current, rest.drop(1), "", 0)
                } else {
                    go(acc, rest.drop(1), current + head, open)
                }
                else -> go(acc, rest.drop(1), current + head, open)
            }
        }
    return go(listOf(), string, "", 0)
}


data class PackagePair(
    val first: ItemList,
    val second: ItemList,
) {
    val inOrder: Boolean
        get() = first < second

    val packagesInOrder: List<ItemList> =
        if (inOrder) listOf(first, second) else listOf(second, first)
}

sealed interface PackageItem : Comparable<PackageItem>

data class ItemList(
    val list: List<PackageItem> = emptyList(),
) : PackageItem {
    constructor(item: PackageItem) : this(listOf(item))

    override fun compareTo(other: PackageItem): Int =
        when (other) {
            is SingleItem -> compareTo(other.asItemList)
            is ItemList -> list.zip(other.list)
                .map { (one, other) -> one.compareTo(other) }
                .firstOrNull { it != 0 } ?: (list.size - other.list.size)
        }
}

data class SingleItem(
    val item: Int,
) : PackageItem {
    override fun compareTo(other: PackageItem): Int =
        when (other) {
            is SingleItem -> item.compareTo(other.item)
            is ItemList -> asItemList.compareTo(other)
        }

    val asItemList: ItemList
        get() = ItemList(listOf(this))
}

