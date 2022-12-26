package com.gilpereda.aoc2022.day19

import kotlinx.coroutines.async
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime


fun firstTask(input: Sequence<String>): String {
    return input.parsed()
        .sumOf { it.quality(24) }
        .toString()
}

fun secondTask(input: Sequence<String>): String {
    val threadPool = newFixedThreadPoolContext(4, "blueprints calculation")
    return runBlocking {
        val blueprints = input.parsed().toList().take(1)

        val firstDeferred = async(threadPool) {
            blueprints.getOrNull(0)?.let {
                it.bestProcess(32, 100).geode
            } ?: 1
        }

        val secondDeferred = async(threadPool) {
            blueprints.getOrNull(1)?.let {
                it.bestProcess(32, 20).geode
            } ?: 1
        }

        val thirdDeferred = async(threadPool) {
            blueprints.getOrNull(2)?.let {
                it.bestProcess(32, 20).geode
            } ?: 1
        }

        firstDeferred.await() * secondDeferred.await() * thirdDeferred.await()
    }.toString()
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

fun BluePrint.bestProcess(limit: Int, x: Int = 20): Process =
    if (limit < 23) {
        bestXProcesses(limit, 1).first()
    } else {
        (23 .. limit).fold(bestXProcesses(22, x)) { acc, l ->
            if (l < limit) {
                bestXProcesses(l, x, acc)
            } else {
                bestXProcesses(l, 1, acc)
            }
        }.first()
    }.also {
        println("Best process for blueprint $id: ${it.geode} geodes.")
    }

private fun BluePrint.bestXProcesses(limit: Int, x: Int, seed: List<Process> = emptyList()): List<Process> {
    val start = System.currentTimeMillis()
    tailrec fun go(current: Process, currentBests: List<Process>, open: MutableCollection<Process>, iter: Long): List<Process> {
        if (iter % 10_000_000 == 0L) {
            println("${LocalDateTime.now()} - Blueprint $id - Iteration $iter. ${currentBests.size} best processes already found. ${open.size} open paths.")
        }
        val nextBests = if (current.finished) {
            (currentBests + current).sortedDescending().take(x).also {
                if (current in it) {
                    println("${LocalDateTime.now()} - Blueprint $id - Iteration $iter - New best added for limit $limit with geode ${current.geode}. ${currentBests.size} best processes already found. ${open.size} open paths.")
                }
            }
        } else {
            currentBests
        }

        open.addAll(newCandidates(current, currentBests, x))

        return when (val nextCandidate = open.maxOrNull()?.also { open.remove(it) }) {
            null -> {
                println("found for $id with limit $limit: ${nextBests?.size} processes in ${System.currentTimeMillis() - start}ms")
                nextBests.ifEmpty { throw Exception("Best processes not found") }
            }

            else -> go(nextCandidate, nextBests, open, iter + 1)
        }
    }

    val initial = seed.map { it.copy(limit = limit) }.ifEmpty { listOf(Process(bluePrint = this, limit = limit)) }
    return go(initial.first(), emptyList(), initial.drop(1).toMutableList(), 0)
}

fun BluePrint.quality(limit: Int): Int = bestProcess(limit).geode * id

private fun newCandidates(current: Process, currentBests: List<Process>, x: Int): List<Process> =
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