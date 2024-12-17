package com.gilpereda.aoc2024.day17

import kotlin.math.pow

typealias Program = Map<Int, Operation>

fun firstTask(input: Sequence<String>): String {
    val (regA, regB, regC, _, programStr) = input.toList()

    val computer =
        Computer(
            regA.removePrefix("Register A: ").toLong(),
            regB.removePrefix("Register B: ").toLong(),
            regC.removePrefix("Register C: ").toLong(),
        )
    val program = programStr.parsed()
    return computer.runProgram(program).output.joinToString(",")
}

fun secondTask(input: Sequence<String>): String {
    val (_, regBStr, regCStr, _, programStr) = input.toList()

    val start = System.currentTimeMillis()
    val program = programStr.parsed()
    println(program)
    val expected = programStr.removePrefix("Program: ")
    var regA = -1L
    val regB = regBStr.removePrefix("Register B: ").toLong()
    val regC = regCStr.removePrefix("Register C: ").toLong()

    do {
        regA++
        val result = Computer(regA, regB, regC).runProgram(program).outputString
        if (expected.endsWith(result)) {
            println("$regA $result")
            if (expected == result) break
            regA =
                if (regA > 6175748441) {
                    (regA * 8) - 1_000_000
                } else {
                    (regA * 8)
                }
        }
    } while (true)
    return regA.toString()
}

fun String.parsed(): Program =
    removePrefix("Program: ")
        .split(",")
        .chunked(2)
        .mapIndexed(Operation::from)
        .toMap()

class Computer(
    var registerA: Long,
    var registerB: Long,
    var registerC: Long,
) {
    var pointer: Int = 0
    var output: MutableList<Int> = mutableListOf()

    override fun toString(): String =
        "register A: $registerA register B: $registerB register C: $registerC pointer: $pointer output: $output"

    val outputString: String
        get() = output.joinToString(",")

    fun runProgram(program: Program): Computer {
        var operation = program[pointer]
        while (operation != null) {
            operation.run(this)
            operation = program[pointer]
        }
        return this
    }

    fun next() {
        pointer += 2
    }

    fun jumpTo(instruction: Int) {
        pointer = instruction
    }

    fun comboOperand(operand: Int): Long =
        when (operand) {
            0, 1, 2, 3 -> operand.toLong()
            4 -> registerA
            5 -> registerB
            6 -> registerC
            else -> throw IllegalArgumentException("Invalid operand $operand")
        }
}

sealed interface Operation {
    val operand: Int

    fun run(computer: Computer)

    companion object {
        fun from(
            index: Int,
            list: List<String>,
        ): Pair<Int, Operation> =
            Pair(
                index * 2,
                when (list.first()) {
                    "0" -> Adv(list[1].toInt())
                    "1" -> Bxl(list[1].toInt())
                    "2" -> Bst(list[1].toInt())
                    "3" -> Jnz(list[1].toInt())
                    "4" -> Bxc
                    "5" -> Out(list[1].toInt())
                    "6" -> Bdv(list[1].toInt())
                    "7" -> Cdv(list[1].toInt())
                    else -> throw IllegalArgumentException()
                },
            )
    }
}

data class Adv(
    override val operand: Int,
) : Operation {
    override fun run(computer: Computer) {
        computer.registerA /= 2.0.pow(computer.comboOperand(operand)).toInt()
        computer.next()
    }
}

data class Bxl(
    override val operand: Int,
) : Operation {
    override fun run(computer: Computer) {
        computer.registerB = computer.registerB xor operand.toLong()
        computer.next()
    }
}

data class Bst(
    override val operand: Int,
) : Operation {
    override fun run(computer: Computer) {
        computer.registerB = computer.comboOperand(operand) % 8
        computer.next()
    }
}

data class Jnz(
    override val operand: Int,
) : Operation {
    override fun run(computer: Computer) {
        if (computer.registerA != 0L) {
            computer.jumpTo(operand.toInt())
        } else {
            computer.next()
        }
    }
}

data object Bxc : Operation {
    override val operand: Int = 0

    override fun run(computer: Computer) {
        computer.registerB = computer.registerB xor computer.registerC
        computer.next()
    }
}

data class Out(
    override val operand: Int,
) : Operation {
    override fun run(computer: Computer) {
        computer.output.add((computer.comboOperand(operand) % 8).toInt())
        computer.next()
    }
}

data class Bdv(
    override val operand: Int,
) : Operation {
    override fun run(computer: Computer) {
        computer.registerB = computer.registerA / 2.0.pow(computer.comboOperand(operand)).toInt()
        computer.next()
    }
}

data class Cdv(
    override val operand: Int,
) : Operation {
    override fun run(computer: Computer) {
        computer.registerC = computer.registerA / 2.0.pow(computer.comboOperand(operand)).toInt()
        computer.next()
    }
}

private fun Double.pow(value: Long): Long = toDouble().pow(value.toDouble()).toLong()
