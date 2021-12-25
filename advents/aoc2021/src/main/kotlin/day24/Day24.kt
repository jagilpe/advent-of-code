package com.gilpereda.adventsofcode.adventsofcode2021.day24

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.gilpereda.adventsofcode.adventsofcode2021.Executable


val part1: Executable = { input ->
    val program = input.map(::parseLine).toList()
    generateSequence(99999999999999L) { it - 1 }
        .takeWhile { it >= 11111111111111L }
        .map {
            if (it % 1_000_000 == 0L) println(it)
            "$it".toList()
        }
        .filter { '0' !in it }
        .map { model -> model to program.result(Alu(input = model.map { "$it".toLong() })) }
        .first { (_, alu) -> alu.z == 0L }.first.joinToString("")
}

val part2: Executable = { TODO() }

val inpRegex = "inp ([xyzw])".toRegex()
val biOperandRegex = "(inp|add|mul|div|mod|eql) ([xyzw])(?: ([\\-0-9xyzw]+))?".toRegex()
fun parseLine(line: String): Instruction =
    inpRegex.find(line)?.destructured
        ?.let { (a) ->
            Inp.inp(Register.valueOf(a.uppercase()))
        }
        ?: biOperandRegex.find(line)?.destructured
            ?.let { (command, a, b) ->
                val regA = Register.valueOf(a.uppercase())
                val valueB = b.toLongOrNull()?.right() ?: Register.valueOf(b.uppercase()).left()
                when (command) {
                    "add" -> Add(regA, valueB)
                    "mul" -> Mul(regA, valueB)
                    "div" -> Div(regA, valueB)
                    "mod" -> Mod(regA, valueB)
                    "eql" -> Eql(regA, valueB)
                    else -> throw Exception("Could not parse line $line")
                }
            } ?: throw Exception("Could not parse line $line")



enum class Register(val get: (Alu) -> Long, val set: (Alu, Long) -> Alu) {
    X(get = { alu -> alu.x }, set = { alu, value -> alu.copy(x = value) }),
    Y(get = { alu -> alu.y }, set = { alu, value -> alu.copy(y = value) }),
    Z(get = { alu -> alu.z }, set = { alu, value -> alu.copy(z = value) }),
    W(get = { alu -> alu.w }, set = { alu, value -> alu.copy(w = value) })
}

data class Alu(val x: Long = 0, val y: Long = 0, val z: Long = 0, val w: Long = 0, val input: List<Long>)

fun List<Instruction>.result(alu: Alu): Alu =
    fold(alu) { acc, instruction -> instruction.next(acc) }

sealed interface Instruction {
    fun next(alu: Alu): Alu

}

data class Inp(val a: Register) : Instruction {
    override fun next(alu: Alu): Alu {
        val value = alu.input.first()
        return a.set(alu.copy(input = alu.input.drop(1)), value)
    }

    companion object {
        fun inp(a: Register): Instruction = Inp(a)
    }
}

data class Add(val a: Register, val b: Either<Register, Long>) : Instruction {
    override fun next(alu: Alu): Alu  = a.set(alu, a.get(alu) + b.fold({ it.get(alu) }, { it }))

    companion object {
        fun add(a: Register, value: Long): Instruction = Add(a, value.right())
        fun add(a: Register, b: Register): Instruction = Add(a, b.left())
    }
}

data class Mul(val a: Register, val b: Either<Register, Long>) : Instruction {
    override fun next(alu: Alu): Alu  = a.set(alu, a.get(alu) * b.fold({ it.get(alu) }, { it }))

    companion object {
        fun mul(a: Register, value: Long): Instruction = Mul(a, value.right())
        fun mul(a: Register, b: Register): Instruction = Mul(a, b.left())
    }
}

data class Div(val a: Register, val b: Either<Register, Long>) : Instruction {
    override fun next(alu: Alu): Alu {
        val bValue = b.fold({ it.get(alu) }, { it })
        if (bValue == 0L) throw ArithmeticException("Can not divide by 0")
        return a.set(alu, a.get(alu) / b.fold({ it.get(alu) }, { it }))
    }

    companion object {
        fun div(a: Register, value: Long): Instruction = Div(a, value.right())
        fun div(a: Register, b: Register): Instruction = Div(a, b.left())
    }
}

data class Mod(val a: Register, val b: Either<Register, Long>) : Instruction {
    override fun next(alu: Alu): Alu {
        val aValue = a.get(alu)
        if (aValue < 0) throw ArithmeticException("Invalid value for a in mod: $aValue")
        val bValue = b.fold({ it.get(alu) }, { it })
        if (bValue <= 0) throw ArithmeticException("Invalid value for b in mod: $bValue")
        return a.set(alu, aValue % bValue)
    }

    companion object {
        fun mod(a: Register, value: Long): Instruction = Mod(a, value.right())
        fun mod(a: Register, b: Register): Instruction = Mod(a, b.left())
    }
}

data class Eql(val a: Register, val b: Either<Register, Long>) : Instruction {
    override fun next(alu: Alu): Alu = a.set(alu, if (a.get(alu) == b.fold({ it.get(alu) }, { it })) 1 else 0)

    companion object {
        fun eql(a: Register, value: Long): Instruction = Eql(a, value.right())
        fun eql(a: Register, b: Register): Instruction = Eql(a, b.left())
    }
}

