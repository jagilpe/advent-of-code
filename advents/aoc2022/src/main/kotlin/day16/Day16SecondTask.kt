package com.gilpereda.aoc2022.day16

import java.util.SortedSet


/**
 * SECOND TASK
 */
fun secondTask(input: Sequence<String>): String {
    val tunnelSystem = TunnelSystem(input.parsed())
    val route = DoublePathFinder(tunnelSystem).findBestRoute()

    return route.flown.toString()
}

typealias Position = Pair<Valve, Valve>

class DoublePathFinder(
    private val tunnelSystem: TunnelSystem,
) {
    private val limit: Int = 26
    private val openRoutes: SortedSet<Route> = sortedSetOf()

    private fun initial(): Route =
        tunnelSystem.findValve("AA")
            .let { start ->
                Route(
                    position = Pair(start, start),
                    valvesToOpen = tunnelSystem.openableValves,
                    path = listOf(Pair(start.desc(), start.desc())),
                )
            }

    fun findBestRoute(): Route {
        openRoutes.clear()
        var iter = 0L
        fun go(current: Route, currentBest: Route?): Route {
            if (iter % 2_000 == 0L) {
                println("iter: $iter, open paths: ${openRoutes.size}, current winner: ${currentBest?.flown ?: 0}, current flow: ${current.flown}, valves to open: ${current.valvesToOpen.size}")
            }
            iter += 1
            val (next, nextBest) = update(current, currentBest)
            return when (next) {
                null -> nextBest ?: throw Exception("Could not find best route")
                else -> go(next, nextBest)
            }
        }

        return go(initial(), null)
    }

    private fun update(current: Route, currentBest: Route?): Pair<Route?, Route?> {
        val nextBest = if (current.finished && (currentBest == null || current.flown > currentBest.flown)) {
            current
        } else {
            currentBest
        }

        openRoutes.addAll(newRoutes(current, currentBest))

        val nextRoute = openRoutes.firstOrNull()?.also { openRoutes.remove(it) }

        return Pair(nextRoute, nextBest)

    }

    private fun newRoutes(route: Route, currentBest: Route?): List<Route> =
        if (route.finished || route cannotWin currentBest) {
            emptyList()
        } else {
            route.nextRoutes
        }

    inner class Route(
        private val position: Position,
        val valvesToOpen: Set<Valve>,
        private val path: List<Pair<String, String>>,
        private val previous: Position = position,
        val flown: Int = 0,
        private val elapsed: Int = 0,
    ) : Comparable<Route> {
        val finished: Boolean = valvesToOpen.isEmpty() || elapsed >= limit

        infix fun cannotWin(other: Route?): Boolean =
            other != null && elapsed > other.elapsed && flown < other.flown

        val nextRoutes: List<Route>
            get() = possibleOperations.map { operation ->
                val newPosition = operation.newPosition
                Route(
                    position = newPosition,
                    valvesToOpen = valvesToOpen - operation.openedValves,
                    path = path + newPosition.description,
                    previous = position,
                    elapsed = elapsed + 1,
                    flown = flown + (operation.newFlowRate * (LIMIT - elapsed - 2))
                )
            }

        private val Position.description: Pair<String, String>
            get() = Pair(
                first.desc(first !in valvesToOpen),
                second.desc(second !in valvesToOpen),
            )

        private val possibleOperations: List<Pair<Operation, Operation>>
            get() =
                possibleOps(position.first, previous.first).flatMap { firstOp ->
                    possibleOps(position.second, previous.second)
                        .filter { it != firstOp }
                        .map { secondOp ->
                            setOf(firstOp, secondOp)
                    }
                }.distinct().map {
                    val (first, second) = it.toList()
                    Pair(first, second)
                }

        private fun possibleOps(valve: Valve, previous: Valve): List<Operation> =
            (if (valve !in valvesToOpen) listOf(OpenValve(valve)) else emptyList()) +
                    valve.leadTo
                        .map { tunnelSystem.findValve(it)}
                        .filter { it != previous || valve.leadTo.size == 1 }
                        .map(::GoToValve)


        private val Pair<Operation, Operation>.newPosition: Pair<Valve, Valve>
            get() = Pair(
                first = (first as? GoToValve)?.valve ?: position.first,
                second = (second as? GoToValve)?.valve ?: position.second,
            )

        private val Pair<Operation, Operation>.newFlowRate: Int
            get() = first.newFlowRate + second.newFlowRate

        private val Pair<Operation, Operation>.openedValves: Set<Valve>
            get() = setOfNotNull(first.valveToOpen, second.valveToOpen)

        override fun compareTo(other: Route): Int =
            when {
                flown > other.flown -> -1
                flown < other.flown -> 1
                else -> 0
            }
    }
}


fun Valve.desc(open: Boolean = false): String {
    val status = when {
        !canOpen -> "-"
        open -> "O"
        else -> "C"
    }
    return "$id($status)"
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

private const val LIMIT = 30

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
