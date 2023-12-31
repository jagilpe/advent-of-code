package com.gilpereda.aoc2022.day19

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.runBlocking
import java.io.File
import java.time.LocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue


private val objectMapper = jacksonObjectMapper()
private val writer = objectMapper.writer()
private val reader = objectMapper.reader()

private val processListRef: TypeReference<List<Process>> = object : TypeReference<List<Process>>() {}

fun firstTask(input: Sequence<String>): String {
    return input.parsed()
        .sumOf { it.quality(24) }
        .toString()
}

fun secondTask(input: Sequence<String>): String {
    val threadPool = newFixedThreadPoolContext(4, "blueprints calculation")
    return runBlocking {
        val blueprints = input.parsed().toList().take(3)

        val firstDeferred: Deferred<Process?> = async(threadPool) {
            blueprints.getOrNull(0)?.let {
                val seed = it.loadBestProcesses(22, 100)
                it.bestXProcesses(32, 1, seed).first()
            } ?:
            null
        }

        val secondDeferred: Deferred<Process?> = async(threadPool) {
            blueprints.getOrNull(1)?.let {
                val seed = it.loadBestProcesses(22, 100)
                it.bestXProcesses(32, 1, seed).first()
            } ?:
            null
        }

        val thirdDeferred: Deferred<Process?> = async(threadPool) {
            blueprints.getOrNull(2)?.let {
                val seed = it.loadBestProcesses(22, 100)
                it.bestXProcesses(32, 1, seed).first()
            } ?:
            null
        }

        val first = firstDeferred.await()
        val second = secondDeferred.await()
        val third = thirdDeferred.await()
        println("Blueprint 1 - ${first?.geode} geodes")
        println("Blueprint 2 - ${first?.geode} geodes")
        println("Blueprint 3 - ${first?.geode} geodes")

        (first?.geode ?: 1) * (second?.geode ?: 1) * (third?.geode ?: 1)
    }.toString()
}

fun BluePrint.saveBestProcesses(limit: Int, count: Int) {
    val processed = bestXProcesses(limit, count)
    val path = System.getProperty("user.dir")
    writer.writeValue(File("$path/blueprint-$id-$limit.json"), processed)
}

fun BluePrint.loadBestProcesses(limit: Int, count: Int): List<Process> {
    val fileName = "/day19/blueprint-$id-$limit.json"
    val uri = Process::class.java.getResource(fileName).toURI()

    return objectMapper.readValue(File(uri), processListRef).sortedDescending().take(count)
}

private val PARSE_REGEX =
    "Blueprint ([0-9]+): Each ore robot costs ([0-9]+) ore. Each clay robot costs ([0-9]+) ore. Each obsidian robot costs ([0-9]+) ore and ([0-9]+) clay. Each geode robot costs ([0-9]+) ore and ([0-9]+) obsidian.".toRegex()

fun Sequence<String>.parsed(): Sequence<BluePrint> =
    map { line ->
        PARSE_REGEX.find(line)?.destructured?.let { (id, oreCost, clayCost, obsidianCost1, obsidianCost2, geodeCost1, geodeCost2) ->
            BluePrint(
                id = id.toInt(),
                oreRobot = Resources(ore = oreCost.toInt()),
                clayRobot = Resources(ore = clayCost.toInt()),
                obsidianRobot = Resources(ore = obsidianCost1.toInt(), clay = obsidianCost2.toInt()),
                geodeRobot = Resources(ore = geodeCost1.toInt(), obsidian = geodeCost2.toInt()),
            )
        } ?: throw Exception("Could not parse $line")
    }

@OptIn(ExperimentalTime::class)
fun BluePrint.bestProcess(limit: Int, x: Int = 1_000): Process =
    measureTimedValue {
        if (limit < 23) {
            bestXProcesses(limit, 1).first()
        } else {
            val best22 = bestXProcesses(22, x)
            bestXProcesses(limit, 1, best22).first()
        }
    }.let { (value, duration) ->
        println("Best process for blueprint $id: ${value.geode} geodes. Found in ${duration.inWholeSeconds}s")
        value
    }

private fun BluePrint.bestXProcesses(limit: Int, count: Int, seed: List<Process> = emptyList()): List<Process> {
    val start = System.currentTimeMillis()
    var lastIterStart = start
    tailrec fun go(current: Process, currentBests: MutableCollection<Process>, currentWorstBest: Process?, open: MutableCollection<Process>, iter: Long): List<Process> {
        if (iter % 1_000_000 == 0L) {
            val ellapsed = System.currentTimeMillis() - lastIterStart
            println("${LocalDateTime.now()} - Blueprint $id - Iteration $iter. ${currentBests.size} best processes already found (${currentBests.minOfOrNull { it.geode }}~${currentBests.maxOfOrNull { it.geode }}) . ${open.size} open paths. $ellapsed ms")
            lastIterStart = System.currentTimeMillis()
        }
        val nextWorstBest = if (current.finished) {
            if (currentBests.size < count || current wins currentWorstBest) {
                currentBests.add(current)
                when {
                    currentBests.size > count -> {
                        currentBests.minOrNull()?.also { currentBests.remove(it) }
                        currentBests.minOrNull()
                    }
                    currentBests.size == count -> currentBests.minOrNull()
                    else -> currentWorstBest
                }
            } else {
                currentWorstBest
            }
        } else {
            currentWorstBest
        }

        open.addAll(newCandidates(current, currentBests, count))

        return when (val nextCandidate = open.maxOrNull()?.also { open.remove(it) }) {
            null -> {
                println("found for $id with limit $limit: ${currentBests?.size} processes in ${System.currentTimeMillis() - start}ms")
                currentBests.ifEmpty { throw Exception("Best processes not found") }.toList()
            }

            else -> go(nextCandidate, currentBests, nextWorstBest, open, iter + 1)
        }
    }

    val initial = seed.map { it.copy(limit = limit) }.ifEmpty { listOf(Process(bluePrint = this, limit = limit)) }
    return go(initial.first(), mutableSetOf(), initial.minOrNull(), initial.drop(1).toMutableList(), 0)
}

fun BluePrint.quality(limit: Int): Int = bestProcess(limit).geode * id

private fun newCandidates(current: Process, currentBests: MutableCollection<Process>, x: Int): List<Process> =
    if (!current.finished && (currentBests.size < x || currentBests.any { current canWin it })) {
        current.nextCandidates()
    } else {
        emptyList()
    }

data class Process(
    val bluePrint: BluePrint,
    val resources: Resources = Resources(),
    val robots: Resources = Resources(ore = 1),
    val resourceEvolution: List<MinuteLog> = emptyList(),
    val firstGeodeRobotAt: Int? = null,
    val time: Int = 1,
    val limit: Int,
) : Comparable<Process> {
    val finished: Boolean = time > limit

    infix fun wins(other: Process?): Boolean =
        this.finished && (other == null || this > other)

    infix fun canWin(other: Process?): Boolean =
        !(this cannotWin other)

    infix fun cannotWin(other: Process?): Boolean =
        robots.geode == 0 && time > (other?.firstGeodeRobotAt ?: limit)

    val geode: Int = resources.geode

    fun nextCandidates(): List<Process> {
        return listOf(
            Recollect(),
            BuildOreRobot(),
            BuildClayRobot(),
            BuildObsidianRobot(),
            BuildGeodeRobot(),
        ).filter { it.resourcesReach }
            .sortedDescending()
            .map { it.next().updateLog() }
    }

    override fun compareTo(other: Process): Int =
        when {
            geode > other.geode -> 1
            geode < other.geode -> -1
            else -> when (val robotComp = robots.compareTo(other.robots)) {
                0 -> resources.compareTo(other.resources)
                else -> robotComp
            }
        }

    interface Operation : Comparable<Operation> {
        fun next(): Process
        val resourcesReach: Boolean
        val priority: Int

        override fun compareTo(other: Operation): Int =
            priority.compareTo(other.priority)
    }

    inner class Recollect : Operation {
        override fun next(): Process =
            copy(
                resources = resources + robots,
                time = time + 1,
            )

        override val resourcesReach: Boolean
            get() = true
        override val priority: Int = 0
    }

    inner class BuildOreRobot : Operation {
        private val basePriority: Int = 10
        override val priority: Int = 10
        override fun next(): Process =
            copy(
                resources = resources + robots - bluePrint.oreRobot,
                robots = robots.copy(ore = robots.ore + 1),
                time = time + 1
            )

        override val resourcesReach: Boolean = resources covers bluePrint.oreRobot
    }

    inner class BuildClayRobot : Operation {
        override val priority: Int = 20
        override fun next(): Process =
            copy(
                resources = resources + robots - bluePrint.clayRobot,
                robots = robots.copy(clay = robots.clay + 1),
                time = time + 1
            )

        override val resourcesReach: Boolean = resources covers bluePrint.clayRobot
    }

    inner class BuildObsidianRobot : Operation {
        override val priority: Int = 30
        override fun next(): Process =
            copy(
                resources = resources + robots - bluePrint.obsidianRobot,
                robots = robots.copy(obsidian = robots.obsidian + 1),
                time = time + 1
            )

        override val resourcesReach: Boolean = resources covers bluePrint.obsidianRobot
    }

    inner class BuildGeodeRobot : Operation {
        override val priority: Int = 40
        override fun next(): Process =
            copy(
                resources = resources + robots - bluePrint.geodeRobot,
                robots = robots.copy(geode = robots.geode + 1),
                time = time + 1,
                firstGeodeRobotAt = time
            )

        override val resourcesReach: Boolean = resources covers bluePrint.geodeRobot
    }
}

data class MinuteLog(
    val time: Int,
    val robots: Resources,
    val resources: Resources,
)

fun Process.updateLog(): Process =
    copy(
        resourceEvolution = resourceEvolution + MinuteLog(time = time, robots = robots, resources = resources)
    )

data class BluePrint(
    val id: Int,
    val oreRobot: Resources,
    val clayRobot: Resources,
    val obsidianRobot: Resources,
    val geodeRobot: Resources,
)

data class Resources(
    val ore: Int = 0,
    val clay: Int = 0,
    val obsidian: Int = 0,
    val geode: Int = 0,
) : Comparable<Resources> {
    infix fun covers(other: Resources): Boolean =
        ore >= other.ore && clay >= other.clay && obsidian >= other.obsidian && geode >= other.geode

    operator fun minus(other: Resources): Resources =
        copy(
            ore = ore - other.ore,
            clay = clay - other.clay,
            obsidian = obsidian - other.obsidian,
            geode = geode - other.geode,
        )

    operator fun plus(other: Resources): Resources =
        copy(
            ore = ore + other.ore,
            clay = clay + other.clay,
            obsidian = obsidian + other.obsidian,
            geode = geode + other.geode,
        )

    override fun compareTo(other: Resources): Int =
        when {
            geode > other.geode -> 1
            geode < other.geode -> -1
            else -> when {
                obsidian > other.obsidian -> 1
                obsidian < other.obsidian -> -1
                else -> when {
                    clay > other.clay -> 1
                    clay < other.clay -> -1
                    else -> when {
                        ore > other.ore -> 1
                        ore < other.ore -> -1
                        else -> 0
                    }
                }
            }

        }
}