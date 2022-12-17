package com.gilpereda.aoc2022.day17

import java.time.LocalDateTime
import java.time.ZoneId

fun firstTask(input: Sequence<String>): String =
    input.runTaskWith(2023L)

fun secondTask(input: Sequence<String>): String =
    input.runTaskWith(1000000000000L)

private const val STEP = 100_000

private val zoneOffset = ZoneId.of("Europe/Berlin").rules.getOffset(LocalDateTime.now())

fun Sequence<String>.runTaskWith(rocks: Long): String {
    val movements: List<Movement> = first().parsed()

    val start = System.currentTimeMillis() / 1000

    println("Started: ${LocalDateTime.ofEpochSecond(start, 0, zoneOffset)}")
    val last = generateSequence(Game(movements = movements)) {
        it.next()
    }
        .onEachIndexed { index, game ->
            if (index % STEP == 0) {
                println("Stopped ${game.stoppedRocks} rocks in ${game.elapsed}s")
                println("ETF: ${game.etf(rocks)}")
            }
        }
        .takeWhile { it.stoppedRocks < rocks }.last()
    return last.towerHeight.toString()
}

fun String.parsed(): List<Movement> =
    map {
        when (it) {
            '<' -> Left
            '>' -> Right
            else -> throw Exception("Unknown direction")
        }
    }

data class Game(
    val movements: List<Movement>,
    val nextMovements: List<Movement> = movements,
    val rocksOrder: List<Rock> = Rock.orderedRocks,
    val nextRocks: List<Rock> = rocksOrder,
    val movingRock: RockPosition? = null,
    val squeezedRocks: SqueezedRocks = SqueezedRocks(),
    val boardWidth: Int = 7,
    val stoppedRocks: Long = 0,
    val start: Long = System.currentTimeMillis() / 1000,
    val lastStoppedRockOn: Long = Long.MAX_VALUE / 1000,
) {
    fun etf(rocks: Long): LocalDateTime =
        try {
            LocalDateTime.ofEpochSecond(lastStoppedRockOn + (elapsed * (rocks / stoppedRocks)), 0, zoneOffset)
        } catch (ex: Exception) {
            LocalDateTime.now()
        }

    val elapsed: Long by lazy { lastStoppedRockOn - start }

//    override fun toString(): String = """Game(
//        |nextMovements: $nextMovements
//        |nextRocks: $nextRocks
//        |rocksPositions: $rocksPositions
//        |)
//    """.trimMargin()

    override fun toString(): String {
        return dump()
    }


    fun dump(header: String? = null, lines: Int? = null): String {
        val highestY = maxOf(movingRock?.maxY ?: squeezedRocks.height, 4)
        val toLine = maxOf(lines?.let { highestY - lines } ?: 0) - 1
        val pointsInMovement = movingRock?.points ?: emptySet()
        return (highestY downTo toLine)
            .joinToString(separator = "\n", prefix = header?.let { "$it\n" } ?: "") { y ->
                if (y > -1) {
                    (0..6).joinToString(separator = "", prefix = "|", postfix = "|") { x ->
                        when {
                            Point(x, y) in pointsInMovement -> "@"
                            Point(x, y) in squeezedRocks.points -> "#"
                            else -> "."
                        }
                    }
                } else {
                    "+-------+\n"
                }
            }
            .also(::println)
    }

    val towerHeight: Int by lazy { squeezedRocks.height }

    fun next(): Game =
        when {
            movingRock == null -> copy(
                nextMovements = nextMovements,
                nextRocks = nextRocks.drop(1).ifEmpty { rocksOrder },
                movingRock = nextRock(squeezedRocks),
            )

            movingRock.stopped -> {
                val newSqueezedRocks = squeezedRocks.add(movingRock)
                copy(
                    nextMovements = nextMovements,
                    nextRocks = nextRocks.drop(1).ifEmpty { rocksOrder },
                    movingRock = nextRock(newSqueezedRocks),
                    squeezedRocks = newSqueezedRocks,
                    stoppedRocks = stoppedRocks + 1,
                    lastStoppedRockOn = System.currentTimeMillis() / 1000
                )
            }

            else -> copy(
                nextMovements = nextMovements.drop(1).ifEmpty { movements },
                movingRock = movingRock.push(nextMovements.first()).drop(),
            )
        }

    private fun nextRock(squeezedRocks: SqueezedRocks): RockPosition =
        nextRocks.first().pos(squeezedRocks.height + 3)

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
        get() = (point.x in 0..(boardWidth - rock.width)) && !this.overlaps(squeezedRocks)

}

data class RockPosition(
    val rock: Rock,
    val point: Point,
    val stopped: Boolean = false,
) {
    val maxY = point.y + rock.height - 1

    fun stop(): RockPosition = copy(stopped = true)

    fun overlaps(other: SqueezedRocks): Boolean =
        points.intersect(other.points).isNotEmpty()

    val points: Set<Point> by lazy {
        rock.points.map { (x, y) -> Point(point.x + x, point.y + y) }.toSet()
    }
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
    val points: Set<Point>
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
    override val points: Set<Point> = setOf(
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
    override val points: Set<Point> = setOf(
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
    override val points: Set<Point> = setOf(
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
    override val points: Set<Point> = setOf(
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
    override val points: Set<Point> = setOf(
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

data class SqueezedRocks(
    override val points: Set<Point> = emptySet(),
) : Rock {
    fun add(other: RockPosition): SqueezedRocks =
        (points + other.points)
            .let { allPoints ->
                val newPoints = if (allPoints.size > 500) {
                    val limit = allPoints.maxY - 100
                    val newPoints = allPoints.filter { it.y >= limit }.toSet()
//                    println("removed: ${allPoints.size - newPoints.size}, new size: ${newPoints.size}")
                    newPoints
                } else {
                    allPoints
                }
                copy(points = newPoints)
            }

    override val height: Int by lazy { points.maxY }

    private val Set<Point>.maxY: Int
        get() = maxOfOrNull { it.y + 1 } ?: 0


    override val width: Int by lazy {
        points.maxOfOrNull { it.x }
            ?.let { maxX ->
                points.minOf { it.x }
                    ?.let { minX -> maxX - minX + 1 }
            }
            ?: 0
    }

    override fun heightAt(x: Int): Int =
        points.filter { it.x == x }.maxOfOrNull { it.y } ?: Int.MIN_VALUE
}