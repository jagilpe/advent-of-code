package com.gilpereda.aoc2022.day20

import com.gilpereda.adventofcode.commons.math.leastCommonMultiple

fun firstTask(input: Sequence<String>): String {
    val modules = input.toList().parsed()
    val broadcastingSystem = BroadcastingSystem(modules)
    repeat(1000) { broadcastingSystem.pushButton() }
    return broadcastingSystem.result().toString()
}

/**
 * 53374000000 too low
 */
fun secondTask(input: Sequence<String>): String {
    val modules = input.toList().parsed() + mapOf(RX to RxModule)
    val broadcastingSystem = BroadcastingSystem(modules)
    return generateSequence(broadcastingSystem) {
        it.pushButton()
        it.updateState()
        it.reset()
        it
    }.first { it.allSegmentsLoopsFound() }
        .validStates()
        .map { it.keys.first() }.leastCommonMultiple().toString()
}

private const val BROADCASTER = "broadcaster"
private const val OUTPUT = "output"
private const val RX = "rx"
private const val BUTTON = "button"

private val OUTPUTS = setOf(OUTPUT, RX)

fun List<String>.parsed(): Map<String, Module> {
    return map { line ->
        val (name, dest) = line.split(" -> ")
        val destModules = dest.split(", ")
        when {
            name == BROADCASTER -> Broadcaster(destModules)
            name.startsWith("%") -> FlipFlop(destinations = destModules, name = name.removePrefix("%"))
            name.startsWith("&") -> Conjunction(destinations = destModules, name = name.removePrefix("&"))
            else -> throw IllegalArgumentException("Could not parse line $line")
        }
    }.associateBy { it.name }
        .also { modules -> modules.values.forEach { module -> module.updateInputs(modules) } }
}


enum class PulseType {
    LOW,
    HIGH;
}

class BroadcastingSystem(
    private val modules: Map<String, Module>
) {
    private val lastModule = modules.values.first { it.hasOutput(RX) }
    private val segmentModules: List<Conjunction> = lastModule.inputs as List<Conjunction>

    private val segments: Map<String, Segment> =
        lastModule.inputs.associate { it.name to findSegment(it as Conjunction) }

    private val segmentLoopFound: MutableMap<String, Boolean> =
        segments.keys.associateWith { false }.toMutableMap()

    private val segmentStates: Map<String, MutableList<State>> =
        segments.keys.associateWith { mutableListOf() }

    fun validStates(): List<Map<Int, State>> =
        segmentStates.values.map { states -> states
            .mapIndexed { index, state -> index + 1 to state }
            .filter { (_, state) -> state.highSent == 1 }
            .toMap() }

    fun reset() {
        segmentModules.forEach { it.reset() }
    }

    fun allSegmentsLoopsFound(): Boolean =
        segmentLoopFound.values.all { it}

    fun updateState() {
        segments.forEach { (name, segment) ->
            if (!segmentLoopFound[name]!!) {
                val state = segment.state()
                val states = segmentStates[name]!!
                if (state in states) {
                    segmentLoopFound[name] = true
                    println("Found loop for segment $segment after ${segmentStates.size} loops")
                }
                states.add(state)
            }
        }
    }

    private val receivedPulses: MutableMap<PulseType, Int> = mutableMapOf(
        PulseType.LOW to 0,
        PulseType.HIGH to 0,
    )

    fun result(): Long = receivedPulses[PulseType.LOW]!!.toLong() * receivedPulses[PulseType.HIGH]!!.toLong()

    fun pushButton() {
        tailrec fun go(rest: List<Pair<String, Pulse>>) {
            if (rest.isNotEmpty()) {
                val (source, pulse) = rest.first()
                go(rest.drop(1) + pulse.send())
            }
        }
        val initial = Pulse(BUTTON, BROADCASTER, PulseType.LOW)
        go(listOf(Pair("button", initial)))
    }

    private fun dumpPulse(source: String, pulse: Pulse) {
        println("$source -${pulse.type}-> ${pulse.destination}")
    }

    private fun Pulse.send(): List<Pair<String, Pulse>> {
        receivedPulses[type] = receivedPulses[type]!! + 1
        return if (destination in OUTPUTS) {
            emptyList()
        } else {
            (modules[destination] ?: throw IllegalArgumentException("Could not find module $destination")).send(this)
                .map { Pair(destination, it) }
        }
    }

    private fun findSegment(module: Conjunction): Segment {
        tailrec fun go(rest: List<Module>, acc: Set<Module> = emptySet()): Segment =
            if (rest.isEmpty()) {
                Segment(module, acc)
            } else {
                val next = rest.first()
                if (next !in acc) {
                    go(rest.drop(1) + next.inputs, acc + next)
                } else {
                    go(rest.drop(1), acc)
                }
            }

        return go(listOf(module), setOf())
    }
}

data class Segment(
    val finalModule: Conjunction,
    val modules: Set<Module>,
) {
    fun state(): State =
        State(
            hash = modules.map { it.name to it.state() }.hashCode(),
            highSent = finalModule.sentPulses[PulseType.HIGH]!!,
            lowSent = finalModule.sentPulses[PulseType.LOW]!!
        )

}

data class Pulse(
    val source: String,
    val destination: String,
    val type: PulseType,
)

sealed interface Module {
    val name: String

    fun send(pulse: Pulse): List<Pulse>

    fun hasOutput(module: String): Boolean

    val inputs: List<Module>

    fun updateInputs(modules: Map<String, Module>)

    fun state(): Any
}

data class Broadcaster(
    val destinations: List<String>
) : Module {
    override val name: String = BROADCASTER
    override fun send(pulse: Pulse): List<Pulse> =
        destinations.map { Pulse(source = name, destination = it, type = pulse.type) }

    override fun hasOutput(module: String): Boolean = module in destinations
    override val inputs: List<Module> = emptyList()
    override fun updateInputs(modules: Map<String, Module>) {
        // Do nothing
    }

    override fun state(): Int = 0
}

data class FlipFlop(
    val destinations: List<String>,
    override val name: String,
) : Module {
    private var on: Boolean = false

    override val inputs: MutableList<Module> = mutableListOf()

    override fun updateInputs(modules: Map<String, Module>) {
        inputs.addAll(modules.values.filter { it.hasOutput(name) })
    }

    override fun hasOutput(module: String): Boolean = module in destinations
    override fun state(): Boolean = on

    override fun send(pulse: Pulse): List<Pulse> =
        when (pulse.type) {
            PulseType.HIGH -> emptyList()
            PulseType.LOW ->
                if (on) {
                    on = !this.on
                    destinations.map { Pulse(source = name, destination = it, PulseType.LOW) }
                } else {
                    on = !this.on
                    destinations.map { Pulse(source = name, destination = it, PulseType.HIGH) }
                }
        }
}

data class Conjunction(
    val destinations: List<String>,
    override val name: String,
) : Module {
    override val inputs: MutableList<Module> = mutableListOf()

    private val lastReceived: MutableMap<String, PulseType> = mutableMapOf()

    val sentPulses: MutableMap<PulseType, Int> = mutableMapOf(
        PulseType.HIGH to 0,
        PulseType.LOW to 0,
    )

    override fun hasOutput(module: String): Boolean = module in destinations
    override fun state(): Any = lastReceived

    override fun updateInputs(modules: Map<String, Module>) {
        modules.values.filter { it.hasOutput(name) }
            .forEach {
                inputs.add(it)
                lastReceived[it.name] = PulseType.LOW
            }
    }

    override fun send(pulse: Pulse): List<Pulse> {
        lastReceived[pulse.source] = pulse.type
        return if (lastReceived.values.all { it == PulseType.HIGH }) {
            destinations.map { Pulse(source = name, destination = it, PulseType.LOW) }
                .also { sentPulses[PulseType.LOW] = sentPulses[PulseType.LOW]!! + 1 }
        } else {
            destinations.map { Pulse(source = name, destination = it, PulseType.HIGH) }
                .also { sentPulses[PulseType.HIGH] = sentPulses[PulseType.HIGH]!! + 1 }
        }
    }

    fun reset() {
        sentPulses.forEach { (k, _) -> sentPulses[k] = 0 }
    }
}

data object RxModule : Module {
    override val name: String = RX
    var receivedPulses = 0

    override fun send(pulse: Pulse): List<Pulse> {
        receivedPulses += 1
        return emptyList()
    }

    override fun hasOutput(module: String): Boolean = false
    override val inputs: MutableList<Module> = mutableListOf()

    override fun updateInputs(modules: Map<String, Module>) {
        inputs.addAll(modules.values.filter { it.hasOutput(name) })
    }

    override fun state(): Any = 0
}

data class State(
    val hash: Int,
    val lowSent: Int,
    val highSent: Int,
)
