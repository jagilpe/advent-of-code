package com.gilpereda.adventofcode.commons.geometry

class Point3D private constructor(
    val x: Int,
    val y: Int,
    val z: Int,
) {
    fun isLowerThan(other: Point3D): Int = z.compareTo(other.z)

    fun to2d(projection: Axis): Point {
        val (one, other) = projection.others
        return Point.from(axis(one), axis(other))
    }

    fun axis(axis: Axis): Int =
        when (axis) {
            Axis.X -> x
            Axis.Y -> y
            Axis.Z -> z
        }

    fun xy(): Point = Point.from(x, y)

    fun move(
        axis: Axis,
        steps: Int,
    ): Point3D =
        Point3D(
            x = if (axis == Axis.X) x + steps else x,
            y = if (axis == Axis.Y) y + steps else y,
            z = if (axis == Axis.Z) z + steps else z,
        )

    override fun toString(): String =
        """
        (x=$x, y=$y, z=$z)
        """.trimIndent()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Point3D

        if (x != other.x) return false
        if (y != other.y) return false
        if (z != other.z) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        result = 31 * result + z
        return result
    }

    companion object {
        fun from(
            x: Int,
            y: Int,
            z: Int,
        ): Point3D = Point3D(x, y, z)

        fun from(string: String): Point3D {
            val (x, y, z) = string.split(",")
            return Point3D(x.toInt(), y.toInt(), z.toInt())
        }

        object CompareX : Comparator<Point3D> {
            override fun compare(
                one: Point3D,
                other: Point3D,
            ): Int = one.z.compareTo(other.z)
        }

        object CompareY : Comparator<Point3D> {
            override fun compare(
                one: Point3D,
                other: Point3D,
            ): Int = one.z.compareTo(other.z)
        }

        object CompareZ : Comparator<Point3D> {
            override fun compare(
                one: Point3D,
                other: Point3D,
            ): Int = one.z.compareTo(other.z)
        }
    }
}

enum class Axis {
    X,
    Y,
    Z,
    ;

    val others: Pair<Axis, Axis> by lazy {
        when (this) {
            X -> Y to Z
            Y -> X to Z
            Z -> X to Y
        }
    }
}
