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

//    val start = System.currentTimeMillis() / 1000

    val (squeezedRocks, droppedRocks, rocksPerCycle, cycle) = findRepeatingPattern(movements, rocks)

    val cycles = (rocks - droppedRocks) / rocksPerCycle
    val rest = (rocks - droppedRocks) % rocksPerCycle

    val cyclesHeight = cycles * cycle.sum()
    val restHeight = cycle.take(rest.toInt()).sum()

    val result = squeezedRocks.height + cyclesHeight + restHeight
    return result.toString()
}

fun String.parsed(): List<Movement> =
    map {
        when (it) {
            '<' -> Left
            '>' -> Right
            else -> throw Exception("Unknown direction")
        }
    }


private const val BOARD_WIDTH: Int = 7

fun Rock.position(squeezedRocks: SqueezedRocks): RockPosition =
    this.pos(squeezedRocks.height + 3)

fun RockPosition.move(movement: Movement, squeezedRocks: SqueezedRocks): RockPosition =
    push(movement, squeezedRocks).drop(squeezedRocks)

private fun RockPosition.push(movement: Movement, squeezedRocks: SqueezedRocks): RockPosition =
    when (movement) {
        is Left -> copy(point = point.copy(x = point.x - 1))
        is Right -> copy(point = point.copy(x = point.x + 1))
    }
        .let { if (it.valid(squeezedRocks)) it else this }

private fun RockPosition.drop(squeezedRocks: SqueezedRocks): RockPosition =
    copy(point = point.copy(y = maxOf(point.y - 1, 0L)), stopped = point.y == 0L)
        .let { if (it.valid(squeezedRocks)) it else copy(stopped = true) }
        .also { if (it.point.y < squeezedRocks.minY) throw IllegalStateException("Rock should not go through the squeezed rocks") }

private fun RockPosition.valid(squeezedRocks: SqueezedRocks): Boolean =
    (point.x in 0..(BOARD_WIDTH - rock.width)) && !this.overlaps(squeezedRocks)

data class CycleResult(
    val squeezedRocks: SqueezedRocks,
    val droppedRocks: Long,
    val rocksPerCycle: Long,
    val cycleIncrements: List<Long>,
)

fun findRepeatingPattern(
    movements: List<Movement>,
    maxDroppedRocks: Long,
): CycleResult {
    tailrec fun go(
        currentRock: RockPosition,
        nextRocks: List<Rock>,
        nextMovements: List<Movement>,
        squeezedRocks: SqueezedRocks,
        droppedRocks: Long,
        cycleDetector: CycleDetector,
    ): CycleResult =
        when {
            currentRock.stopped -> {
                if (droppedRocks % 100 == 0L) {
                    println("${LocalDateTime.now()} - droppedRocks: $droppedRocks")
                }
                val previousHeight = squeezedRocks.height
                val newSqueezedRocks = squeezedRocks.add(currentRock)
                val newNextRocks = nextRocks.ifEmpty { Rock.orderedRocks }
                val newCurrentRock = newNextRocks.first().position(newSqueezedRocks)
                val newCycleDetector = cycleDetector.add(previousHeight, newSqueezedRocks.height)
                val cycle = newCycleDetector.cycle()
                if (cycle != null) {
                    CycleResult(squeezedRocks, droppedRocks, cycle.first, cycle.second)
                } else {
                    go(
                        currentRock = newCurrentRock,
                        nextRocks = newNextRocks.drop(1),
                        nextMovements = nextMovements,
                        squeezedRocks = newSqueezedRocks,
                        droppedRocks = droppedRocks + 1,
                        cycleDetector = newCycleDetector,
                    )
                }
            }

            else -> {
                val newNextMovements = nextMovements.ifEmpty { movements }
                go(
                    currentRock = currentRock.push(newNextMovements.first(), squeezedRocks).drop(squeezedRocks),
                    nextRocks = nextRocks,
                    nextMovements = newNextMovements.drop(1),
                    squeezedRocks = squeezedRocks,
                    droppedRocks = droppedRocks,
                    cycleDetector = cycleDetector,
                )
            }
        }

    val squeezedRocks = SqueezedRocks()

    return go(
        currentRock = Rock.orderedRocks.first().position(squeezedRocks),
        nextRocks = Rock.orderedRocks.drop(1),
        nextMovements = movements,
        squeezedRocks = squeezedRocks,
        droppedRocks = 1,
        cycleDetector = CycleDetector(),
    )
}

private const val CYCLE_COUNT = 20
private const val GAP = 100_000

class CycleDetector {
    private val nodes: MutableList<Long> = mutableListOf()
    private var minCycle = 5

    fun add(previousHeight: Long, newHeight: Long): CycleDetector {
        nodes.add(newHeight - previousHeight)
        return this
    }

    fun cycle(): Pair<Long, List<Long>>? =
        if (nodes.size >= GAP + (minCycle * CYCLE_COUNT)) {
            val cycle = (nodes.size - GAP) / CYCLE_COUNT
            val cycleList = nodes.drop(nodes.size - cycle * CYCLE_COUNT).chunked(cycle)
            if (cycleList.areEqual()) {
                cycle.toLong() to cycleList.first()
            } else {
                null
            }
        } else {
            null
        }

    private fun List<List<Long>>.areEqual(): Boolean {
        tailrec fun go(acc: List<List<Long>>): Boolean =
            if (acc.isEmpty() || acc.first().isEmpty()) {
                true
            } else {
                val (next, rest) = acc.decomposed
                if (next.all { it == next.first() }) {
                    go(rest)
                } else {
                    false
                }
            }

        return go(this)
    }

    private val List<List<Long>>.decomposed: Pair<List<Long>, List<List<Long>>>
        get() = fold(Pair(emptyList(), emptyList())) { acc, next ->
            Pair(
                acc.first + next.first(),
                acc.second.plus<List<Long>>(next.drop(1)),
            )
        }

}

fun dump(current: RockPosition, squeezedRocks: SqueezedRocks): String {
    val highestY = maxOf(current.maxY, squeezedRocks.height, 4)
    val toLine = squeezedRocks.minY - 1
    val pointsInMovement = current.points
    return (highestY downTo toLine)
        .joinToString("\n") { y ->
            if (y > -1) {
                (0L..6).joinToString(separator = "", prefix = "|", postfix = "|") { x ->
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
                    (0L..6).joinToString(separator = "", prefix = "|", postfix = "|") { x ->
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

    val towerHeight: Long by lazy { squeezedRocks.height }


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
//                movingRock = movingRock.push(nextMovements.first()).drop(),
            )
        }

    private fun nextRock(squeezedRocks: SqueezedRocks): RockPosition =
        nextRocks.first().pos(squeezedRocks.height + 3)

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
    val x: Long,
    val y: Long,
)

interface Rock {
    val points: Set<Point>
    val height: Long
    val width: Long

    fun heightAt(x: Long): Long

    companion object {
        val orderedRocks: List<Rock> = listOf(
            HorizontalLine,
            Plus,
            InvertedL,
            VerticalLine,
            Square
        )
    }

    fun pos(x: Long, y: Long): RockPosition = RockPosition(this, Point(x, y))

    fun pos(y: Long): RockPosition = RockPosition(this, Point(2, y))
}

object HorizontalLine : Rock {
    override val points: Set<Point> = setOf(
        Point(0, 0),
        Point(1, 0),
        Point(2, 0),
        Point(3, 0),
    )

    override val height: Long = 1L
    override val width: Long = 4L
    override fun heightAt(x: Long): Long =
        if (x in 0..3) 1 else Long.MIN_VALUE

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

    override val height: Long = 3
    override val width: Long = 3
    override fun heightAt(x: Long): Long =
        when (x) {
            0L -> 2
            1L -> 3
            2L -> 2
            else -> Long.MIN_VALUE
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

    override val height: Long = 3
    override val width: Long = 3
    override fun heightAt(x: Long): Long =
        when (x) {
            0L -> 1
            1L -> 2
            2L -> 3
            else -> Long.MIN_VALUE
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

    override val height: Long = 4
    override val width: Long = 1
    override fun heightAt(x: Long): Long =
        when (x) {
            0L -> 4
            else -> Long.MIN_VALUE
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

    override val height: Long = 2
    override val width: Long = 2
    override fun heightAt(x: Long): Long =
        when (x) {
            0L -> 2
            1L -> 2
            else -> Long.MIN_VALUE
        }

    override fun toString(): String = "*"
}

data class SqueezedRocks(
    override val points: MutableSet<Point> = mutableSetOf(),
) : Rock {

    constructor(points: Collection<Point>) : this(mutableSetOf<Point>().also { it.addAll(points) })

    fun highestLines(count: Int): Set<Point> =
        points.filter { it.y > maxY - count }.toSet()

    override val height: Long
        get() = maxY + 1
    override val width: Long
        get() = throw UnsupportedOperationException()
    var minY: Long = points.minOfOrNull { it.y }?.toLong() ?: -1L
    private var maxY: Long = points.maxOfOrNull { it.y }?.toLong() ?: -1L

    fun add(other: RockPosition): SqueezedRocks {
        points.addAll(other.points)
        maxY = points.maxOfOrNull { it.y }?.toLong() ?: -1L
        points.removeIf { it.y < maxY - 100 }
        minY = points.minOfOrNull { it.y } ?: 0L

        return this
    }

    override fun heightAt(x: Long): Long =
        points.filter { it.x == x }.maxOfOrNull { it.y } ?: Long.MIN_VALUE
}