package com.gilpereda.aoc2022.day23

import com.gilpereda.aoc2022.day23.Movement.*

fun firstTask(input: Sequence<String>): String =
    generateSequence(input.parsed()) {
        it.next
    }
        .take(11).last().emptyCells.toString()


fun secondTask(input: Sequence<String>): String =
    generateSequence(2 to input.parsed()) { (round, field) ->
        (round + 1) to field.next
    }.takeWhile { !it.second.noElfHasToMove }.last().first.toString()

fun Sequence<String>.parsed(): Field =
    filter { it.isNotBlank() }
        .flatMapIndexed { y, line ->
            line.mapIndexed { x, cell -> Point(x, y) to cell }
        }.fold(mapOf<Int, Map<Int, Elf>>()) { acc, (point, cell) ->
            when (cell) {
                '#' -> acc.add(Elf(x = point.x, y = point.y))
                else -> acc
            }
        }.let(::Field)

typealias Plan = Map<Point, List<Step>>

fun Map<Int, Map<Int, Elf>>.addAll(points: Iterable<Elf>): Map<Int, Map<Int, Elf>> =
    points.fold(this) { acc, elf -> acc.add(elf) }

fun Map<Int, Map<Int, Elf>>.add(elf: Elf): Map<Int, Map<Int, Elf>> =
    this + mapOf(elf.y to (getOrDefault(elf.y, mapOf()) + mapOf(elf.x to elf)))

data class Field(
    val map: Map<Int, Map<Int, Elf>>,
    val movements: List<Movement> = listOf(NORTH, SOUTH, WEST, EAST)
) {
    private val maxY = map.keys.max()
    private val minY = map.keys.min()
    private val maxX = map.flatMap { (_, value) -> value.keys }.max()
    private val minX = map.flatMap { (_, value) -> value.keys }.min()

    val emptyCells: Int by lazy {
        (maxX - minX + 1) * (maxY - minY + 1) - elves.size
    }

    val next: Field
        get() = plan.entries.fold(mapOf<Int, Map<Int, Elf>>()) { acc, (_, step) ->
            if (step.size > 1) {
                acc.addAll(step.map { it.old })
            } else {
                acc.add(step.first().new)
            }
        }.let(::Field).copy(movements = movements.drop(1) + movements.first())

    val noElfHasToMove: Boolean by lazy { elves.all { it.doNotMove != null } }

    private val plan: Plan by lazy {
        elves.map { Step(it, it.next) }.groupBy { it.new.point }
    }

    private val Elf.next: Elf
        get() =
            doNotMove
                ?: movements.firstNotNullOfOrNull {
                    when (it) {
                        NORTH -> goNorth
                        SOUTH -> goSouth
                        EAST -> goEast
                        WEST -> goWest
                    }
                }
                ?: this

    private val Elf.doNotMove: Elf?
        get() = if (suroundingPoints.all { it.isFree }) this else null

    private val Elf.goNorth: Elf?
        get() = if (northPoints.all { it.isFree }) copy(y = y - 1) else null

    private val Elf.goSouth: Elf?
        get() = if (southPoints.all { it.isFree }) copy(y = y + 1) else null

    private val Elf.goEast: Elf?
        get() = if (eastPoints.all { it.isFree }) copy(x = x + 1) else null

    private val Elf.goWest: Elf?
        get() = if (westPoints.all { it.isFree }) copy(x = x - 1) else null

    private val Point.isFree: Boolean
        get() = map.getOrDefault(y, mapOf())[x] == null

    private val elves: List<Elf> by lazy { map.values.flatMap { it.values } }


    override fun toString(): String =
        (minY..maxY).joinToString("\n") { y ->
            (minX..maxX).joinToString("") { x ->
                if (map.getOrDefault(y, emptyMap())[x] != null) "#" else "."
            }
        }
}

data class Step(
    val old: Elf,
    val new: Elf,
)

data class Point(val x: Int, val y: Int)

data class Elf(
    val x: Int,
    val y: Int,
) {
    val point: Point = Point(x, y)

    val northPoints: List<Point> by lazy {
        listOf(Point(x - 1, y - 1), Point(x, y - 1), Point(x + 1, y - 1))
    }

    val southPoints: List<Point> by lazy {
        listOf(Point(x - 1, y + 1), Point(x, y + 1), Point(x + 1, y + 1))
    }

    val eastPoints: List<Point> by lazy {
        listOf(Point(x + 1, y - 1), Point(x + 1, y), Point(x + 1, y + 1))
    }

    val westPoints: List<Point> by lazy {
        listOf(Point(x - 1, y - 1), Point(x - 1, y), Point(x - 1, y + 1))
    }

    val suroundingPoints: List<Point> by lazy {
        northPoints + southPoints + eastPoints + westPoints
    }
}

enum class Movement {
    NORTH,
    SOUTH,
    EAST,
    WEST,
}