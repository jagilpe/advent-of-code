package com.gilpereda.aoc2022.day07

val CD_REGEX = "\\\$ cd (.+)".toRegex()
val DIR_OUTPUT_REGEX = "dir (.*)".toRegex()
val FILE_OUTPUT_REGEX = "([0-9]+) .+".toRegex()

fun firstTask(input: Sequence<String>): String {
    val root = getFs(input)
    return root.dirs.map { it.size }.filter { it <= 100000 }.sum().toString()
}

fun secondTask(input: Sequence<String>): String {
    val root = getFs(input)
    val totalSize = root.size
    val toFreeUp = totalSize - 30_000_000
    val toDelete = root.dirs.map { it.size }
    return toDelete.filter { it >= toFreeUp }.min().toString()
}

fun getFs(input: Sequence<String>): Dir {
    val root = Dir("/")
    var current = root
    input.toList().filter { it.isNotBlank() }.map(::parseLine).forEach { cmd ->
        when (cmd) {
            is Cd -> {
                try {
                    current = if (cmd.dir == "..") {
                        current.parent!!
                    } else {
                        current.children.filterIsInstance<Dir>().first {
                            it.name == cmd.dir
                        }
                    }
                } catch (ex: Exception) {
                    throw ex
                }
            }

            is Ls -> {}
            is DirOutput -> current.children.add(Dir(cmd.dir, parent = current))
            is FileOutput -> current.children.add(File(cmd.size))
        }
    }
    return root
}


fun parseLine(line: String): CmdItem =
    findCd(line) ?: findLs(line) ?: findDirOutput(line) ?: findFileOutput(line) ?: throw Exception(line)


fun findCd(line: String): Cd? = CD_REGEX.find(line)
    ?.destructured?.let { (dir) -> Cd(dir) }

fun findLs(line: String): Ls? = if (line == "\$ ls") Ls else null

fun findDirOutput(line: String): DirOutput? =
    DIR_OUTPUT_REGEX.find(line)?.destructured?.let { (dir) -> DirOutput(dir) }

fun findFileOutput(line: String): FileOutput? =
    FILE_OUTPUT_REGEX.find(line)?.destructured?.let { (size) -> FileOutput(size.toInt()) }


sealed interface CmdItem

data class Cd(
    val dir: String
) : CmdItem

object Ls : CmdItem

sealed interface LsOutput : CmdItem

data class DirOutput(
    val dir: String
) : LsOutput

data class FileOutput(
    val size: Int
) : LsOutput


sealed interface FsElem {
    val size: Int
    fun print(indent: String = ""): String
}

data class Dir(
    val name: String,
    val parent: Dir? = null,
    val children: MutableList<FsElem> = mutableListOf()
) : FsElem {
    override val size: Int
        get() = children.sumOf { it.size }

    val dirs: List<Dir>
        get() = children.filterIsInstance<Dir>().flatMap { it.dirs + it } + this

    override fun print(indent: String): String =
"""$indent|$name - $size
${children.joinToString("\n") { it.print("$indent  ") }}"""
}

data class File(
    override val size: Int
) : FsElem {
    override fun print(indent: String): String =
        "$indent|f: $size"
}
