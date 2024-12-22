package com.gilpereda.aoc2024.day21

import com.gilpereda.adventofcode.commons.geometry.Orientation
import com.gilpereda.adventofcode.commons.geometry.Point

fun firstTask(input: Sequence<String>): String = input.solve(1).toString()

fun secondTask(input: Sequence<String>): String = input.solve(24).toString()

private fun Sequence<String>.solve(levels: Int): Long {
    val cache = StepCache()
    val robot = List(levels) { it }.fold(Robot(cache = cache)) { robot, _ -> Robot(cache, robot) }
    return map { Code(it, robot) }
        .sumOf { it.result() }
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

        val paths = numericPadMoves.map { robot.resolve(it) }

        return paths.minOf { it }.toLong()
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
    private val cache: StepCache,
    private val delegate: Robot? = null,
) {
    private val level: Int by lazy { if (delegate != null) delegate.level + 1 else 0 }

    fun resolve(instructions: List<Button>): Long =
        instructions
            .withInitialButton()
            .windowed(2)
            .map(::Step)
            .sumOf { resolveHigherLevelStep(it) }

    private fun List<Button>.withInitialButton(): List<Button> = listOf(ActivateButton, *toTypedArray())

    private fun resolveHigherLevelStep(step: Step): Long {
        val cached = cache[step, level]
        return if (cached != null) {
            cached
        } else {
            // I get the buttons to press by this robot
            val steps = resolveDirectionalKeys(step)
            val cost =
                if (delegate != null) {
                    steps.minOf { delegate.resolve(it) }
                } else {
                    steps.minOf { it.size }.toLong()
                }
            cache[step, level] = cost
            cost
        }
    }

    private fun resolveDirectionalKeys(step: Step): List<List<Button>> {
        val cached = cache.getButtons(step)
        return if (cached != null) {
            cached
        } else {
            val fromPoint = DirectionalKeyPad.pointFor(step.from)
            val toPoint = DirectionalKeyPad.pointFor(step.to)
            val buttonsList =
                (
                    fromPoint
                        .movementsTo(toPoint, DirectionalKeyPad::isValid)
                        .ifEmpty { listOf(emptyList()) }
                ).map { path -> path.map { Button.from(it) } + ActivateButton }.toList()
            cache.setButtons(step, buttonsList)
            buttonsList
        }
    }
}

data class Step(
    val from: Button,
    val to: Button,
) {
    constructor(list: List<Button>) : this(list[0], list[1])
}

class StepCache {
    private val cache = mutableMapOf<Entry, Long>()

    private val buttonsCache = mutableMapOf<Step, List<List<Button>>>()

    operator fun get(
        step: Step,
        level: Int,
    ): Long? = cache[Entry(step, level)]

    operator fun set(
        step: Step,
        level: Int,
        value: Long,
    ) {
        cache[Entry(step, level)] = value
    }

    fun getButtons(step: Step): List<List<Button>>? = buttonsCache[step]

    fun setButtons(
        step: Step,
        value: List<List<Button>>,
    ) {
        buttonsCache[step] = value
    }

    data class Entry(
        val step: Step,
        val level: Int,
    )
}

sealed interface Button {
    companion object {
        private val buttons: Map<Orientation, DirectionalButton> = Orientation.entries.associateWith { DirectionalButton(it) }

        fun from(orientation: Orientation): Button = buttons[orientation]!!

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
