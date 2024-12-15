package com.gilpereda.aoc2024.day15

import com.gilpereda.adventofcode.commons.geometry.Orientation
import com.gilpereda.adventofcode.commons.geometry.Point
import com.gilpereda.adventofcode.commons.map.TypedTwoDimensionalMap

fun firstTask(input: Sequence<String>): String = resolve(input.toList().joinToString("\n"))

fun secondTask(input: Sequence<String>): String =
    resolve(
        input
            .toList()
            .joinToString("\n")
            .replace("#", "##")
            .replace(".", "..")
            .replace("O", "[]")
            .replace("@", "@."),
    )

private fun resolve(input: String): String {
    val (mapString, commandsString) = input.split("\n\n")
    val initialGame = parseGame(mapString)
    val commands =
        commandsString
            .split("\n")
            .joinToString("")
            .map {
                when (it) {
                    '<' -> Orientation.WEST
                    '>' -> Orientation.EAST
                    '^' -> Orientation.NORTH
                    else -> Orientation.SOUTH
                }
            }

    return commands.fold(initialGame) { game, orientation -> game.move(orientation) }.result().toString()
}

fun parseGame(string: String): Game {
    val stringList = string.split("\n")
    val width = stringList.first().length
    val height = stringList.size
    return Game(width = width, height = height)
        .apply {
            stringList.forEachIndexed { y, line ->
                line.forEachIndexed { x, c ->
                    when (c) {
                        '@' -> {
                            robot = Point.from(x, y)
                        }
                        '#' -> addWall(x, y)
                        'O' -> addSingleCellBox(x, y)
                        '[' -> addDoubleCellBox(x, y)
                    }
                }
            }
        }
}

class Game(
    val height: Int,
    val width: Int,
) {
    private val map: TypedTwoDimensionalMap<Byte> by lazy {
        TypedTwoDimensionalMap.from(0.toByte(), width, height)
    }
    lateinit var robot: Point
    private val boxes: MutableSet<Box> = mutableSetOf()
    private val walls: MutableSet<Point> = mutableSetOf()

    override fun toString(): String =
        map.dumpWithIndex { point, _ ->
            when {
                point == robot -> "@"
                point.isWall() -> "#"
                else -> boxes.firstNotNullOfOrNull { it[point] } ?: "."
            }
        }

    private fun Point.isWall() = this in walls

    fun addWall(
        x: Int,
        y: Int,
    ) {
        walls.add(Point.from(x, y))
    }

    fun addSingleCellBox(
        x: Int,
        y: Int,
    ) {
        boxes.add(SCBox(Point.from(x, y)))
    }

    fun addDoubleCellBox(
        x: Int,
        y: Int,
    ) {
        boxes.add(DCBox(Point.from(x, y)))
    }

    fun move(orientation: Orientation): Game {
        val destination = robot.move(orientation)
        if (robot.canMove(orientation)) {
            robot = destination
            getBox(destination)?.move(orientation)
        }
        return this
    }

    private fun Point.canMove(orientation: Orientation): Boolean {
        val destination = move(orientation)
        return when {
            destination.isWall() -> false
            else -> getBox(destination)?.canMove(orientation) ?: true
        }
    }

    fun result(): Int = boxes.sumOf { (point) -> point.y * 100 + point.x }

    private fun getBox(point: Point): Box? = boxes.firstOrNull { point in it }

    sealed class Box(
        val position: Point,
    ) {
        abstract operator fun contains(point: Point): Boolean

        operator fun component1(): Point = position

        abstract operator fun get(point: Point): String?

        abstract fun canMove(orientation: Orientation): Boolean

        abstract fun move(orientation: Orientation)

        override fun toString(): String = "${this::class.simpleName}($position)"

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Box
            return position == other.position
        }

        override fun hashCode(): Int {
            var result = this::class.simpleName.hashCode()
            result = 31 * result + position.hashCode()
            return result
        }
    }

    inner class SCBox(
        position: Point,
    ) : Box(position) {
        override fun contains(point: Point): Boolean = position == point

        override fun get(point: Point): String? = if (point == position) "O" else null

        override fun canMove(orientation: Orientation): Boolean = position.canMove(orientation)

        override fun move(orientation: Orientation) {
            val destination = position.move(orientation)
            getBox(destination)?.move(orientation)
            boxes.remove(this)
            boxes.add(SCBox(destination))
        }
    }

    inner class DCBox(
        position: Point,
    ) : Box(position) {
        private val positionRight: Point = position.move(Orientation.EAST)

        override fun contains(point: Point): Boolean = position == point || positionRight == point

        override fun get(point: Point): String? =
            when (point) {
                position -> "["
                positionRight -> "]"
                else -> null
            }

        override fun canMove(orientation: Orientation): Boolean =
            when (orientation) {
                Orientation.NORTH, Orientation.SOUTH -> position.canMove(orientation) && positionRight.canMove(orientation)
                Orientation.EAST -> positionRight.canMove(orientation)
                Orientation.WEST -> position.canMove(orientation)
            }

        override fun move(orientation: Orientation) {
            val destination = position.move(orientation)
            val destinationRight = positionRight.move(orientation)
            setOfNotNull(
                getBox(destination),
                getBox(destinationRight),
            ).filter { it != this }.forEach { it.move(orientation) }
            boxes.remove(this)
            boxes.add(DCBox(destination))
        }
    }
}
