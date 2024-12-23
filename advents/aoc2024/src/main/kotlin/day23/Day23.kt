package com.gilpereda.aoc2024.day23

typealias Connection = List<String>

fun firstTask(input: Sequence<String>): String {
    val lanParty = LanParty()
    input.forEach(lanParty::addConnection)

    return lanParty
        .threeComputerConnections()
        .count { it.any { it.startsWith("t") } }
        .toString()
}

fun secondTask(input: Sequence<String>): String {
    val lanParty = LanParty()
    input.forEach(lanParty::addConnection)

    return lanParty.password()
}

class LanParty {
    private val computers = mutableSetOf<String>()
    private val connections = mutableSetOf<Connection>()
    val groups = mutableSetOf<Group>()

    fun addConnection(line: String) {
        val (computer1, computer2) = line.split("-")
        computers.add(computer1)
        computers.add(computer2)
        val connection = listOf(computer1, computer2)
        connections.add(connection)
    }

    fun threeComputerConnections(): Set<Set<String>> =
        connections
            .flatMap { (computer1, computer2) ->
                computers
                    .filter { it isConnectedWith computer1 && it isConnectedWith computer2 }
                    .map { setOf(computer1, computer2, it) }
                    .filter { it.size == 3 }
            }.toSet()

    fun password(): String {
        connections.forEach { connection ->
            val matchingGroups = groups.filter { it.belongsToGroup(connection) }
            if (matchingGroups.isNotEmpty()) {
                matchingGroups
                    .forEach { it.addConnection(connection) }
            } else {
                groups.add(Group(connection))
            }
        }
        return groups
            .maxBy { it.size }
            .computers
            .sorted()
            .joinToString(",")
    }

    private infix fun String.isConnectedWith(computer: String): Boolean =
        connections.any { connection -> connection.contains(computer) && connection.contains(this) }

    inner class Group(
        initial: Connection,
    ) {
        private val connections = mutableSetOf(initial)

        val size: Int
            get() = connections.size

        val computers: Set<String>
            get() = connections.flatten().toSet()

        fun belongsToGroup(connection: Connection): Boolean =
            connections.all { (other1, other2) ->
                val (this1, this2) = connection
                this1.isConnectedWith(other1) &&
                    this1.isConnectedWith(other2) &&
                    this2.isConnectedWith(other1) &&
                    this2.isConnectedWith(other2)
            }

        fun addConnection(connection: Connection) {
            connections.add(connection)
        }
    }
}
