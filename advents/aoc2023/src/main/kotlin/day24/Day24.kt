package com.gilpereda.aoc2022.day24

private const val DUMP = true

/**
 * 20926 -> too low
 */
fun firstTask(
    input: Sequence<String>,
    min: Long,
    max: Long,
): String {
    val hailstones = input.mapIndexed { index, line -> line.parsed(index) }.toList()
    tailrec fun go(next: Hailstone, rest: List<Hailstone>, acc: List<Intersection> = emptyList()): List<Intersection> =
        if (rest.isEmpty()) {
            acc
        } else {
            val newIntersections = rest.map { next.intersectionWith(it) }
//                .filter { it.matches(min, max) }
            go(rest.first, rest.drop(1), acc + newIntersections)
        }

    val intersections = go(hailstones.first, hailstones.drop(1))
    val (intersecting, parallel) = intersections.partition { it.intersection != null }
    val (matching, notMatching) = intersecting.partition { it.matches(min, max) }
//    if (DUMP && min > 10000) {
//        val output = Files.createTempFile("not_matching", ".txt")
//        File(output.toUri())
//            .writeText(notMatching
//                .filter { it.inFuture }
//                .joinToString("\n"))
//        println(output.toAbsolutePath())
//    }

    return matching.count().toString()
}

fun secondTask(input: Sequence<String>): String =
    TODO()

fun String.parsed(index: Int): Hailstone =
    split(" @ ").let { (position, velocity) ->
        Hailstone(
            name = "${index + 1}",
            position = P.from(position),
            velocity = P.from(velocity),
        )
    }

data class Intersection(
    val one: Hailstone,
    val other: Hailstone,
    val intersection: P? = null,
    val t: Double? = null,
    val u: Double? = null,
) {
    fun matches(min: Long, max: Long,): Boolean =
        intersection != null && inFuture && intersection in min .. max

    val inFuture: Boolean = t != null && u != null && t >= 0 && u >= 0

    override fun toString(): String =
        if (intersection != null)
            "one = $one, other = $other, x = ${intersection.x}, y = ${intersection.y}, t = $t, u = $u, in future = $inFuture"
        else
            "one = $one, other = $other, lines are parallel"
}

operator fun LongRange.contains(intersection: P): Boolean {
//    val longRange = first..last
    val (x, y) = intersection
    return x in this && y in this
}

data class Hailstone(
    val name: String,
    val position: P,
    val velocity: P,
) {
    fun intersectionWith(other: Hailstone): Intersection {
        val (px0, py0) = position.decomposed2D()
        val (vx0, vy0) = velocity.decomposed2D()
        val (px1, py1) = other.position.decomposed2D()
        val (vx1, vy1) = other.velocity.decomposed2D()
        val denominator1 = vx0 * vy1 - vx1 * vy0
        val denominator0 = -denominator1
        return if (denominator1 == 0.0) {
            Intersection(this, other)
        } else {
            val t = ((py1 - py0) * vx1 + (px0 - px1) * vy1) / denominator0
            val u = ((py0 - py1) * vx0 + (px1 - px0) * vy0) / denominator1
            Intersection(this, other, P((px1 + vx1 * u).toLong(), (py1 + vy1 * u).toLong(), 0), t,  u)
        }
    }

    override fun toString(): String = name
}

fun areParallelIn2D(one: Hailstone, other: Hailstone): Boolean =
    one.velocity.x - other.velocity.x == one.velocity.y - other.velocity.y

data class P(
    val x: Long,
    val y: Long,
    val z: Long,
) {
    fun decomposed2D(): Pair<Double, Double> =
        Pair(x.toDouble(), y.toDouble())

    companion object {
        fun from(string: String): P =
            string.split(", ").let { (x, y, z) ->
                P(x.trim().toLong(), y.trim().toLong(), z.trim().toLong())
            }
    }
}