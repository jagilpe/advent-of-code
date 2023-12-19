package com.gilpereda.aoc2022.day19

private val WORKFLOW_REGEX = "([a-zA-Z]+)\\{(.+)\\}".toRegex()
private val PART_REGEX = "\\{x=([0-9]+),m=([0-9]+),a=([0-9]+),s=([0-9]+)\\}".toRegex()

fun firstTask(input: Sequence<String>): String {
    val (workflowsString, partsString) = input.joinToString("\n").split("\n\n")
    val workflows = workflowsString.parseWorkflows()
    val parts = partsString.parseParts()
    return workflows.solve(parts).toString()
}

fun secondTask(input: Sequence<String>): String {
    val (workflowsString, _) = input.joinToString("\n").split("\n\n")
    val workflows = workflowsString.parseWorkflows()
    return workflows.solveTwo().toString()
}

fun String.parseWorkflows(): Workflows =
    Workflows(split("\n")
        .map { it.parseWorkflow() })

fun String.parseWorkflow(): Workflow =
    WORKFLOW_REGEX.find(this)?.destructured?.let {
        (name, rulesString) ->
        val rules = rulesString.split(",")
            .map {Rule.from(it) }
        Workflow(name = name, rules = rules)
    } ?: throw IllegalArgumentException("Could not parse workflow: $this")

fun String.parseParts(): List<Part> =
    split("\n")
        .map {
            PART_REGEX.find(it)?.destructured?.let {
                    (x, m, a ,s) ->
                Part(mapOf(
                    "x" to x.toInt(),
                    "m" to m.toInt(),
                    "a" to a.toInt(),
                    "s" to s.toInt(),
                ))
            } ?: throw IllegalArgumentException("Could not parse part: $this")
        }

class Workflows(
    workflows: List<Workflow>,
) {
    private val start = "in"
    private val nameToWorkflow = workflows.associateBy { it.name }

    fun solve(parts: List<Part>): Int =
        parts
            .map { it to solvePart(it) }
            .sumOf { it.second }

    fun solveTwo(): Long {
        tailrec fun go(rest: List<Pair<Probe, String>>, acc: Long = 0): Long =
            if (rest.isEmpty()) {
                acc
            } else {
                val next = rest.flatMap { (probe, workflow) -> nameToWorkflow[workflow]!!.next(probe) }
                val finishedSuccessfully = next.filter { it.second == "A" }.sumOf { it.first.combinations }
                go(next.filter { (_, workflow) -> workflow != "A" && workflow != "R" }, acc + finishedSuccessfully)
            }

        return go(listOf(Pair(Probe(), start)))
    }

    private fun solvePart(part: Part): Int {
        tailrec fun go(next: String): Int =
            when (next) {
                "A" -> part.rating
                "R" -> 0
                else -> go(nameToWorkflow[next]!!.next(part))
            }

        return go(start)
    }
}

@Suppress("UNCHECKED_CAST")
data class Workflow(
    val name: String,
    val rules: List<Rule>,
) {
    fun next(part: Part): String =
        rules.firstNotNullOf { it.next(part) }

    fun next(probe: Probe): List<Pair<Probe, String>> {
        tailrec fun go(rest: List<Rule>, pending: List<Probe>, acc: List<Pair<Probe, String>> = emptyList()): List<Pair<Probe, String>> =
            if (rest.isEmpty()) {
                acc
            } else {
                val nextRule = rest.first()
                val new = pending.flatMap { nextRule.next(it) }
                val newPending = new.filter { it.second == null }.map { it.first }
                val newAcc: List<Pair<Probe, String>> = acc + new.filter { it.second != null } as List<Pair<Probe, String>>
                go(rest.drop(1), newPending, newAcc)
            }

        return go(rules, listOf(probe))
    }
}

sealed interface Rule {
    fun next(part: Part): String?

    fun next(probe: Probe): List<Pair<Probe, String?>>

    companion object {
        fun from(string: String): Rule =
            when  {
                string.contains("<") -> LessThanRule.from(string)
                string.contains(">") -> MoreThanRule.from(string)
                else -> UnconditionalRule(string)
            }
    }
}

data class LessThanRule(
    val property: String,
    val value: Int,
    val destination: String
) : Rule {
    override fun next(part: Part): String? =
        if (part[property] < value) destination else null

    override fun next(probe: Probe): List<Pair<Probe, String?>> {
        val range = probe[property]
        return when {
            value in range -> listOf(
                Pair(probe.withRange(property, range.first until value), destination),
                Pair(probe.withRange(property, value .. range.last), null),
            )
            value > range.last -> listOf(Pair(probe, destination))
            else -> listOf(Pair(probe, null))
        }
    }

    companion object {
        fun from(string: String): LessThanRule =
            string.split(":").let { (comparison, destination) ->
                comparison.split("<").let { (property, value) ->
                    LessThanRule(property = property, value = value.toInt(), destination = destination)
                }
            }
    }
}

data class MoreThanRule(
    val property: String,
    val value: Int,
    val destination: String
) : Rule {
    override fun next(part: Part): String? =
        if (part[property] > value) destination else null

    override fun next(probe: Probe): List<Pair<Probe, String?>> {
        val range = probe[property]
        return when {
            value in range -> listOf(
                Pair(probe.withRange(property, range.first .. value), null),
                Pair(probe.withRange(property, value + 1 .. range.last), destination),
            )
            value > range.last -> listOf(Pair(probe, destination))
            else -> listOf(Pair(probe, null))
        }
    }

    companion object {
        fun from(string: String): MoreThanRule =
            string.split(":").let { (comparison, destination) ->
                comparison.split(">").let { (property, value) ->
                    MoreThanRule(property = property, value = value.toInt(), destination = destination)
                }
            }
    }
}

data class UnconditionalRule(
    val destination: String
) : Rule {
    override fun next(part: Part): String = destination
    override fun next(probe: Probe): List<Pair<Probe, String?>>  = listOf(Pair(probe, destination))

    companion object {
        fun from(string: String): UnconditionalRule =
            UnconditionalRule(string)
    }
}

data class Part(
    private val properties: Map<String, Int>,
) {
    val rating: Int
        get() = properties.values.sum()
    operator fun get(property: String): Int =
        properties[property] ?: throw IllegalArgumentException("Could not read property: $property")
}

data class Probe(
    private val properties: Map<String, IntRange> =
        mapOf(
            "x" to 1 .. 4000,
            "m" to 1 .. 4000,
            "a" to 1 .. 4000,
            "s" to 1 .. 4000,
        )
) {
    operator fun get(property: String): IntRange =
        properties[property] ?: throw IllegalArgumentException("Could not read property: $property")

    fun withRange(property: String, range: IntRange): Probe =
        Probe(properties + (property to range))

    val combinations: Long by lazy {
        properties.values.fold(1) { acc, next ->
            acc * next.count()
        }
    }
}