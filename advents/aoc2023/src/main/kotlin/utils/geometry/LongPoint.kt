package com.gilpereda.aoc2022.utils.geometry


data class LongPoint3D(
    val x: Long,
    val y: Long,
    val z: Long,
) {
    fun decomposed2D(): Pair<Double, Double> =
        Pair(x.toDouble(), y.toDouble())

    operator fun minus(other: LongPoint3D): LongPoint3D =
        copy(x = x - other.x, y = y - other.y, z = z - other.z)

    operator fun plus(other: LongPoint3D): LongPoint3D =
        copy(x = x + other.x, y = y + other.y, z = z + other.z)

    companion object {
        fun from(string: String): LongPoint3D =
            string.split(", ").let { (x, y, z) ->
                LongPoint3D(x.trim().toLong(), y.trim().toLong(), z.trim().toLong())
            }
    }
}

data class LongPoint(
    val x: Long,
    val y: Long,
) {

    operator fun plus(other: LongPoint): LongPoint =
        LongPoint(x + other.x, y + other.y)

    operator fun unaryMinus(): LongPoint = LongPoint(-x, -y)
}