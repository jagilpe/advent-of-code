package com.gilpereda.aoc2022.day17

fun firstTask(input: Sequence<String>): String {
    val movements: List<Movement> = input.first().parsed()

    val last = generateSequence(Game(movements = movements)) {
        it.next()
    }
        .takeWhile { it.rocksPositions.size < 2023 }.last()
    return last.towerHeight.toString()
}

fun secondTask(input: Sequence<String>): String = TODO()

fun String.parsed(): List<Movement> =
    map {
        when (it) {
            '<' -> Left
            '>' -> Right
            else -> throw Exception("Unknown direction")
        }
    }

fun List<RockPosition>.dump(header: String? = null, lines: Int? = null): String {
    val pointsInMovement = filter { !it.stopped }.flatMap { it.points }
    val stoppedPoints = filter { it.stopped }.flatMap { it.points }
    val highestY = maxOfOrNull(RockPosition::nextY) ?: 4

    val toLine = maxOf(lines?.let { highestY - lines } ?: 0, ) - 1
    return (highestY downTo toLine)
        .joinToString(separator = "\n", prefix = header?.let { "$it\n" } ?: "") { y ->
            if (y > -1) {
                (0..6).joinToString(separator = "", prefix = "|", postfix = "|") { x ->
                    when {
                        Point(x, y) in pointsInMovement -> "@"
                        Point(x, y) in stoppedPoints -> "#"
                        else -> "."
                    }
                }
            } else {
                "\n+-------+\n"
            }
        }
            .also(::println)
}

data class Game(
    val movements: List<Movement>,
    val nextMovements: List<Movement> = movements,
    val rocksOrder: List<Rock> = Rock.orderedRocks,
    val nextRocks: List<Rock> = rocksOrder,
    val rocksPositions: List<RockPosition> = emptyList(),
    val boardWidth: Int = 7,
) {
//    override fun toString(): String = """Game(
//        |nextMovements: $nextMovements
//        |nextRocks: $nextRocks
//        |rocksPositions: $rocksPositions
//        |)
//    """.trimMargin()

    fun dump(lines: Int = 20): String {
        return rocksPositions.dump(lines = lines).also { println("\n\n") }
    }

    override fun toString(): String = rocksPositions.dump()

    val towerHeight: Int by lazy { firstFreeLine }

    private val firstFreeLine: Int by lazy {
        rocksPositions.maxOfOrNull(RockPosition::nextY) ?: 0
    }

    private val blockingElements: Set<RockPosition> by lazy {
//        (0..6).mapNotNull(::highestBlockingForColumn).toSet()
        rocksPositions.filter { it.stopped }.toSet()
    }

    private fun highestBlockingForColumn(column: Int): RockPosition? =
        rocksPositions.filter(RockPosition::stopped)
            .map { it to it.highestYAtX(column) }
            .filter { it.second >= 0 }
            .maxByOrNull { it.second }?.first

    private fun RockPosition.highestYAtX(x: Int): Int =
        point.y - 1 + rock.heightAt(x - point.x)

    fun next(): Game =
        if (canDrop) {
            val movedRock = rocksPositions.last()
                .push(nextMovements.first()).drop()
            copy(
                nextMovements = nextMovements.drop(1).ifEmpty { movements },
                rocksPositions = rocksPositions.dropLast(1) + movedRock,
            )
        } else {
            copy(
                nextMovements = nextMovements,
                nextRocks = nextRocks.drop(1).ifEmpty { rocksOrder },
                rocksPositions = rocksPositions + nextRocks.first().pos(firstFreeLine + 3)
            )
        }

    private fun RockPosition.push(movement: Movement): RockPosition =
        when (movement) {
            is Left -> copy(point = point.copy(x = point.x - 1))
            is Right -> copy(point = point.copy(x = point.x + 1))
        }
            .let { if (it.valid) it else this }


    private fun RockPosition.drop(): RockPosition =
        copy(point = point.copy(y = maxOf(point.y - 1, 0)), stopped = point.y == 0)
            .let { if (it.valid) it else copy(stopped = true) }

    private val RockPosition.valid: Boolean
        get() = (point.x in 0..(boardWidth - rock.width)) && blockingElements.none { it.overlaps(this) }

    private val canDrop: Boolean
        get() = !(rocksPositions.lastOrNull()?.stopped ?: true)
}

data class RockPosition(
    val rock: Rock,
    val point: Point,
    val stopped: Boolean = false,
) {
    val nextY: Int = point.y + rock.height

    fun stop(): RockPosition = copy(stopped = true)

    fun overlaps(other: RockPosition): Boolean =
        points.intersect(other.points).isNotEmpty()

    val points: Set<Point>
        get() = rock.points.map { (x, y) -> Point(point.x + x, point.y + y) }.toSet()
}

sealed interface Movement

object Left : Movement {
    override fun toString(): String = "<"
}

object Right : Movement {
    override fun toString(): String = ">"
}

data class Point(
    val x: Int,
    val y: Int,
)

interface Rock {
    val points: List<Point>
    val height: Int
    val width: Int

    fun heightAt(x: Int): Int

    companion object {
        val orderedRocks: List<Rock> = listOf(
            HorizontalLine,
            Plus,
            InvertedL,
            VerticalLine,
            Square
        )
    }

    fun pos(x: Int, y: Int): RockPosition = RockPosition(this, Point(x, y))

    fun pos(y: Int): RockPosition = RockPosition(this, Point(2, y))
}

object HorizontalLine : Rock {
    override val points: List<Point> = listOf(
        Point(0, 0),
        Point(1, 0),
        Point(2, 0),
        Point(3, 0),
    )

    override val height: Int = 1
    override val width: Int = 4
    override fun heightAt(x: Int): Int =
        if (x in 0..3) 1 else Int.MIN_VALUE

    override fun toString(): String = "_"
}

object Plus : Rock {
    override val points: List<Point> = listOf(
        Point(1, 0),
        Point(0, 1),
        Point(1, 1),
        Point(2, 1),
        Point(1, 2),
    )

    override val height: Int = 3
    override val width: Int = 3
    override fun heightAt(x: Int): Int =
        when (x) {
            0 -> 2
            1 -> 3
            2 -> 2
            else -> Int.MIN_VALUE
        }

    override fun toString(): String = "+"
}

object InvertedL : Rock {
    override val points: List<Point> = listOf(
        Point(0, 0),
        Point(1, 0),
        Point(2, 0),
        Point(2, 1),
        Point(2, 2),
    )

    override val height: Int = 3
    override val width: Int = 3
    override fun heightAt(x: Int): Int =
        when (x) {
            0 -> 1
            1 -> 2
            2 -> 3
            else -> Int.MIN_VALUE
        }

    override fun toString(): String = "L"
}

object VerticalLine : Rock {
    override val points: List<Point> = listOf(
        Point(0, 0),
        Point(0, 1),
        Point(0, 2),
        Point(0, 3),
    )

    override val height: Int = 4
    override val width: Int = 1
    override fun heightAt(x: Int): Int =
        when (x) {
            0 -> 4
            else -> Int.MIN_VALUE
        }

    override fun toString(): String = "|"
}

object Square : Rock {
    override val points: List<Point> = listOf(
        Point(0, 0),
        Point(1, 0),
        Point(0, 1),
        Point(1, 1),
    )

    override val height: Int = 2
    override val width: Int = 2
    override fun heightAt(x: Int): Int =
        when (x) {
            0 -> 2
            1 -> 2
            else -> Int.MIN_VALUE
        }

    override fun toString(): String = "*"
}