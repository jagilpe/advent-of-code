package com.gilpereda.aoc2022.day16

fun firstTask(input: Sequence<String>): String {
    val tunnelSystem = TunnelSystem(input.parsed())
    val init = Path(
        current = tunnelSystem.findValve("AA"),
        tunnelSystem = tunnelSystem,
    )

    val path = findPaths(init).maxBy(Path::flown)
    return path.flown.toString()
}

fun secondTask(input: Sequence<String>): String {
    val tunnelSystem = TunnelSystem(input.parsed())
    val start = tunnelSystem.findValve("AA")
    val init = DoublePath(
        current = Pair(start, start),
        tunnelSystem = tunnelSystem,
        ellapsed = 3,
    )

    val path = findDoublePaths(init).maxBy(DoublePath::flown)
    return path.flown.toString()
}

private val PARSE_REGEX = "Valve ([A-Z]+) has flow rate=([0-9]+); tunnel(?:s)? lead(?:s)? to valve(?:s)? (.+)".toRegex()

fun Sequence<String>.parsed(): List<Valve> =
    map {
        PARSE_REGEX.find(it)?.destructured
            ?.let { (id, flowRate, leadTo) ->
                Valve(id, flowRate.toInt(), leadTo.split(", ").toSet())
            } ?: throw Exception("Could not parse $it")
    }.toList().sortedBy { it.id }

private const val LIMIT = 30


private fun findPaths(path: Path): List<Path> {
    tailrec fun go(time: Int, acc: List<Path>): List<Path> =
        if (time <= LIMIT) {
            val nextCandidates = acc.flatMap { it.next() }
            val maxFlow = nextCandidates.maxOf(Path::maxReachableFlow)
            val nextPaths = nextCandidates.filter {
                it.maxReachableFlow >= maxFlow
            }
            println("time: $time, paths: ${nextPaths.size}, removed paths: ${nextCandidates.size - nextPaths.size}")
            go(time + 1, nextPaths)
        } else {
            acc
        }

    return go(0, listOf(path))
}

data class Path(
    private val current: Valve,
    private val tunnelSystem: TunnelSystem,
    private val openValves: Set<Valve> = emptySet(),
    private val previous: Operation? = null,
    val flown: Int = 0,
    val ellapsed: Int = 0,
) {
    private val timeLeft = LIMIT - ellapsed
    val maxReachableFlow: Int = flown + tunnelSystem.maxRemainingFlow(openValves) * timeLeft
    fun next(): List<Path> =
        possibleOps.map { operation ->
            when (operation) {
                is OpenValve -> copy(
                    openValves = openValves + current,
                    previous = operation,
                    ellapsed = ellapsed + 1,
                    flown = flown + (operation.valveToOpen.flowRate * (LIMIT - ellapsed - 1))
                )

                is GoToValve -> copy(
                    current = operation.valve,
                    previous = operation,
                    ellapsed = ellapsed + 1,
                )
            }
        }

    private val possibleOps: List<Operation>
        get() = current.leadTo.map { GoToValve(findValve(it)) }.filter { it != previous } +
                if (current.isClosed) listOf(OpenValve(current)) else emptyList()

    private val Valve.isClosed: Boolean
        get() = this !in tunnelSystem.valvesWithoutFlow && this !in openValves

    private fun findValve(id: String): Valve = tunnelSystem.findValve(id)
}


private fun findDoublePaths(path: DoublePath): Set<DoublePath> {
    tailrec fun go(time: Int, acc: Set<DoublePath>): Set<DoublePath> =
        if (time <= LIMIT && acc.all { it.remainingFlow > 0}) {
            if (time == 8) {
                println("time has come")
            }
            val nextCandidates = acc.flatMap { it.next() }.toSet()
            val maxReachableFlow = nextCandidates.maxOf(DoublePath::maxReachableFlow)
//            val maxFlow = nextCandidates.maxOf(DoublePath::flown)
            val nextPaths = nextCandidates.filter {
                time < 8 || it.maxReachableFlow >= maxReachableFlow
            }.toSet()
            println("time: $time, paths: ${nextPaths.size}, removed paths: ${nextCandidates.size - nextPaths.size}")
            go(time + 1, nextPaths)
        } else {
            acc
        }

    return go(3, setOf(path))
}

data class DoublePath(
    private val current: Pair<Valve, Valve>,
    private val tunnelSystem: TunnelSystem,
    private val operations: List<Pair<Operation, Operation>> = emptyList(),
    private val openValves: Set<Valve> = emptySet(),
    private val previous: Pair<Operation, Operation>? = null,
    val flown: Int = 0,
    val ellapsed: Int = 0,
) {
    private val timeLeft = LIMIT - ellapsed
    val remainingFlow: Int by lazy {
        tunnelSystem.remainingValves(openValves).sumOf { valve ->
            val dist = minOf(
                tunnelSystem.distanceBetween(valve, current.first),
                tunnelSystem.distanceBetween(valve, current.second),
            )
            valve.flowRate * (timeLeft - dist)
        }
    }

    val maxReachableFlow: Int = 20 * flown + remainingFlow
    fun next(): Set<DoublePath> =
        possibleOps.map { operations ->
            copy(
                current = operations.newCurrent,
                openValves = openValves + operations.newOpenValves,
                operations = this.operations + operations,
                previous = operations,
                ellapsed = ellapsed + 1,
                flown = flown + (operations.newFlowRate * (LIMIT - ellapsed - 1))
            )
        }.toSet()

    private val Pair<Operation, Operation>.newCurrent: Pair<Valve, Valve>
        get() = Pair(
            first = (first as? GoToValve)?.valve ?: current.first,
            second = (second as? GoToValve)?.valve ?: current.second,
        )

    private val Pair<Operation, Operation>.newFlowRate: Int
        get() = first.newFlowRate + second.newFlowRate

    private val Pair<Operation, Operation>.newOpenValves: Set<Valve>
        get() = setOfNotNull(first.valveToOpen, second.valveToOpen)

    private val possibleOps: Set<Pair<Operation, Operation>>
        get() = possibleOps(current.first, previous?.first).flatMap { firstOp ->
            possibleOps(current.second, previous?.second).filter { it != firstOp }.map { secondOp ->
                Pair(firstOp, secondOp)
            }
        }.toSet()

    private fun possibleOps(valve: Valve, previous: Operation?): List<Operation> =
        valve.leadTo.map { GoToValve(findValve(it)) }.filter { it != previous } +
                if (valve.isClosed) listOf(OpenValve(valve)) else emptyList()

    private val Valve.isClosed: Boolean
        get() = this !in tunnelSystem.valvesWithoutFlow && this !in openValves

    private fun findValve(id: String): Valve = tunnelSystem.findValve(id)
}

data class TunnelSystem(
    private val valves: List<Valve>,
) {
    val valvesWithoutFlow: Set<Valve> = valves.filter { it.flowRate == 0 }.toSet()

    val distances: Map<Valve, Map<Valve, Int>> by lazy(::calculateDistances)

    fun maxRemainingFlow(openValves: Set<Valve>): Int =
        remainingValves(openValves).sumOf { it.flowRate }

    fun remainingValves(openValves: Set<Valve>): List<Valve> =
        valves.filter { it !in openValves }

    fun findValve(id: String): Valve = valves.first { it.id == id }

    fun distanceBetween(one: Valve, other: Valve): Int =
        distances.getOrDefault(one, mapOf()).getOrDefault(other, Int.MAX_VALUE)

    private fun calculateDistances(): Map<Valve, Map<Valve, Int>> =
        valves.associateWith { calculateDistances(it) }

    private fun calculateDistances(valve: Valve): Map<Valve, Int> {
        val otherValves = valves.filter { it != valve }
        tailrec fun go(distance: Int, next: Set<Valve>, acc: Map<Valve, Int>): Map<Valve, Int> =
            if (otherValves.any { it !in acc.keys }) {
                val newValves = next.flatMap { it.leadTo }
                    .map { findValve(it) }
                    .filter { it !in acc.keys && it != valve }.toSet()
                val newMap = acc + newValves.associateWith { distance }
                go(distance + 1, newValves, newMap)
            } else {
                acc
            }

        return go(0, setOf(valve), mapOf())
    }
}

data class Valve(
    val id: String,
    val flowRate: Int,
    val leadTo: Set<String>,
) {
    override fun toString(): String = id
}

sealed interface Operation {
    val valveToOpen: Valve?
    val newFlowRate: Int
}

data class OpenValve(override val valveToOpen: Valve) : Operation {
    override val newFlowRate: Int = valveToOpen.flowRate

    override fun toString(): String = "OpenValve(${valveToOpen.id})"
}

data class GoToValve(val valve: Valve) : Operation {
    override val valveToOpen: Valve? = null
    override val newFlowRate: Int = 0

    override fun toString(): String = "GoToValve(${valve.id})"
}

