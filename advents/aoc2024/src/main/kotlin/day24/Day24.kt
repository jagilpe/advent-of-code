package com.gilpereda.aoc2024.day24

import kotlin.math.pow

/**
 * 18446744073709551615 is not the answer
 */
fun firstTask(input: Sequence<String>): String {
    val (wiresString, instructionsString) = input.joinToString("\n").split("\n\n")
    val wireToInitialValue =
        wiresString
            .split("\n")
            .map { it.split(": ") }
            .associate { (gate, value) -> gate to value.toInt() }
            .toMutableMap()

    val instructions =
        instructionsString.split("\n").associate {
            val (instruction, output) = it.split(" -> ")
            val (input1, operation, input2) = instruction.split(" ")

            output to
                Instruction(
                    input1 = input1,
                    input2 = input2,
                    operation = Operation.valueOf(operation),
                )
        }

    val result =
        instructions
            .filterKeys { it.startsWith("z") }
            .entries
            .sortedByDescending { it.key }
            .map { it.value.calculateOutput(wireToInitialValue, instructions) }
            .joinToString("")

    return result
        .binaryToLong()
        .toString()
}

fun String.binaryToLong(): Long =
    reversed()
        .mapIndexed { i, digit -> "$digit".toLong() * 2.0.pow(i).toLong() }
        .sum()

fun secondTask(input: Sequence<String>): String {
    val (_, instructionsString) = input.joinToString("\n").split("\n\n")
    val instructions: Map<String, Instruction> =
        instructionsString.split("\n").associate {
            val (instruction, output) = it.split(" -> ")
            val (input1, operation, input2) = instruction.split(" ")

            output to
                Instruction(
                    input1 = input1,
                    input2 = input2,
                    operation = Operation.valueOf(operation),
                )
        }

    val wiresToSwap =
        listOf<Pair<String, String>>(
            "z12" to "fgc",
            "z29" to "mtj",
            "dgr" to "vvm",
            "z37" to "dtv",
        )

    val result = wiresToSwap.flatMap { listOf(it.first, it.second) }.sorted().joinToString(",")

    return if (isCorrect(instructions.swap(wiresToSwap))) {
        result
    } else {
        throw IllegalStateException("Not fixed yet")
    }
}

private fun isCorrect(instructions: Map<String, Instruction>): Boolean {
    val outputs = instructions.keys.filter { it.startsWith("z") }.sorted()

    tailrec fun go(
        open: List<String>,
        acc: Map<String, Set<String>>,
    ): Boolean =
        if (open.isEmpty()) {
            true
        } else {
            val current = open.first()
            val instruction = instructions[current]!!
            if (current == "z00") {
                if (instruction.operation == Operation.XOR && instruction.operands.containsAll(listOf("x00", "y00"))) {
                    go(open.drop(1), acc)
                } else {
                    false
                }
            } else {
                if (instruction.operation != Operation.XOR) {
                    println("Operation with output $current should be XOR")
                    false
                } else {
                    val xorOperand = instructions.findXorOperation(current, instruction)
                    if (xorOperand == null) {
                        println("Could not find xor instruction for $current")
                        false
                    } else {
                        val orInstruction = instructions[instruction.other(xorOperand)!!]
                        if (orInstruction == null) {
                            println("Could not find or operand for $current")
                            false
                        } else {
                            if (current == "z01") {
                                go(open.drop(1), acc + (current to instruction.operands))
                            } else {
                                if (orInstruction.operation != Operation.OR) {
                                    println("Operation with output $orInstruction should be OR")
                                    false
                                } else {
                                    val andPreviousOperand =
                                        instructions.findPreviousAndOperation(current, orInstruction)
                                    if (andPreviousOperand == null) {
                                        println("Could not find previous and operand for $current")
                                        false
                                    } else {
                                        val andInstruction = instructions[orInstruction.other(andPreviousOperand)!!]
                                        if (andInstruction == null) {
                                            println("Could not find and operand for $current")
                                            false
                                        } else {
                                            val previousXorOperands = acc[current.previous]!!
                                            if (andInstruction.operands.containsAll(previousXorOperands)) {
                                                go(open.drop(1), acc + (current to instruction.operands))
                                            } else {
                                                println(
                                                    "Operands for and operation for $current should be $previousXorOperands but are ${andInstruction.operands}",
                                                )
                                                false
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    return go(outputs, mapOf())
}

private fun Map<String, Instruction>.swap(wiresToSwap: List<Pair<String, String>>): Map<String, Instruction> =
    this +
        wiresToSwap
            .flatMap { (one, other) ->
                listOfNotNull(this[other]?.let { one to it }, this[one]?.let { other to it })
            }.toMap()

private fun Map<String, Instruction>.findXorOperation(
    output: String,
    instruction: Instruction,
): String? =
    instruction.operands
        .mapNotNull { operand -> get(operand)?.let { operand to it } }
        .firstOrNull { it.second.isExpectedXorFor(output) }
        ?.first

private fun Map<String, Instruction>.findPreviousAndOperation(
    output: String,
    instruction: Instruction,
): String? =
    instruction.operands
        .mapNotNull { operand -> get(operand)?.let { operand to it } }
        .firstOrNull { it.second.isExpectedAndFor(output) }
        ?.first

private fun String.previousDigits(): Set<String> {
    val previous = previous
    return setOf(previous.replace("z", "x"), previous.replace("z", "y"))
}

private val String.previous: String
    get() =
        when (val outputNumber = removePrefix("z").toInt()) {
            in 0..10 -> "z0${outputNumber - 1}"
            else -> "z${outputNumber - 1}"
        }

class Instruction(
    val input1: String,
    val input2: String,
    val operation: Operation,
) {
    val operands: Set<String> = setOf(input1, input2)

    fun other(operand: String): String? =
        when (operand) {
            input1 -> input2
            input2 -> input1
            else -> null
        }

    fun isExpectedXorFor(output: String): Boolean {
        val expectedInputs = setOf(output.replace("z", "y"), output.replace("z", "x"))
        return expectedInputs.containsAll(expectedInputs) && operation == Operation.XOR
    }

    fun isExpectedAndFor(output: String): Boolean = operands.containsAll(output.previousDigits())

    fun calculateOutput(
        wireToInitialValue: Map<String, Int>,
        instructions: Map<String, Instruction>,
    ): Int {
        val input1Value = wireToInitialValue[input1] ?: instructions[input1]!!.calculateOutput(wireToInitialValue, instructions)
        val input2Value = wireToInitialValue[input2] ?: instructions[input2]!!.calculateOutput(wireToInitialValue, instructions)

        return operation.calculate(input1Value, input2Value)
    }
}

enum class Operation {
    AND,
    XOR,
    OR,
    ;

    fun calculate(
        one: Int,
        other: Int,
    ): Int =
        when (this) {
            AND -> one and other
            XOR -> one xor other
            OR -> one or other
        }
}
