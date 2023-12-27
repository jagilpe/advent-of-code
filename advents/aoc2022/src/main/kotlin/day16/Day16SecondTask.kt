package com.gilpereda.aoc2022.day16


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

    private fun initial(): Route =
        tunnelSystem.findValve("AA")
            .let { start ->
                Route(
                    position = Pair(start, start),
                    valvesToOpen = tunnelSystem.openableValves.sortedByDescending { it.flowRate },
                    path = listOf(Pair(start.toString(), start.toString())),
                )
            }

    fun findBestRoute(): Route {
        var iter = 0L
        tailrec fun go(current: Route, currentBest: Route?, openRoutes: MutableCollection<Route>): Route {
            if (iter % 200_000L == 0L) {
                println("iter: $iter, open paths: ${openRoutes.size}, current winner: ${currentBest?.flown ?: 0}, current flow: ${current.flown}, valves to open: ${current.valvesToOpen.size}")
            }
            iter += 1

            val nextBest = if (current.finished && (currentBest == null || current.flown > currentBest.flown)) {
                openRoutes.removeIf { it cannotWin current}
                current
            } else {
                currentBest
            }

            openRoutes.addAll(newRoutes(current, currentBest))

            return when (val nextRoute = openRoutes.maxByOrNull { it.flown }?.also { openRoutes.remove(it) }) {
                null -> nextBest ?: throw Exception("Could not find best route")
                else -> go(nextRoute, nextBest, openRoutes)
            }
        }

        return go(initial(), null, mutableListOf())
    }

    private fun newRoutes(route: Route, currentBest: Route?): List<Route> =
        if (route.finished || route cannotWin currentBest) {
            emptyList()
        } else {
            route.nextRoutes
        }

    inner class Route(
        private val position: Position,
        val valvesToOpen: List<Valve>,
        private val path: List<Pair<String, String>>,
        private val previous: Position = position,
        private val openValves: Map<Valve, Int> = mapOf(),
        private val elapsed: Int = 1,
    ) : Comparable<Route> {
        val finished: Boolean = valvesToOpen.isEmpty() || elapsed >= limit

        val flown: Int by lazy { flownUntil(limit) }

        private fun flownUntil(to: Int) =
            openValves.map { (valve, openSince) -> valve.flowRate * (to - openSince) }.sum()

        private val maxPotentialFlow: Int by lazy {
            flown + valvesToOpen.sumOf {
                val minDistanceToValve = minOf(tunnelSystem.distanceBetween(position.first, it), tunnelSystem.distanceBetween(position.second, it))
                it.flowRate * (limit - elapsed - minDistanceToValve)
            }
        }

        infix fun cannotWin(other: Route?): Boolean =
            other != null && maxPotentialFlow < other.flown

        val nextRoutes: List<Route>
            get() = possibleOperations.map { operation ->
                val newPosition = operation.newPosition
                Route(
                    position = newPosition,
                    valvesToOpen = valvesToOpen - operation.openedValves,
                    path = path + operation.description,
                    previous = position,
                    elapsed = elapsed + 1,
                    openValves = openValves + operation.openedValves.associateWith { elapsed }
                )
            }

        private val Pair<Operation, Operation>.description: Pair<String, String>
            get() = Pair(
                first.desc(),
                second.desc(),
            )

        private val possibleOperations: List<Pair<Operation, Operation>>
            get() = possibleOps(position.first, previous.first)
                .flatMap { firstOp ->
                    possibleOps(position.second, previous.second)
                        .filter { it != firstOp }
                        .map { secondOp ->
                            setOf(firstOp, secondOp)
                        }
                }.distinct().map {
                val (first, second) = it.toList()
                    Pair(first, second)
                }

        private val highestPriorityValves: List<Valve> = valvesToOpen.take(3)

        private fun possibleOps(valve: Valve, previous: Valve): List<Operation> {
            val openOperation = if (valve in valvesToOpen) listOf(OpenValve(valve)) else emptyList()
            val moveOperations = valve.leadTo
                .map { tunnelSystem.findValve(it)}
                .filter { it != previous || valve.leadTo.size == 1 }
                .map(::GoToValve)

            return if (valve in highestPriorityValves) {
                openOperation + moveOperations
            } else {
                moveOperations + openOperation
            }
        }

        private val Pair<Operation, Operation>.newPosition: Pair<Valve, Valve>
            get() = Pair(
                first = (first as? GoToValve)?.valve ?: position.first,
                second = (second as? GoToValve)?.valve ?: position.second,
            )

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

fun Operation.desc(): String =
    when (this) {
        is GoToValve -> "Goto $valve"
        is OpenValve -> "Open $valveToOpen"
    }
