package com.gilpereda.aoc2022.day07

val CD_REGEX = "\\\$ cd (.+)".toRegex()
val DIR_OUTPUT_REGEX = "dir (.*)".toRegex()
val FILE_OUTPUT_REGEX = "([0-9]+) .+".toRegex()

fun firstTask(input: Sequence<String>): String {
//    val list = input.map(::parseLine).toList()
    return TODO()
}

fun secondTask(input: Sequence<String>): String = TODO()


fun parseLine(line: String): CmdItem =
    findCd(line) ?: findLs(line) ?: findDirOutput(line) ?: findFileOutput(line) ?: throw Exception()

fun parseCommands(commands: List<CmdItem>): Dir {
    fun go(current: Dir, rest: List<CmdItem>): Dir
}

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
}

data class Dir(
    val name: String,
    val parent: Dir,
    val children: List<FsElem>
) : FsElem {
    override val size: Int
        get() = children.sumOf { it.size }
}

data class File(
    override val size: Int
) : FsElem