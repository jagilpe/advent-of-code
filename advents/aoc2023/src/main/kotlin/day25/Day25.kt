package com.gilpereda.aoc2022.day25

typealias Component = String

fun firstTask(input: Sequence<String>): String {
    val system = input.parsed()
    val groups = system.groups()
    return system.solve().toString()
}

fun secondTask(input: Sequence<String>): String =
    TODO()

private fun Sequence<String>.parsed(): System {
    val initial = map { line ->
        val (one, others) = line.split(": ")
        one to others.split(" ").toSet()
    }.toMap()
    val components = initial.flatMap { (k, v) -> v + k }.toSet()
    val withAllComponents = components
        .associateWith { component ->
            val connections =
                (initial[component] ?: emptySet()) + initial.filter { (_, components) -> component in components }.keys
            connections
        }
    return System(withAllComponents)
}

//private fun Set<Component>.groups(): List<Group> {
//    fun go(rest: Set<Component>, acc: List<Group> = emptyList()): List<Group> =
//        if (rest.isEmpty()) {
//            acc
//        } else {
//            val current = rest.first()
//
//            val group = acc.first {  }
//        }
//
//    return go(this)
//}

class System(
    private val componentToNeighbours: Map<Component, Set<Component>>
) {
    private val components = componentToNeighbours.keys

    fun solve(): Int =
        componentToNeighbours.flatMap { (component, neighbours) ->
            neighbours.map { setOf(component, it) }
        }.toSet()
            .map { pair -> Connection.from(pair) to disconnect(Connection.from(pair)).shortestPath(pair) }
            .sortedByDescending { it.second }
            .take(3)
            .fold(this) { acc, (connection, _) -> acc.disconnect(connection) }
            .groups()
            .fold(1) { acc, group -> acc * group.size}

    operator fun get(component: Component): Set<Component> =
        componentToNeighbours[component] ?: emptySet()

    private fun shortestPath(pair: Set<Component>): Int {
        val (source, destination) = pair.toList()
        fun go(open: Set<String>, visited: Set<String> = emptySet(), acc: Int = 0): Int =
            if (acc > 11) {
                Int.MAX_VALUE
            } else {
                if (destination in open) {
                    acc
                } else {
                    go(open.flatMap { get(it).filter { it !in visited } }.toSet(), visited + open, acc + 1)
                }
            }
        return go(setOf(source))
    }

    private fun disconnectAll(connections: List<Connection>): System =
        connections.fold(this) { acc, connection -> acc.disconnect(connection)}

    private fun disconnect(connection: Connection): System {
        return System(componentToNeighbours.mapValues { (component, connections) ->
            if (component in connection) {
                connections - connection.one -connection.other
            } else {
                connections
            }
        })
    }

    fun groups(): List<Set<Component>> {
        tailrec fun go(
            rest: List<Component>,
            open: List<Component> = emptyList(),
            group: Set<Component> = emptySet(),
            acc: MutableList<Set<Component>> = mutableListOf()
        ): List<Set<Component>> =
            if (rest.isEmpty()) {
                acc.add(group)
                acc.filter { it.isNotEmpty() }
            } else if (open.isEmpty()) {
                acc.add(group)
                go(rest.drop(1), listOf(rest.first()), setOf(rest.first()), acc)
            } else {
                val current = open.first()
                val neighbours = get(current).filter { it in rest }
                go(rest - neighbours, open.drop(1) + neighbours, group + neighbours, acc)
            }

        return go(components.toList())
    }

    private fun Component.isConnectedTo(other: Component): Boolean {
        tailrec fun go(current: Set<Component>, rest: Set<Component>): Boolean =
            if (current.isEmpty()) {
                false
            } else {
                if (current.any { it == other }) {
                    true
                } else {
                    val next = current.mapNotNull { componentToNeighbours[it] }.flatten()
                        .filter { it in rest }.toSet()
                    go(next, rest - next)
                }
            }
        return go(setOf(this), componentToNeighbours.keys)
    }
}

data class Connection(
    val one: Component,
    val other: Component,
) {

    operator fun contains(component: Component): Boolean =
        component == one || component == other

    companion object {
        fun from(components: Set<Component>): Connection {
            val (one, other) = components.toList()
            return Connection(one, other)
        }
    }
}