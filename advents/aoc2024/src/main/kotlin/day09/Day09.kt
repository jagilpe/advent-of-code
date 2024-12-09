package com.gilpereda.aoc2024.day09

fun firstTask(input: Sequence<String>): String {
    val fileSystem = input.first().parsed()

    tailrec fun go(
        acc: List<DataBlock>,
        rest: List<FileBlock>,
    ): List<DataBlock> =
        if (rest.isEmpty()) {
            acc
        } else {
            when (val first = rest.first()) {
                is DataBlock -> go(acc + first, rest.drop(1))
                is EmptyBlock -> {
                    when (val last = rest.last()) {
                        is DataBlock ->
                            when {
                                first.length == last.length -> go(acc + last, rest.drop(1).dropLast(1))
                                first.length > last.length ->
                                    go(acc + last, listOf(EmptyBlock(last.index, first.length - last.length)) + rest.drop(1).dropLast(1))
                                else -> {
                                    go(
                                        acc + DataBlock(last.index, id = last.id, length = first.length),
                                        rest.drop(1).dropLast(1) + DataBlock(last.index, id = last.id, length = last.length - first.length),
                                    )
                                }
                            }
                        is EmptyBlock -> go(acc, rest.dropLast(1))
                    }
                }
            }
        }

    val result = go(emptyList(), fileSystem)
    return result
        .asSequence()
        .flatMap { item -> List(item.length) { item.id } }
        .mapIndexed { index, item -> index * item }
        .sum()
        .toString()
}

fun secondTask(input: Sequence<String>): String {
    val fileSystem = input.first().parsed()

    val candidates = fileSystem.candidates
    val total = candidates.size
    val result =
        candidates
            .foldIndexed(fileSystem) { index, acc, next ->
                println("$index of $total")
                acc.replace(next)
            }
    return result
        .asSequence()
        .flatMap { item ->
            List(item.length) {
                when (item) {
                    is DataBlock -> item.id
                    is EmptyBlock -> 0L
                }
            }
        }.mapIndexed { index, item -> index * item }
        .sum()
        .toString()
}

private fun List<FileBlock>.replace(dataBlock: DataBlock): List<FileBlock> =
    firstOrNull { it is EmptyBlock && it.length >= dataBlock.length && it.index < dataBlock.index }
        ?.let { toBeReplaced ->
            flatMap { block ->
                when (block) {
                    toBeReplaced ->
                        when (val rest = toBeReplaced.length - dataBlock.length) {
                            0 -> listOf(dataBlock)
                            else -> listOf(dataBlock, EmptyBlock(index = toBeReplaced.index, length = rest))
                        }
                    dataBlock -> listOf(EmptyBlock(index = dataBlock.index, length = dataBlock.length))
                    else -> listOf(block)
                }
            }.mergeEmpties()
        } ?: this

private fun List<FileBlock>.mergeEmpties(): List<FileBlock> {
    tailrec fun go(
        acc: List<FileBlock>,
        rest: List<FileBlock>,
        last: EmptyBlock? = null,
    ): List<FileBlock> =
        if (rest.isEmpty()) {
            acc
        } else {
            when (val first = rest.first()) {
                is DataBlock -> {
                    val nextAcc =
                        if (last != null) {
                            acc + last + first
                        } else {
                            acc + first
                        }
                    go(nextAcc, rest.drop(1))
                }
                is EmptyBlock -> {
                    if (last != null) {
                        go(acc, rest.drop(1), EmptyBlock(index = last.index, length = last.length + first.length))
                    } else {
                        go(acc, rest.drop(1), first)
                    }
                }
            }
        }
    return go(emptyList(), this)
}

private val List<FileBlock>.candidates: List<DataBlock>
    get() =
        filterIsInstance<DataBlock>().reversed()

private fun String.parsed(): List<FileBlock> =
    mapIndexed { index, c ->
        if (index % 2 == 1) {
            EmptyBlock(index = index, length = "$c".toInt())
        } else {
            DataBlock(index = index, id = (index / 2).toLong(), length = "$c".toInt())
        }
    }

sealed interface FileBlock {
    val index: Int
    val length: Int
}

data class DataBlock(
    override val index: Int,
    val id: Long,
    override val length: Int,
) : FileBlock

data class EmptyBlock(
    override val index: Int,
    override val length: Int,
) : FileBlock
