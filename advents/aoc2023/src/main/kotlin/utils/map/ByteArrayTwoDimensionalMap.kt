@file:OptIn(ExperimentalUnsignedTypes::class)

package com.gilpereda.aoc2022.utils.map

import com.gilpereda.aoc2022.utils.geometry.Point

typealias IntTwoDimensionalMap = ByteArrayTwoDimensionalMap<Int>

@OptIn(ExperimentalUnsignedTypes::class)
class ByteArrayTwoDimensionalMap<T>(
    private val toValue: UByte.() -> T,
    private val fromValue: T.() -> UByte,
    private val internalMap: Array<UByteArray>
) {
    val width: Int = internalMap.first().size
    val height: Int = internalMap.size

    fun dump(transform: (Point, T) -> String): String =
        internalMap.mapIndexed { y, line -> line.mapIndexed { x, cell -> transform(Point.from(x, y), toValue(cell)) } }
            .joinToString("\n") { line -> line.joinToString("") }

    /**
     * Will replace the value and return true if there was no value or the existing one was different
     */
    fun replace(point: Point, value: T): Boolean {
        val newCell = value.fromValue()
        return if (getByte(point) == newCell) {
            false
        } else {
            setByte(point, newCell)
            true
        }
    }

    fun withinMap(point: Point): Boolean =
        point.withinLimits(0 until width, 0 until height)

    operator fun set(point: Point, value: T) {
        setByte(point, value.fromValue())
    }

    operator fun get(point: Point): T =
        getByte(point).toValue()

    private fun getByte(point: Point): UByte =
        internalMap[point.y][point.x]

    private fun setByte(point: Point, byte: UByte) {
        internalMap[point.y][point.x] = byte
    }

    fun values(): List<T> = internalMap.flatMap { line -> line.map(toValue) }

    fun count(predicate: (T) -> Boolean): Int =
        values().count(predicate)
}

@OptIn(ExperimentalUnsignedTypes::class)
fun <T> List<String>.parseToByteArrayMap(
    parse: (Char) -> T,
    toValue: UByte.() -> T,
    fromValue: T.() -> UByte,
) =
    ByteArrayTwoDimensionalMap(
        toValue = toValue,
        fromValue = fromValue,
        internalMap = map { line -> line.map { parse(it).fromValue() }.toUByteArray() }.toTypedArray()
    )

fun List<String>.parseToIntArrayMap(): IntTwoDimensionalMap = parseToByteArrayMap<Int>(
    toValue = { toInt() },
    fromValue = { toUByte() },
    parse = { it.digitToInt() }
)