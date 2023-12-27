package com.gilpereda.adventsofcode.adventsofcode2021.day12

fun numberOfPaths(input: Sequence<String>): String =
    pathsCount(listOf(createGraph(input).toPath())).toString()

fun numberOfPath2(input: Sequence<String>): String =
    pathsCount(listOf(createGraph(input).toPath(true))).toString()


data class Path(val path: List<Node>, val canRevisitSmallCave: Boolean = false) {
    val isFinished: Boolean
        get() = path.lastOrNull() is End
}

fun Node.toPath(canRevisit: Boolean = false): Path = Path(listOf(this), canRevisit)

tailrec fun pathsCount(paths: List<Path>): Int =
    if (paths.all { it.isFinished }) {
        paths.count()
    } else {
        pathsCount(paths.flatMap { it.newPaths })
    }

val Path.newPaths: List<Path>
    get() = when (val lastNode = this.path.last()) {
        is End -> listOf(this)
        else -> lastNode.neighbours
            .filter { node -> node.canBeVisitedTwice(canRevisitSmallCave) || node !in path }
            .map {
                val newPath = path + it
                val canRevisitSmallCave = canRevisitSmallCave && newPath.hasNotVisitedTwiceASmallCave
                copy(path = newPath, canRevisitSmallCave = canRevisitSmallCave)
            }
    }

val List<Node>.hasNotVisitedTwiceASmallCave: Boolean
    get() = filterIsInstance<SmallCave>().groupBy { it }.all { (_, nodes) -> nodes.size == 1 }

private fun createGraph(input: Sequence<String>): Node =
    input.fold(mapOf<String, Node>()) { nodes, next ->
        val (origStr, destStr) = connection(next)
        val orig = nodes[origStr] ?: Node.from(origStr)
        val dest = (nodes[destStr] ?: Node.from(destStr))
        nodes + mapOf(origStr to orig.addNeighbour(dest), destStr to dest.addNeighbour(orig))
    }["start"]!!


fun connection(pair: String): Pair<String, String> =
    pair.split("-").let { (origin, destination) -> Pair(origin, destination) }


sealed class Node {
    open fun addNeighbour(node: Node): Node {
        _neighbours.add(node)
        return this
    }

    abstract fun canBeVisitedTwice(canRevisitSmallCave: Boolean): Boolean
    abstract val name: String

    val neighbours: Set<Node>
        get() = _neighbours

    private val _neighbours: MutableSet<Node> = mutableSetOf()

    override fun toString(): String = name

    companion object {
        fun from(node: String): Node =
            when {
                node == "start" -> Start()
                node == "end" -> End()
                node.all { it.isLowerCase() } -> SmallCave(node)
                node.all { it.isUpperCase() } -> BigCave(node)
                else -> throw IllegalArgumentException("Can not parse cave name")
            }
    }
}

class End : Node() {
    override fun canBeVisitedTwice(canRevisitSmallCave: Boolean): Boolean = false
    override val name: String = "end"
}

class Start : Node() {
    override fun canBeVisitedTwice(canRevisitSmallCave: Boolean) = false
    override val name: String = "start"
}

data class BigCave(override val name: String) : Node() {
    override fun canBeVisitedTwice(canRevisitSmallCave: Boolean) = true
}

data class SmallCave(override val name: String) : Node() {
    override fun canBeVisitedTwice(canRevisitSmallCave: Boolean) = canRevisitSmallCave
}