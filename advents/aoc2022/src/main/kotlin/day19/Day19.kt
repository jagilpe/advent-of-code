package com.gilpereda.aoc2022.day19


fun firstTask(input: Sequence<String>): String =
    input.parsed()
        .sumOf { it.quality(24) }
        .toString()

fun secondTask(input: Sequence<String>): String = TODO()

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

fun BluePrint.quality(minutes: Int): Int {
    tailrec fun go(step: Step, round: Long): Int {
        if (round % 100_000_000L == 0L) {
            println("round: $round, open paths: ${step.open.size}")
        }
        return if (!step.current.finished) {
            val next = step.current.nextStates()
            go(step.copy(current = next.first(), open = next.drop(1) + step.open), round + 1)
        } else {
            if (step.open.isNotEmpty()) {
                val bestPath = listOf(step.current, step.acc).maxBy { it.quality }
                val next = step.open.first()
                if (next canWin bestPath) {
                    go(
                        Step(
                            acc = bestPath,
                            current = next,
                            open = step.open.drop(1),
                        ),
                        round + 1
                    )
                } else {
                    go(
                        step.copy(open = step.open.drop(1)),
                        round + 1
                    )
                }
            } else {
                step.acc.quality
            }
        }
    }

    return go(Step(State(this, limit = minutes)), 0)
}

data class Step(
    val current: State,
    val acc: State = current,
    val open: List<State> = emptyList()
)

data class State(
    val bluePrint: BluePrint,
    val resources: Resources = Resources(),
    val robots: Resources = Resources(ore = 1,),
    val resourceEvolution: List<MinuteLog> = emptyList(),
    val time: Int = 1,
    val limit: Int = 24,
) {
    val finished: Boolean = time > limit

    infix fun canWin(other: State): Boolean =
//        true
        (resources.geode + limit - time) > other.resources.geode

    val quality: Int = bluePrint.id * resources.geode

    fun nextStates(): List<State> {
        return listOfNotNull(
            buildNothing().updateLog(time),
//            buildOneRobot()?.updateLog(time),
            buildOreRobot()?.updateLog(time),
            buildClayRobot()?.updateLog(time),
            buildObsidianRobot()?.updateLog(time),
            buildGeodeRobot()?.updateLog(time),
        )
    }

    private fun buildOneRobot(): State? =
        when {
            canBuildGeodeRobot -> buildGeodeRobot()
            canBuildObsidianRobot -> buildObsidianRobot()
            canBuildClayRobot -> buildClayRobot()
            canBuildOreRobot -> buildOreRobot()
            else -> null
        }

    private val canBuildOreRobot: Boolean = resources covers bluePrint.oreRobot

    private val canBuildClayRobot: Boolean = resources covers bluePrint.clayRobot

    private val canBuildObsidianRobot: Boolean = resources covers bluePrint.obsidianRobot

    private val canBuildGeodeRobot: Boolean = resources covers bluePrint.geodeRobot

    private fun buildNothing(): State =
        copy(
            resources = resources + robots,
            time = time + 1,
        )

    private fun buildOreRobot(): State? =
        if (canBuildOreRobot) copy(
            resources = resources + robots - bluePrint.oreRobot,
            robots = robots.copy(ore = robots.ore + 1),
            time = time + 1
        ) else null

    private fun buildClayRobot(): State? =
        if (canBuildClayRobot) copy(
            resources = resources + robots - bluePrint.clayRobot,
            robots = robots.copy(clay = robots.clay + 1),
            time = time + 1
        ) else null

    private fun buildObsidianRobot(): State? =
        if (canBuildObsidianRobot) copy(
            resources = resources + robots - bluePrint.obsidianRobot,
            robots = robots.copy(obsidian = robots.obsidian + 1),
            time = time + 1
        ) else null

    private fun buildGeodeRobot(): State? =
        if (canBuildGeodeRobot) copy(
            resources = resources + robots - bluePrint.geodeRobot,
            robots = robots.copy(geode = robots.geode + 1),
            time = time + 1
        ) else null

}

data class MinuteLog(
    val time: Int,
    val robots: Resources,
    val resources: Resources,
)

fun State.updateLog(time: Int): State =
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
) {
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
}