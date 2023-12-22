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
    secondTask(input, 500 + 1)

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

    fun count(step: Int): Map<String, Int> {
        val (activePoints, loopedBlocks) = coordinateToBlock.values.fold(0 to 0) { (active, blocked), next ->
            when (next) {
                is ActiveBlock -> active + next.count(step) to blocked
                is LoopBlock -> active to next.count(step) + blocked
            }
        }
        return mapOf(
            "active" to activePoints,
            "looped" to loopedBlocks,
            "total" to activePoints + loopedBlocks
        )
    }

    operator fun get(point: Point, step: Int): Cell {
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

    operator fun get(point: Point): Cell = get(point, step)

    private val blockWidth = map.originalWidth
    private val blockHeight = map.originalHeight

    private val coordinateToBlock: MutableMap<BlockCoordinate, Block> = mutableMapOf()
    private val loopedBlocks = mutableListOf<LoopBlock>()

    val blockInfo: MutableMap<Point, BlockLoopEntry> = mutableMapOf()

    private var loop: Loop = null

    fun next(): World {
        val newPoints = activePoints.flatMap { point -> point.neighbours.values.filter { map[it] != Rock } }.toSet()
        step += 1
        activePoints = updateBlocks(newPoints)
        loopedBlocksCount[step] = loopedBlocks.count() to loopedBlocks.sumOf { it.blocksAfter(step) }
        if (step % 200 == 0) println("Step $step, active points: ${activePoints.size}")
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
                            val loopBlock = block.toLoopBlock()
                            coordinateToBlock[blockCoordinates] = loopBlock
                            loopedBlocks.add(loopBlock)
                            val (firstStep, value) = block.stepToActivePoints.entries.minBy { it.key }
                            blockInfo[blockCoordinates] = BlockLoopEntry(
                                initialStep = firstStep,
                                hash = value.hash,
                                stepsToLoop = step - firstStep
                            )
                            loopDetected(block.loop)
                            emptyList()
                        } else {
                            points
                        }
                    }

                    is LoopBlock -> emptyList()
                }
            }.flatten().toSet()

    private fun loopDetected(loop: Loop) {
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
    fun count(step: Int): Int
}

private const val GAP = 10
private const val CYCLE_COUNT = 10
private const val CYCLE_LENGTH = 2
private const val START_DETECTION = GAP + (CYCLE_LENGTH * CYCLE_COUNT)

class ActiveBlock(
    var loop: Loop = null
) : Block {

    private var enteredLoopInStep: Int? = null

    private var activePoints: Set<Point> = emptySet()
    val stepToActivePoints: MutableMap<Int, BlockState> = mutableMapOf()

    override fun count(step: Int): Int = stepToActivePoints[step]?.count ?: 0

    override fun isActive(point: Point, step: Int): Boolean =
        point in activePoints

    fun loopDetected(loop: Loop) {
        this.loop = loop
    }

    fun toLoopBlock(): LoopBlock =
        LoopBlock(loop = loop!!, loopEntered = enteredLoopInStep!!, previousStates = stepToActivePoints)

    fun update(step: Int, points: Set<Point>): Boolean {
        stepToActivePoints[step] = BlockState(hash = points.hashCode(), points = points, count = points.size)
        activePoints = points
        return this.loop?.let { loop ->
            if (points.hashCode() == loop.firstOrNull()?.hash) {
//                println("Detected loop entry in ${stepToActivePoints.size}")
                enteredLoopInStep = step
                true
            } else {
                false
            }
        } ?: cycleDetected()
    }

    private fun <K : Comparable<K>, T> Map<K, T>.last(count: Int): List<Pair<K, T>> =
        map { (k, v) -> k to v }.sortedBy { it.first }.subList(size - count, size)

    private fun <K : Comparable<K>, T> Map<K, T>.first(count: Int): List<T> =
        map { (k, v) -> k to v }.sortedBy { it.first }.map { it.second }.subList(0, count)


    private fun cycleDetected(): Boolean {
        if (stepToActivePoints.size >= START_DETECTION) {
            val items = stepToActivePoints.last(CYCLE_COUNT * CYCLE_LENGTH)
            val cycleList = items.map { it.second.hash }.chunked(CYCLE_LENGTH)
            if (cycleList.areEqual()) {
                enteredLoopInStep = items.first().first
                loop = stepToActivePoints.last(CYCLE_LENGTH).map { it.second }
                return true
            }
        }
        return false
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
}

data class BlockState(
    val hash: Int,
    val points: Set<Point>,
    val count: Int,
)

data class LoopBlock(
    val loop: List<BlockState>,
    private val loopEntered: Int,
    private val previousStates: Map<Int, BlockState>
) : Block {
    fun blocksAfter(steps: Int): Int =
        loop[(steps - loopEntered) % loop.size].count

    override fun count(step: Int): Int = blockState(step)?.count ?: 0

    private fun blockState(step: Int): BlockState? =
        if (step >= loopEntered) {
            loop[(step - loopEntered) % loop.size]
        } else {
            previousStates[step]
        }

    fun loopIsEqual(other: LoopBlock): Boolean {
        val thisHashes = loop.map { it.hash }
        val otherHashes = other.loop.map { it.hash }
        val start = otherHashes.indexOf(thisHashes.first())
        val otherComparable = otherHashes.subList(start, otherHashes.size) + otherHashes.subList(0, start)
        return thisHashes == otherComparable
    }

    override fun isActive(point: Point, step: Int): Boolean =
        blockState(step)
            ?.let { point in it.points } ?: false
}

fun TypedTwoDimensionalMap<Char>.next(point: Point): List<Point> =
    point.neighbours.values
        .filter { withinMap(it) && get(it) != Rock }
