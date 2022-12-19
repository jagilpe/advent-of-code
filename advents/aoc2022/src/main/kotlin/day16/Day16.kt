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
        elapsed = 3,
    )

    val path = findBestDoublePath(init)
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

private fun findBestDoublePath(path: DoublePath): DoublePath {
    var iter = 0L
    tailrec fun go(state: State): DoublePath {
        if (iter % 2_000 == 0L) {
            println("iter: $iter, open paths: ${state.open.size}, current winner: ${state.currentBest.flown}, current flow: ${state.current.flown}, open valves: ${state.current.openValves.size}")
        }
        iter += 1
        val next = state.next()
        return when {
            next.finished -> state.current
            else -> go(next)
        }
    }

    return go(State(path))
}

data class State(
    val current: DoublePath,
    val currentBest: DoublePath = current.finish(),
    val open: List<DoublePath> = emptyList(),
) {
    val finished: Boolean = current.finished && open.isEmpty()

    fun next(): State =
        nextCandidates.let { (newCandidate, newOpen) ->
            State(
                current = newCandidate,
                currentBest = newBest,
                open = newOpen,
            )
        }

//        return if (!current.finished) {
//
//            if (currentBest canWin currentBest) {
//                val newOpen = open + current.nextPaths()
//                val newCurrent = newOpen.nextCandidates
//                newCurrent?.let { copy(current = it, open = newOpen - it) }
//            } else {
//                copy(current = current.finish())
//            }
//        } else {
//            if (open.isNotEmpty()) {
//                val next = open.maxBy { it.flown / (it.elapsed + 1) }
//                copy(
//                    currentBest = newBest,
//                    current = next,
//                    open = open - next,
//                )
//            } else {
//                null
//            }
//        }

    private val newBest: DoublePath
        get() = if (current.finished && current.flown > currentBest.flown) {
            println("new best found. flown: ${current.flown} in ${current.elapsed}")
            current
        } else {
            currentBest
        }

    private val nextCandidates: Pair<DoublePath, List<DoublePath>>
        get() {
            val newOpen = if (!current.finished && current canWin currentBest) {
                open + current.nextPaths()
            } else {
                open
            }
            val newCandidate = newOpen.maxByOrNull { it.flown } ?: current.finish()
            return newCandidate to (newOpen - newCandidate)
        }

}

data class DoublePath(
    private val current: Pair<Valve, Valve>,
    private val tunnelSystem: TunnelSystem,
    private val operations: List<Pair<Operation, Operation>> = emptyList(),
    val openValves: Set<Valve> = emptySet(),
    private val previous: Pair<Operation, Operation>? = null,
    val flown: Int = 0,
    val elapsed: Int = 0,
) : Comparable<DoublePath> {
    fun finish(): DoublePath = copy(elapsed = LIMIT)

    val finished: Boolean by lazy { elapsed > LIMIT || allValvesOpen }

    infix fun canWin(other: DoublePath): Boolean =
        elapsed < other.elapsed || flown >= other.flown

    private val allValvesOpen: Boolean by lazy {
        tunnelSystem.remainingValves(openValves).isEmpty()
    }

//    private val timeLeft = LIMIT - elapsed
//    val remainingFlow: Int by lazy {
//        tunnelSystem.remainingValves(openValves).sumOf { valve ->
//            val dist = minOf(
//                tunnelSystem.distanceBetween(valve, current.first),
//                tunnelSystem.distanceBetween(valve, current.second),
//            )
//            valve.flowRate * (timeLeft - dist)
//        }
//    }
//
//    val maxReachableFlow: Int = 20 * flown + remainingFlow

    fun nextPaths(): List<DoublePath> =
        possibleOps.map { operations ->
            copy(
                current = operations.newCurrent,
                openValves = openValves + operations.newOpenValves,
                operations = this.operations + operations,
                previous = operations,
                elapsed = elapsed + 1,
                flown = flown + (operations.newFlowRate * (LIMIT - elapsed - 2))
            )
        }

    private val Pair<Operation, Operation>.newCurrent: Pair<Valve, Valve>
        get() = Pair(
            first = (first as? GoToValve)?.valve ?: current.first,
            second = (second as? GoToValve)?.valve ?: current.second,
        )

    private val Pair<Operation, Operation>.newFlowRate: Int
        get() = first.newFlowRate + second.newFlowRate

    private val Pair<Operation, Operation>.newOpenValves: Set<Valve>
        get() = setOfNotNull(first.valveToOpen, second.valveToOpen)

    private val possibleOps: List<Pair<Operation, Operation>>
        get() = possibleOps(current.first, previous?.first).flatMap { firstOp ->
            possibleOps(current.second, previous?.second).filter { it != firstOp }.map { secondOp ->
                Pair(firstOp, secondOp)
            }
        }

    private fun possibleOps(valve: Valve, previous: Operation?): List<Operation> =
        (if (valve.isClosed) listOf(OpenValve(valve)) else emptyList()) +
                valve.leadTo.map { GoToValve(findValve(it)) }.filter { it != previous || valve.leadTo.size == 1 }


    private val Valve.isClosed: Boolean
        get() = this !in tunnelSystem.valvesWithoutFlow && this !in openValves

    private fun findValve(id: String): Valve = tunnelSystem.findValve(id)

    override fun compareTo(other: DoublePath): Int =
        when {
            openValves.size > other.openValves.size -> 1
            openValves.size < other.openValves.size -> -1
            else -> when {
                flown > other.flown -> 1
                flown < other.flown -> -1
                else -> when {
                    elapsed > other.elapsed -> 1
                    elapsed < other.elapsed -> -1
                    else -> 0
                }
            }
        }
}

data class TunnelSystem(
    private val valves: List<Valve>,
) {
    val valvesWithoutFlow: Set<Valve> = valves.filter { it.flowRate == 0 }.toSet()

    val distances: Map<Valve, Map<Valve, Int>> by lazy(::calculateDistances)

    fun maxRemainingFlow(openValves: Set<Valve>): Int =
        remainingValves(openValves).sumOf { it.flowRate }

    fun remainingValves(openValves: Set<Valve>): List<Valve> =
        valves.filter { it !in openValves && it !in valvesWithoutFlow }

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

