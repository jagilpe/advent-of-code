package com.gilpereda.aoc2024.day21

import com.gilpereda.adventofcode.commons.geometry.Orientation
import com.gilpereda.adventofcode.commons.geometry.Point

fun firstTask(input: Sequence<String>): String {
    val robot = List(1) { it }.fold(Robot()) { robot, _ -> Robot(robot) }
    return input
        .map { Code(it, robot) }
        .sumOf { it.result() }
        .toString()
}

fun secondTask(input: Sequence<String>): String {
    val robot = List(23) { it }.fold(Robot()) { robot, _ -> Robot(robot) }
    var counter = 0
    return input
        .map { Code(it, robot) }
        .sumOf {
            println("Counter: ${counter++}")
            it.result()
        }.toString()
}

object NumericPad {
    private val numericPad =
        mapOf(
            '7' to Point.from(0, 0),
            '8' to Point.from(1, 0),
            '9' to Point.from(2, 0),
            '4' to Point.from(0, 1),
            '5' to Point.from(1, 1),
            '6' to Point.from(2, 1),
            '1' to Point.from(0, 2),
            '2' to Point.from(1, 2),
            '3' to Point.from(2, 2),
            '0' to Point.from(1, 3),
            'A' to Point.from(2, 3),
        )

    private val invertedPad =
        mapOf(
            Point.from(0, 0) to '7',
            Point.from(1, 0) to '8',
            Point.from(2, 0) to '9',
            Point.from(0, 1) to '4',
            Point.from(1, 1) to '5',
            Point.from(2, 1) to '6',
            Point.from(0, 2) to '1',
            Point.from(1, 2) to '2',
            Point.from(2, 2) to '3',
            Point.from(1, 3) to '0',
            Point.from(2, 3) to 'A',
        )

    fun pointFor(char: Char): Point = numericPad.getValue(char)

    fun charForPoint(point: Point): Char = invertedPad.getValue(point)

    private val numericKeyPadPositions = numericPad.values.toSet()

    fun isValid(point: Point): Boolean = point in numericKeyPadPositions
}

object DirectionalKeyPad {
    private val directionalKeyPad =
        mapOf(
            Button.from(Orientation.NORTH) to Point.from(1, 0),
            ActivateButton to Point.from(2, 0),
            Button.from(Orientation.WEST) to Point.from(0, 1),
            Button.from(Orientation.SOUTH) to Point.from(1, 1),
            Button.from(Orientation.EAST) to Point.from(2, 1),
        )

    private val invertedPad =
        mapOf(
            Point.from(1, 0) to Button.from(Orientation.NORTH),
            Point.from(2, 0) to ActivateButton,
            Point.from(0, 1) to Button.from(Orientation.WEST),
            Point.from(1, 1) to Button.from(Orientation.SOUTH),
            Point.from(2, 1) to Button.from(Orientation.EAST),
        )

    fun pointFor(button: Button): Point = directionalKeyPad.getValue(button)

    fun buttonForPoint(point: Point): Button = invertedPad.getValue(point)

    private val directionalKeyPadPositions = directionalKeyPad.values.toSet()

    fun isValid(point: Point): Boolean = point in directionalKeyPadPositions
}

class Code(
    private val code: String,
    private val robot: Robot,
) {
    fun result(): Long {
        val complexity = complexity()
        val number = code.dropLast(1).toLong()

        return number * complexity
    }

    private fun complexity(): Long {
        val numericPadMoves =
            findPathsInNumericPad(code)
//                .filter { dump(it) == "<A^A>^^AvvvA" }
//                .filter { dump(it) == "<A^A^>^AvvvA" }
//                .filter { dump(it) == "<A^A^^>AvvvA" }

        var counter = 0

        val firstLevelCache = RealCache()

        val paths = numericPadMoves.flatMap { robot.resolve(it.toActivatedSteps(), firstLevelCache) }

        val outputs = numericPadMoves.map { path -> revertNumericPad(path) }

        return paths.minOf { it.size }.toLong() ?: 0L
    }

    private fun findPathsInNumericPad(code: String): List<List<Button>> {
        tailrec fun go(
            open: List<List<Char>>,
            acc: List<List<Button>>,
        ): List<List<Button>> =
            if (open.isEmpty()) {
                acc
            } else {
                val next = open.first()
                val newMoves = findPathInNumericPad(next)
                val newAcc = acc.flatMap { path -> newMoves.map { path + it } }
                go(open.drop(1), newAcc)
            }
        return go("A$code".toList().windowed(2), listOf(emptyList()))
    }

    private fun findPathInNumericPad(pair: List<Char>): List<List<Button>> =
        pair.let { (from, to) ->
            val fromPoint = NumericPad.pointFor(from)
            val toPoint = NumericPad.pointFor(to)
            fromPoint.movementsTo(toPoint, NumericPad::isValid).map { path -> path.map { Button.from(it) } + ActivateButton }
        }

    private fun dump(instructions: List<Button>): String = instructions.joinToString("")
}

fun revertNumericPad(steps: List<Button>): String =
    steps
        .fold('A' to "") { (current, acc), next ->
            when (next) {
                is DirectionalButton ->
                    NumericPad.charForPoint(NumericPad.pointFor(current).move(next.orientation)) to acc
                is ActivateButton -> current to acc + current
            }
        }.second

fun revertDirectionPad(steps: List<Button>): List<Button> =
    steps
        .fold(ActivateButton as Button to (listOf<Button>())) { (current, acc), next ->
            when (next) {
                is DirectionalButton ->
                    DirectionalKeyPad.buttonForPoint(DirectionalKeyPad.pointFor(current).move(next.orientation)) to acc
                is ActivateButton -> current to acc + current
            }
        }.second

class Robot(
    private val delegate: Robot? = null,
) {
    private val localCache: Cache = RealCache()

    fun resolve(
        instructions: List<Step>,
        parentCache: Cache = NoopCache,
    ): List<List<Button>> {
        val findPathsInDirectionalPad = findPathsInDirectionalPad(instructions, parentCache)
        return if (delegate != null) {
            findPathsInDirectionalPad.flatMap {
                delegate
                    .resolve(it.toActivatedSteps(), localCache)
            }
        } else {
            findPathsInDirectionalPad
        }
    }

    private fun findPathsInDirectionalPad(
        steps: List<Step>,
        parentCache: Cache,
    ): List<List<Button>> {
        tailrec fun go(
            open: List<Step>,
            acc: List<List<Button>>,
        ): List<List<Button>> =
            if (open.isEmpty()) {
                acc
            } else {
                val step = open.first()
                val newMoves =
                    parentCache[step]
                        ?: findPathInDirectionalPad(step)
                            .ifEmpty { listOf(emptyList()) }
                            .also { parentCache[step] = it }
                val newAcc = acc.flatMap { path -> newMoves.map { path + it } }
                go(open.drop(1), newAcc)
            }
        return go(steps, listOf(emptyList()))
    }

    private fun findPathInDirectionalPad(step: Step): List<List<Button>> {
        val fromPoint = DirectionalKeyPad.pointFor(step.from)
        val fromTo = DirectionalKeyPad.pointFor(step.to)
        return (
            fromPoint
                .movementsTo(fromTo, DirectionalKeyPad::isValid)
                .ifEmpty { listOf(emptyList()) }
        ).map { path -> path.map { Button.from(it) } + ActivateButton }
    }
}

private fun List<Button>.toActivatedSteps(): List<Step> = listOf(ActivateButton, *toTypedArray()).windowed(2).map(::Step)

data class Step(
    val from: Button,
    val to: Button,
) {
    constructor(list: List<Button>) : this(list[0], list[1])
}

interface Cache {
    operator fun get(step: Step): List<List<Button>>?

    operator fun set(
        step: Step,
        value: List<List<Button>>,
    )
}

object NoopCache : Cache {
    override fun get(step: Step): List<List<Button>>? = null

    override fun set(
        step: Step,
        value: List<List<Button>>,
    ) {
        // Do nothing
    }
}

class RealCache : Cache {
    private val cache = mutableMapOf<Step, List<List<Button>>>()

    override operator fun get(step: Step): List<List<Button>>? = cache[step]

    override operator fun set(
        step: Step,
        value: List<List<Button>>,
    ) {
        cache[step] = value
    }
}

sealed interface Button {
    companion object {
        private val buttons = mutableMapOf<Orientation, Button>()

        fun from(orientation: Orientation): Button = buttons.computeIfAbsent(orientation) { DirectionalButton(it) }

        fun from(char: Char): Button =
            when (char) {
                '<' -> from(Orientation.WEST)
                '>' -> from(Orientation.EAST)
                '^' -> from(Orientation.NORTH)
                'v' -> from(Orientation.SOUTH)
                else -> ActivateButton
            }
    }
}

data class DirectionalButton(
    val orientation: Orientation,
) : Button {
    override fun toString(): String = orientation.toString()
}

data object ActivateButton : Button {
    override fun toString(): String = "A"
}
