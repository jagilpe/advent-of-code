package com.gilpereda.aoc2022.utils

import com.gilpereda.aoc2022.utils.geometry.Point

typealias TwoDimensionalMap<T> = Map<Long, Map<Long, T>>

fun <T> List<String>.toTwoDimensionalMapIndexed(transform: (x: Long, y: Long, c: Char) -> T): TwoDimensionalMap<T> =
    mapIndexed { y, line ->
        y.toLong() to line.mapIndexed { x, c ->
            x.toLong() to transform(x.toLong(), y.toLong(), c)
        }.toMap()
    }.toMap()

fun <T> List<String>.toTwoDimensionalMapIndexed(transform: (point: Point, c: Char) -> T): TwoDimensionalMap<T> =
    toTwoDimensionalMapIndexed { x, y, c -> transform(Point(x, y), c) }

fun <T> List<String>.toTwoDimensionalMap(transform: (c: Char) -> T): TwoDimensionalMap<T> =
    toTwoDimensionalMapIndexed { _, _, c -> transform(c) }

fun <T> TwoDimensionalMap<T>.dump(transform: (T) -> String): String =
    values.joinToString(
        separator = "\n",
        transform = { it.values.joinToString(separator = "") { cell -> transform(cell).toString() } }
    )

fun <A, B> TwoDimensionalMap<A>.map2DValuesIndexed(transform: (x: Long, y: Long, value: A) -> B): TwoDimensionalMap<B> =
    mapValues { (y, line) -> line.mapValues { (x, value) -> transform(x, y, value) } }

fun <A, B> TwoDimensionalMap<A>.map2DValuesIndexed(transform: (p: Point, value: A) -> B): TwoDimensionalMap<B> =
    map2DValuesIndexed { x, y, value -> transform(Point(x, y), value) }

fun <A, B> TwoDimensionalMap<A>.map2DValues(transform: (value: A) -> B): TwoDimensionalMap<B> =
    map2DValuesIndexed { _, _, value -> transform(value) }

fun <T> TwoDimensionalMap<T>.valuesToList(): List<T> =
    flatMap { (_, line) -> line.map { (_, value) -> value } }

fun <T> TwoDimensionalMap<T>.valuesToListIndexed(): List<Pair<Point, T>> =
    flatMap { (y, line) -> line.map { (x, value) -> Point(x, y) to value } }

fun <T> TwoDimensionalMap<T>.allMatch(predicate: (T) -> Boolean): Boolean =
    values.all { it.values.all { cell -> predicate(cell) } }

fun <T> TwoDimensionalMap<T>.setValue(x: Long, y: Long, value: T): TwoDimensionalMap<T> =
    map2DValuesIndexed { prevX, prevY, previous -> if (x == prevX && y == prevY) value else previous }

fun <T> TwoDimensionalMap<T>.getNullable(x: Long, y: Long): T? = get(y)?.get(x)

fun <T> TwoDimensionalMap<T>.getNotNullable(x: Long, y: Long): T = getNullable(x, y)!!

fun <T> TwoDimensionalMap<T>.getNotNullable(point: Point): T = getNotNullable(point.x, point.y)

fun TwoDimensionalMap<*>.indices(): List<Point> = (0 until height).flatMap { y -> (0 until width).map { x -> Point(x, y) } }

val <T> TwoDimensionalMap<T>.height: Long
    get() = values.size.toLong()

val <T> TwoDimensionalMap<T>.width: Long
    get() = values.first().size.toLong()

val <T> TwoDimensionalMap<T>.transpose: TwoDimensionalMap<T>
    get() = entries.fold(mapOf()) { acc, (y, row) ->
        row.entries.fold(acc) { acc2, (x, cell) ->
            acc2 + mapOf(x to ((acc2[x] ?: mapOf()) + mapOf(y to cell)))
        }
    }

fun <T, B> TwoDimensionalMap<T>.rows(transform: (Collection<T>) -> B): List<B> =
    values.map { transform(it.values) }

fun <T, B> TwoDimensionalMap<T>.columns(transform: (Collection<T>) -> B): List<B> =
    transpose.values.map { transform(it.values) }

fun <T> TwoDimensionalMap<T>.set(point: Point, newValue: T): TwoDimensionalMap<T> =
    map2DValuesIndexed { xy, value -> if (xy == point) newValue else value }