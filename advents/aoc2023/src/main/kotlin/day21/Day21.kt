package com.gilpereda.aoc2022.day21

import com.gilpereda.aoc2022.utils.TypedTwoDimensionalMap
import com.gilpereda.aoc2022.utils.geometry.Point
import com.gilpereda.aoc2022.utils.map.UnlimitedTypedTwoDimensionalMap
import com.gilpereda.aoc2022.utils.parseToMap

private const val Start = 'S'
private const val Rock = '#'
private const val GardenPlot = '.'

typealias Hash = Int
typealias Count = Int
typealias Loop = List<BlockState>?

enum class Cell {
    Used,
    Empty,
    Rock,
    Start,
}

val example: String = """
        ...........
        .....###.#.
        .###.##..#.
        ..#.#...#..
        ....#.#....
        .##..S####.
        .##..#...#.
        .......##..
        .##.#.####.
        .##..##.##.
        ...........
    """.trimIndent()

fun firstTaskExample(input: Sequence<String>): String =
    firstTask(input, 6 + 1)

fun firstTaskReal(input: Sequence<String>): String =
    firstTask(input, 64 + 1)

fun firstTask(input: Sequence<String>, steps: Int): String {
    val map = input.toList().parseToMap { c -> c }
    val start = map.valuesIndexed().first { it.second == 'S' }

    return generateSequence(setOf(start.first)) { points ->
        points.flatMap { point ->
            map.next(point)
        }.toSet()

    }
        .take(steps).last().count().toString()
}

fun secondTaskExample(input: Sequence<String>): String =
    secondTask(input, 100 + 1)

fun secondTaskReal(input: Sequence<String>): String =
    secondTask(input, 5000 + 1)

fun secondTask(input: Sequence<String>, steps: Int): String {
    val world = World(input.toList().parseToMap { c -> c }.toUnlimited())

//    val output = File(Files.createTempFile("output", ".txt").toUri())
    return generateSequence(world) { it.next() }
        .take(steps).last().count().toString()
        .also {
            world
//            println(output.absolutePath)
//            output.writeText(world.dumpBlockInfo())
        }
}


typealias BlockCoordinate = Point

fun createWorld(loadReal: Boolean = false): World {
    val input = if (loadReal) {
        World::class.java.getResourceAsStream("/day21/input")!!.bufferedReader().lineSequence()
    } else {
        example.splitToSequence("\n")
    }
    return World(input.toList().parseToMap { c -> c }.toUnlimited())
}


class World(val map: UnlimitedTypedTwoDimensionalMap<Char>) {
    private var activePoints: Set<Point> = setOf(map.valuesIndexed().first { it.second == 'S' }.first)

    private var step = 0

    val loopedBlocksCount = mutableMapOf(step to Pair(0, 0))

    operator fun get(point: Point): Cell {
        val normalizedPoint = point.normalized()
        val blockCoordinate = point.toBlockCoordinate()
        val block = blockForCoordinate(blockCoordinate)

        return if (block.isActive(normalizedPoint, step))
            Cell.Used
        else
            when (map[point]) {
                '#' -> Cell.Rock
                'S' -> Cell.Start
                else -> Cell.Empty
            }
    }

    private val blockWidth = map.originalWidth
    private val blockHeight = map.originalHeight

    private val coordinateToBlock: MutableMap<BlockCoordinate, Block> = mutableMapOf()
    private val loopedBlocks = mutableListOf<LoopBlock>()

    val blockInfo: MutableMap<Point, BlockLoopEntry> = mutableMapOf()

    private var loop: Loop = null

    fun next(): World {
        val newPoints = activePoints.flatMap { point -> point.neighbours.values.filter { map[it] != Rock } }.toSet()
        activePoints = updateBlocks(newPoints)
        loopedBlocksCount[step] = loopedBlocks.count() to loopedBlocks.sumOf { it.blocksAfter(step) }
        if (step % 200 == 0) println("Step $step, active points: ${activePoints.size}")
        step += 1
        return this
    }

    fun dumpBlockInfo(): String {
        val points = blockInfo.keys
        val minX = points.minOf { it.x }
        val minY = points.minOf { it.y }
        val maxX = points.maxOf { it.x }
        val maxY = points.maxOf { it.y }

        val fill = "                 "
        return (minY..maxY).joinToString("\n") { y ->
            (minX..maxX).joinToString(" ") { x ->
                val point = Point.from(x, y)
                val content = blockInfo[point]
                    ?.let { "(${it.hash} - ${it.initialStep} - ${it.stepsToLoop})" }
                    ?: ""
                (content + fill).take(fill.length)
            }
        }
    }

    private fun blockForCoordinate(blockCoordinates: BlockCoordinate): Block =
        coordinateToBlock.computeIfAbsent(blockCoordinates) { ActiveBlock(loop) }

    private fun updateBlocks(points: Set<Point>): Set<Point> =
        points.groupBy { it.toBlockCoordinate() }
            .map { (blockCoordinates, points) ->
                when (val block = blockForCoordinate(blockCoordinates)) {
                    is ActiveBlock -> {
                        if (block.update(step, points.normalized())) {
                            val loopBlock = block.toLoopBlock(step)
                            coordinateToBlock[blockCoordinates] = loopBlock
                            loopedBlocks.add(loopBlock)
                            val (firstStep, value) = block.stepToActivePoints.entries.minBy { it.key }
                            blockInfo[blockCoordinates] = BlockLoopEntry(
                                initialStep = firstStep,
                                hash = value.hash,
                                stepsToLoop = step - firstStep
                            )
                            emptyList()
                        } else {
                            points
                        }
                    }

                    is LoopBlock -> emptyList()
                }
            }.flatten().toSet()

    private fun loopDetected(loop: Loop) {
        println("Loop detected")
        this.loop = loop
        coordinateToBlock.values.filterIsInstance<ActiveBlock>().forEach { it.loopDetected(loop) }
    }

    private fun Point.toBlockCoordinate(): BlockCoordinate =
        BlockCoordinate.from(x.mapped(blockWidth), y.mapped(blockHeight))

    fun count(): Int =
        activePoints.count() + blockPoints()

    private fun blockPoints(): Int =
        coordinateToBlock.values.filterIsInstance<LoopBlock>()
            .sumOf { it.blocksAfter(step) }

    private fun List<Point>.normalized(): Set<Point> =
        map { it.normalized() }.toSet()

    private fun Point.normalized(): Point =
        map.transformed(this)
}

data class BlockLoopEntry(
    val initialStep: Int,
    val hash: Int,
    val stepsToLoop: Int,
)

fun Int.mapped(length: Int): Int =
    if (this >= 0) this / length else (this + 1) / length - 1

sealed interface Block {
    fun isActive(point: Point, step: Int): Boolean
}

private const val GAP = 10
private const val CYCLE_COUNT = 10
private const val MIN_CYCLE_LENGTH = 2
private const val MAX_CYCLE_LENGTH = 2
private const val START_DETECTION = GAP + (MAX_CYCLE_LENGTH * CYCLE_COUNT)

class ActiveBlock(
    private var loop: Loop = null
) : Block {

    private var activePoints: Set<Point> = emptySet()
    val stepToActivePoints: MutableMap<Int, BlockState> = mutableMapOf()

    fun loopDetected(loop: Loop) {
        this.loop = loop
    }

    fun toLoopBlock(step: Int): LoopBlock =
        LoopBlock(loop = loop!!, loopEntered = step)

    fun update(step: Int, points: Set<Point>): Boolean {
        stepToActivePoints[step] = BlockState(hash = points.hashCode(), points = points, count = points.size)
        activePoints = points
        return this.loop?.let { loop ->
            if (points.hashCode() == loop.firstOrNull()?.hash) {
                println("Detected loop entry in ${stepToActivePoints.size}")
                true
            } else {
                false
            }
        } ?: cycleDetection(step)
    }

    private fun cycleDetection(step: Int): Boolean {
        val cycleLength = (MAX_CYCLE_LENGTH downTo MIN_CYCLE_LENGTH)
            .firstOrNull { cycleDetected(it) }
        if (cycleLength != null) {
            loop = stepToActivePoints.last(cycleLength)
        }
        return cycleLength != null
    }

    private fun <K : Comparable<K>, T> Map<K, T>.last(count: Int): List<T> =
        map { (k, v) -> k to v }.sortedBy { it.first }.map { it.second }.subList(size - count, size)

    private fun <K : Comparable<K>, T> Map<K, T>.first(count: Int): List<T> =
        map { (k, v) -> k to v }.sortedBy { it.first }.map { it.second }.subList(0, count)


    private fun cycleDetected(loopLength: Int): Boolean =
        if (stepToActivePoints.size >= START_DETECTION) {
            val items = stepToActivePoints.last(CYCLE_COUNT * loopLength).map { it.hash }
            val cycleList = items.chunked(loopLength)
            cycleList.areEqual()
        } else {
            false
        }

    private fun List<List<Int>>.areEqual(): Boolean {
        tailrec fun go(acc: List<List<Int>>): Boolean =
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

    private val List<List<Int>>.decomposed: Pair<List<Int>, List<List<Int>>>
        get() = fold(Pair(emptyList(), emptyList())) { acc, next ->
            Pair(
                acc.first + next.first(),
                acc.second.plus<List<Int>>(next.drop(1)),
            )
        }

    override fun isActive(point: Point, step: Int): Boolean =
        point in activePoints
}

data class BlockState(
    val hash: Int,
    val points: Set<Point>,
    val count: Int,
)

data class LoopBlock(
    val loop: List<BlockState>,
    private val loopEntered: Int,
) : Block {
    fun blocksAfter(steps: Int): Int =
        loop[(steps - loopEntered) % loop.size].count

    fun loopIsEqual(other: LoopBlock): Boolean {
        val thisHashes = loop.map { it.hash }
        val otherHashes = other.loop.map { it.hash }
        val start = otherHashes.indexOf(thisHashes.first())
        val otherComparable = otherHashes.subList(start, otherHashes.size) + otherHashes.subList(0, start)
        return thisHashes == otherComparable
    }

    override fun isActive(point: Point, step: Int): Boolean =
        point in loop[(step - loopEntered) % loop.size].points
}

fun TypedTwoDimensionalMap<Char>.next(point: Point): List<Point> =
    point.neighbours.values
        .filter { withinMap(it) && get(it) != Rock }
