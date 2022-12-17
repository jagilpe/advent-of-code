package com.gilpereda.aoc2022.day15

import kotlin.math.abs

fun firstTask(input: Sequence<String>): String {
    val sensors = input.parsed()
    val beacons = sensors.map { it.beacon }.toSet()
    val start = sensors.minOfOrNull { it.x - it.distanceToBeacon - 1 }!!
    val end = sensors.maxOfOrNull { it.x + it.distanceToBeacon + 1 }!!

    // example
    //val row = 10

    // task 1
    val row = 2_000_000
    return (start..end)
        .map { Point(it, row) }
        .filter { !canHaveBeacon(it, sensors) }
        .count { it !in beacons }.toString()
}

fun secondTask(input: Sequence<String>): String {
    // example
//    val end = 20
    val sensors = input.parsed()

    // task 2
    val end = 4_000_000

    val init = Rectangle(0, 0, end, end)

    val rectangles = sensors.flatMap { it.rectangles(1) }
        .sortedBy { it.size }
        .fold(setOf(init)) { acc, rectangle ->
            acc.flatMap { it - rectangle }.toSet()
        }


//    val rectangles2 =
//        sensors.fold(setOf(init)) { acc, sensor ->
//            acc.flatMap { it - sensor.reach }.toSet()
//        }

    val total = rectangles.sumOf { it.size }
//    val total2 = rectangles2.sumOf { it.size }

    println("$total points to process")

    var start = System.currentTimeMillis()
    var processed = 0L
    var nextReport = 100_000_000L
    val reportStep = 100_000_000L

//    return rectangles
    return rectangles
        .firstNotNullOf { rectangle ->
//            println("rectangle $rectId: ${rectangle.size} points")
        try {
            rectangle.points.filter { point ->
                canHaveBeacon(point, sensors)
            }.firstOrNull()
                .also {
//                        val ellapsed = System.currentTimeMillis() - start
//                        start = System.currentTimeMillis()
                    processed += rectangle.size
//                        println("processed ${rectangle.size} points in $ellapsed")
                    if (processed > nextReport) {
                        nextReport += reportStep
                        println("$processed / $total in ${(System.currentTimeMillis() - start) / 1000}")
                    }
                }
        } catch (ex: Exception) {
            throw ex
        }
    }
        .also { println(it) }
        .distressSignal.toString()

//    return (0..end).asSequence()
//        .onEach { if (it % 500 == 0) println("${LocalDateTime.now()}: $it") }
//        .mapNotNull { y ->
//            (0..end).firstOrNull { x ->
//                canHaveBeacon(Point(x, y), sensors)
//            }?.let { Point(it, y) }
//        }.first().distressSignal.toString()

//
//    val positions = (0 .. end)
//        .flatMap { y ->
//            if (y % 100 == 0) println("row: $y")
//            (start..end)
//                .map { x -> Point(x, y) }
//                .filter { canHaveBeacon(it, sensors) }
//        }
//    assert(positions.size == 1)
//    return positions.first().distressSignal.toString()
}

private val LINE_REGEX = "Sensor at x=([-0-9]+), y=([-0-9]+): closest beacon is at x=([-0-9]+), y=([-0-9]+)".toRegex()

fun Sequence<String>.parsed(): List<Sensor> =
    map {
        LINE_REGEX.find(it)?.destructured
            ?.let { (sx, sy, bx, by) ->
                Sensor(sx.toInt(), sy.toInt(), Point(bx.toInt(), by.toInt()))
            }
            ?: throw Exception("could not parse $it")
    }.toList()

fun canHaveBeacon(point: Point, sensors: List<Sensor>): Boolean =
    sensors.all { !it.inRange(point) }


sealed interface Coordinate {
    val x: Int
    val y: Int

    fun distanceTo(other: Coordinate): Int = abs(x - other.x) + abs(y - other.y)

    val distressSignal: Long
        get() = 4_000_000L * x + y
}

data class Sensor(
    override val x: Int,
    override val y: Int,
    val beacon: Point,
) : Coordinate {
    val distanceToBeacon: Int = distanceTo(beacon)

    private val radius: Int = distanceToBeacon / 2

    fun inRange(point: Point): Boolean =
        distanceTo(point) <= distanceToBeacon

    val reach: Rectangle by lazy {
        Rectangle(
            fromX = x - radius,
            fromY = y - radius,
            toX = x + radius,
            toY = y + radius,
        )
    }

    fun rectangles(limit: Int): Sequence<Rectangle> =
        sequenceOf(Rectangle(
            fromX = x - radius,
            fromY = y - radius,
            toX = x + radius,
            toY = y + radius,
        )) + upperTriangle(limit) + bottomTriangle(limit) + leftTriangle(limit) + rightTriangle(limit)

    private fun upperTriangle(limit: Int): Sequence<Rectangle> {
        tailrec fun go(point: Point, height: Int, acc: Set<Rectangle>): Set<Rectangle> =
            when {
                height < limit -> acc
                else -> go(
                    point = Point(point.x, point.y - height/2),
                    height = height / 2,
                    acc + Rectangle(point.x - height / 2, point.y - height / 2, point.x + height / 2, point.y)
                )
            }

        return go(Point(x, y - radius), radius, emptySet()).asSequence()
    }

    private fun bottomTriangle(limit: Int): Sequence<Rectangle> {
        tailrec fun go(point: Point, height: Int, acc: Set<Rectangle>): Set<Rectangle> =
            when {
                height < limit -> acc
                else -> go(
                    point = Point(point.x, point.y + height/2),
                    height = height / 2,
                    acc + Rectangle(point.x - height / 2, point.y, point.x + height / 2, point.y + height / 2)
                )
            }

        return go(Point(x, y + radius), radius, emptySet()).asSequence()
    }

    private fun leftTriangle(limit: Int): Sequence<Rectangle>  {
        tailrec fun go(point: Point, height: Int, acc: Set<Rectangle>): Set<Rectangle> =
            when {
                height < limit -> acc
                else -> go(
                    point = Point(point.x - height / 2, point.y),
                    height = height / 2,
                    acc + Rectangle(point.x - height / 2, point.y - height / 2, point.x, point.y + height / 2)
                )
            }

        return go(Point(x - radius, y), radius, emptySet()).asSequence()
    }

    private fun rightTriangle(limit: Int): Sequence<Rectangle>  {
        tailrec fun go(point: Point, height: Int, acc: Set<Rectangle>): Set<Rectangle> =
            when {
                height < limit -> acc
                else -> go(
                    point = Point(point.x + height / 2, point.y),
                    height = height / 2,
                    acc + Rectangle(point.x, point.y - height / 2, point.x + height / 2, point.y + height / 2)
                )
            }

        return go(Point(x + radius, y), radius, emptySet()).asSequence()
    }
}

data class Point(
    override val x: Int,
    override val y: Int,
) : Coordinate

operator fun Set<Rectangle>.minus(other: Set<Rectangle>): Set<Rectangle> =
    flatMap { thisRectangle -> other.flatMap { otherRectangle -> thisRectangle - otherRectangle } }.toSet()

data class Rectangle(
    val fromX: Int,
    val fromY: Int,
    val toX: Int,
    val toY: Int,
) {
    val size: Long by lazy { (abs(toX.toLong() - fromX) + 1) * (abs(toY - fromY) + 1) }
    val points: Sequence<Point> by lazy {
        (fromX..toX).asSequence().flatMap { x ->
            (fromY..toY).asSequence().map { y -> Point(x, y) }
        }
    }

    operator fun minus(other: Rectangle): List<Rectangle> =
        when {
            // Case 17
            (other.fromX < fromX && other.toX < fromX) || (other.fromX > toX && other.toX > toX)
                    || (other.fromY < fromY && other.toY < fromY) || (other.fromY > toY && other.toY > toY) -> listOf(
                this
            )
            // Case 1
            other.fromX <= fromX && other.fromY <= fromY && other.toX >= toX && other.toY >= toY ->
                emptyList()
            // Case 2
            other.fromX <= fromX && other.fromY <= fromY && other.toX < toX && other.toY >= toY ->
                listOf(rct(other.toX + 1, fromY, toX, toY))
            // Case 3
            other.fromX > fromX && other.fromY <= fromY && other.toX >= toX && other.toY >= toY ->
                listOf(rct(fromX, fromY, other.fromX - 1, toY))
            // Case 4
            other.fromX <= fromX && other.fromY <= fromY && other.toX >= toX && other.toY < toY ->
                listOf(rct(fromX, other.toY + 1, toX, toY))
            // Case 5
            other.fromX <= fromX && other.fromY > fromY && other.toX >= toX && other.toY >= toY ->
                listOf(rct(fromX, fromY, toX, other.fromY - 1))
            // Case 6
            other.fromX > fromX && other.fromY > fromY && other.toX < toX && other.toY < toY ->
                listOf(
                    rct(fromX, fromY, toX, other.fromY - 1),
                    rct(fromX, other.fromY, other.fromX - 1, other.toY),
                    rct(other.toX + 1, other.fromY, toX, other.toY),
                    rct(fromX, other.toY + 1, toX, toY),
                )
            // Case 7
            other.fromX <= fromX && other.fromY <= fromY && other.toX < toX && other.toY < toY ->
                listOf(
                    rct(other.toX + 1, fromY, toX, other.toY),
                    rct(fromX, other.toY + 1, toX, toY),
                )
            // Case 8
            other.fromX > fromX && other.fromY <= fromY && other.toX >= toX && other.toY < toY ->
                listOf(
                    rct(fromX, fromY, other.fromX - 1, toY),
                    rct(other.fromX, other.toY + 1, toX, toY),
                )
            // Case 9
            other.fromX > fromX && other.fromY > fromY && other.toX >= toX && other.toY >= toY ->
                listOf(
                    rct(fromX, fromY, toX, other.fromY - 1),
                    rct(fromX, other.fromY, other.fromX - 1, toY),
                )
            // Case 10
            other.fromX <= fromX && other.fromY > fromY && other.toX < toX && other.toY >= toY ->
                listOf(
                    rct(fromX, fromY, toX, other.fromY - 1),
                    rct(other.toX + 1, other.fromY, toX, toY),
                )
            // Case 11
            other.fromX <= fromX && other.fromY > fromY && other.toX < toX && other.toY < toY ->
                listOf(
                    rct(fromX, fromY, toX, other.fromY - 1),
                    rct(fromX, other.toY + 1, toX, toY),
                    rct(other.toX + 1, other.fromY, toX, other.toY),
                )
            // Case 12
            other.fromX > fromX && other.fromY > fromY && other.toX >= toX && other.toY < toY ->
                listOf(
                    rct(fromX, fromY, toX, other.fromY - 1),
                    rct(fromX, other.toY + 1, toX, toY),
                    rct(fromX, other.fromY, other.fromX - 1, other.toY),
                )
            // Case 13
            other.fromX > fromX && other.fromY <= fromY && other.toX < toX && other.toY < toY ->
                listOf(
                    rct(fromX, fromY, other.fromX - 1, other.toY),
                    rct(other.toX + 1, fromY, toX, other.toY),
                    rct(fromX, other.toY + 1, toX, toY),
                )
            // Case 14
            other.fromX > fromX && other.fromY > fromY && other.toX < toX && other.toY >= toY ->
                listOf(
                    rct(fromX, fromY, toX, other.fromY - 1),
                    rct(fromX, other.fromY, other.fromX - 1, toY),
                    rct(other.toX + 1, other.fromY, toX, toY),
                )
            // Case 15
            other.fromX <= fromX && other.fromY > fromY && other.toX >= toX && other.toY < toY ->
                listOf(
                    rct(fromX, fromY, toX, other.fromY - 1),
                    rct(fromX, other.toY + 1, toX, toY),
                )
            // Case 16
            other.fromX > fromX && other.fromY <= fromY && other.toX < toX && other.toY >= toY ->
                listOf(
                    rct(fromX, fromY, other.fromX - 1, toY),
                    rct(other.toX + 1, fromY, toX, toY),
                )

            else -> TODO()
        }

    companion object {
        fun rct(fromX: Int, fromY: Int, toX: Int, toY: Int): Rectangle = Rectangle(fromX, fromY, toX, toY)
    }
}