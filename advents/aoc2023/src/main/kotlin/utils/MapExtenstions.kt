package com.gilpereda.aoc2022.utils

typealias TwoDimensionalMap<T> = Map<Int, Map<Int, T>>

fun <T> TwoDimensionalMap<T>.dump(transform: (T) -> Char): String =
    values.joinToString(
        separator = "\n",
        transform = { it.values.joinToString(separator = "") { cell -> transform(cell).toString() } }
    )

fun <A, B> TwoDimensionalMap<A>.map2DValues(transform: (x: Int, y: Int, value: A) -> B): TwoDimensionalMap<B> =
    mapValues { (y, line) -> line.mapValues { (x, value) -> transform(x, y, value) } }

fun <T> TwoDimensionalMap<T>.allMatch(predicate: (T) -> Boolean): Boolean =
    values.all { it.values.all { cell -> predicate(cell) } }

fun <T> TwoDimensionalMap<T>.setValue(x: Int, y: Int, value: T): TwoDimensionalMap<T> =
    map2DValues { prevX, prevY, previous -> if (x == prevX && y == prevY) value else previous }

fun <T> TwoDimensionalMap<T>.getNullable(x: Int, y: Int): T? = get(y)?.get(x)

fun <T> TwoDimensionalMap<T>.getNotNullable(x: Int, y: Int): T = getNullable(x, y)!!