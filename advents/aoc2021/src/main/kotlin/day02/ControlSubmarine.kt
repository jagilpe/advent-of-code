package com.gilpereda.adventsofcode.adventsofcode2021.day02

fun moveSubmarine(commands: Sequence<String>): String =
    commands.map { Command.parse(it) }
        .fold(Submarine()) { acc, command -> command.apply(acc) }
        .result

fun aimAndMoveSubmarine(commands: Sequence<String>): String =
    commands.map { Command.parse(it) }
        .fold(Submarine()) { acc, command -> command.applyWithAim(acc) }
        .result

val commandRegex = "(forward|down|up)\\s(\\d+)".toRegex()

sealed interface Command {
    fun apply(submarine: Submarine): Submarine
    fun applyWithAim(submarine: Submarine): Submarine

    companion object {
        fun parse(command: String): Command =
            commandRegex.find(command)?.destructured?.let { (cmdStr, value) ->
                when (cmdStr) {
                    "forward" -> Forward(value.toInt())
                    "down" -> Down(value.toInt())
                    "up" -> Up(value.toInt())
                    else -> null
                }
            } ?: throw IllegalArgumentException()
    }
}

data class Forward(val steps: Int) : Command {
    override fun apply(submarine: Submarine): Submarine = submarine.copy(distance = submarine.distance + steps)
    override fun applyWithAim(submarine: Submarine): Submarine = submarine.copy(distance = submarine.distance + steps, depth = submarine.depth + (steps * submarine.aim))
}

data class Down(val steps: Int) : Command {
    override fun apply(submarine: Submarine): Submarine = submarine.copy(depth = submarine.depth + steps)
    override fun applyWithAim(submarine: Submarine): Submarine = submarine.copy(aim = submarine.aim + steps)
}

data class Up(val steps: Int) : Command {
    override fun apply(submarine: Submarine): Submarine = submarine.copy(depth = submarine.depth - steps)
    override fun applyWithAim(submarine: Submarine): Submarine = submarine.copy(aim = submarine.aim - steps)
}

data class Submarine(val distance: Int = 0, val depth: Int = 0, val aim: Int = 0) {
    val result: String = (distance * depth).toString()
}