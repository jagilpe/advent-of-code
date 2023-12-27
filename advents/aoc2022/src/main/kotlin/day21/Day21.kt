package com.gilpereda.aoc2022.day21

import com.gilpereda.aoc2022.day21.Tree.Companion.Leaf
import com.gilpereda.aoc2022.day21.Tree.Companion.Node
import java.math.BigDecimal
import kotlin.properties.Delegates

const val ROOT = "root"
const val HUMN = "humn"

fun firstTask(input: Sequence<String>): String {
    val monkeys = input.parsed()
    val monkeyFinder = MonkeyFinder(monkeys)
    monkeys.forEach { it.addMonkeyFinder(monkeyFinder) }

    val root = monkeyFinder.find(ROOT)

    var result by Delegates.notNull<BigDecimal>()

    root.addSubscriber {
        result = it
    }

    return result.toString()
}

fun firstTask2(input: Sequence<String>): String {
    return calculate(input.parseTree().map(Tree::toMonkey)).first[ROOT].toString()
}

fun secondTask(input: Sequence<String>): String {
    val monkeyTree = input.parseTree()
    val treeFinder = TreeFinder(monkeyTree)
    val (firstResults) = calculate(monkeyTree.map(Tree::toMonkey))


    val rootDependency = (treeFinder.find(ROOT) as Node).dependenciesOf(HUMN, treeFinder)!!.notDependent
    val expectedRootValue = firstResults[rootDependency]!! * BigDecimal(2)

    val (secondResults) = calculate(monkeyTree.traverse(HUMN, expectedRootValue).map(Tree::toMonkey))

    return secondResults[HUMN].toString()
}

fun calculate(monkeys: List<Monkey>): Pair<Map<String, BigDecimal>, MonkeyFinder> {
    val result = mutableMapOf<String, BigDecimal>()

    val monkeyFinder = MonkeyFinder(monkeys)
    monkeys.forEach { it.addMonkeyFinder(monkeyFinder) }

    monkeys.forEach { monkey -> monkey.addSubscriber { value -> result[monkey.name] = value } }

    return result to monkeyFinder
}


fun secondTask2(input: Sequence<String>): String {
    val parseTree = input.parseTree()
    val treeFinder = TreeFinder(parseTree)
    val monkeyFinder: MonkeyFinder = parseTree
        .map(Tree::toMonkey)
        .map {
            when (it.name) {
                ROOT -> (it as CalculatingMonkey).copy(operation = Compare)
                HUMN -> CustomYellingMonkey(it.name)
                else -> it
            }
        }
        .let { monkeys ->
            val monkeyFinder = MonkeyFinder(monkeys)
            monkeys.forEach { it.addMonkeyFinder(monkeyFinder) }
            monkeyFinder
        }

    val goal = 90565407195785

    val (dependent, notDependent) =
        (treeFinder.find(ROOT) as Node).dependenciesOf(HUMN, treeFinder)!!
            .let { (dep, notDep) -> Pair(monkeyFinder.find(dep), monkeyFinder.find(notDep)) }

    val humn = monkeyFinder.find(HUMN) as CustomYellingMonkey

    (-8506900824097 downTo 8506900824117).forEach { value -> humn.update(BigDecimal(value)) }

    var result by Delegates.notNull<BigDecimal>()

    notDependent.addSubscriber {
        result = it
    }

    return result.toString()
}


private val CALCULATING_MONKEY_REGEX = "([a-z]+): ([a-z]+) ([+*-/]) ([a-z]+)".toRegex()
private val YELLING_MONKEY_REGEX = "([a-z]+): ([0-9]+)".toRegex()

fun Sequence<String>.parsed(): List<Monkey> =
    map {
        CALCULATING_MONKEY_REGEX.find(it)
            ?.destructured?.let { (name, one, op, other) ->
                val operation = when (op) {
                    "+" -> Sum
                    "-" -> Subtract
                    "*" -> Multiply
                    else -> Divide
                }
                CalculatingMonkey(name, operation, one, other)
            }
            ?: YELLING_MONKEY_REGEX.find(it)
                ?.destructured?.let { (name, value) -> YellingMonkey(name, value.toBigDecimal()) }
            ?: throw Exception("Could not parse line $it")
    }.toList()


fun Sequence<String>.parseTree(): List<Tree> =
    map {
        CALCULATING_MONKEY_REGEX.find(it)
            ?.destructured?.let { (name, one, operation, other) -> Node(name, Operation.of(operation), one, other) }
            ?: YELLING_MONKEY_REGEX.find(it)
                ?.destructured?.let { (name, value) -> Leaf(name, value.toBigDecimal()) }
            ?: throw Exception("Could not parse line $it")
    }.toList()

fun List<Tree>.traverse(dependency: String, rootValue: BigDecimal): List<Tree> {
    val treeFinder = TreeFinder(this)

    return flatMap { monkey ->
        if (monkey.name == ROOT) {
            val (dependent, notDependent) = (monkey as Node).dependenciesOf(dependency, treeFinder)!!
            listOf(Leaf(ROOT, rootValue), Node(dependent, Subtract, ROOT, notDependent))
        } else if (monkey.isDependentOf(dependency, treeFinder)) {
            if (monkey is Node) {
                monkey.traverse(dependency, treeFinder)
            } else {
                // This is the "humn" monkey
                emptyList()
            }
        } else {
            listOf(monkey)
        }
    }
}


class TreeFinder(
    monkeys: List<Tree>,
) {
    private val treeNodeMap: Map<String, Tree> =
        monkeys.associateBy { it.name }

    fun find(name: String): Tree =
        treeNodeMap[name] ?: throw Exception("Tree node does not exist: $name")
}

data class Dependence(
    val dependent: String,
    val notDependent: String,
)


sealed interface Tree {
    val name: String
    fun toMonkey(): Monkey
    fun isDependentOf(dependence: String, treeFinder: TreeFinder): Boolean

    companion object {
        data class Leaf(
            override val name: String,
            val value: BigDecimal,
        ) : Tree {
            override fun toMonkey(): Monkey = YellingMonkey(name, value)
            override fun isDependentOf(dependence: String, treeFinder: TreeFinder): Boolean =
                name == dependence
        }

        data class Node(
            override val name: String,
            val op: Operation,
            val one: String,
            val other: String
        ) : Tree {
            override fun toMonkey(): Monkey = CalculatingMonkey(name, op, one, other)

            override fun isDependentOf(dependency: String, treeFinder: TreeFinder): Boolean =
                one.isDependentOf(dependency, treeFinder) || other.isDependentOf(dependency, treeFinder)

            fun traverse(dependency: String, treeFinder: TreeFinder): List<Node> {
                val (dependent, notDependent) = dependenciesOf(dependency, treeFinder)!!
                return when(op) {
                    Sum, Multiply -> listOf(Node(dependent, op.traversed, name, notDependent))
                    Divide, Subtract -> when {
                        one.isDependentOf(dependency, treeFinder) -> listOf(Node(dependent, op.traversed, name, notDependent))
                        else -> listOf(Node(dependent, op, notDependent, name))
                    }
                    else -> emptyList()
                }
            }

            fun dependenciesOf(dependence: String, treeFinder: TreeFinder): Dependence? =
                when {
                    one.isDependentOf(dependence, treeFinder) -> Dependence(one, other)
                    other.isDependentOf(dependence, treeFinder) -> Dependence(other, one)
                    else -> null
                }

            private fun String.isDependentOf(dependence: String, treeFinder: TreeFinder): Boolean =
                treeFinder.find(this).isDependentOf(dependence, treeFinder)
        }
    }
}

class MonkeyFinder(
    monkeys: List<Monkey>,
) {
    private val monkeyMap: Map<String, Monkey> =
        monkeys.associateBy { it.name }

    fun find(name: String): Monkey =
        monkeyMap[name] ?: throw Exception("Monkey does not exist: $name")
}

sealed interface Monkey {
    val name: String
    fun addSubscriber(subscriber: Subscriber)
    fun addMonkeyFinder(monkeyFinder: MonkeyFinder): Monkey
    fun dependenciesWith(monkey: String): Int
}

fun interface Subscriber {
    fun update(value: BigDecimal)
}

data class YellingMonkey(
    override val name: String,
    val value: BigDecimal,
) : Monkey {
    override fun addSubscriber(subscriber: Subscriber) {
        subscriber.update(value)
    }

    override fun addMonkeyFinder(monkeyFinder: MonkeyFinder) = this

    override fun dependenciesWith(monkey: String): Int = if (name == monkey) 1 else 0
}

class CustomYellingMonkey(
    override val name: String,
) : Monkey {
    private val subscribers: MutableList<Subscriber> = mutableListOf()
    fun update(value: BigDecimal) {
        subscribers.forEach { it.update(value) }
    }

    override fun addSubscriber(subscriber: Subscriber) {
        this.subscribers.add(subscriber)
    }

    override fun addMonkeyFinder(monkeyFinder: MonkeyFinder) = this

    override fun dependenciesWith(monkey: String): Int = if (name == monkey) 1 else 0
}

data class CalculatingMonkey(
    override val name: String,
    private val operation: Operation,
    private val one: String,
    private val other: String,
) : Monkey {
    private lateinit var oneMonkey: Monkey
    private lateinit var otherMonkey: Monkey
    private var operand1: BigDecimal? = null
    private var operand2: BigDecimal? = null
    private val subscribers: MutableList<Subscriber> = mutableListOf()

    override fun addSubscriber(subscriber: Subscriber) {
        this.subscribers.add(subscriber)
        updateValue()
    }

    override fun addMonkeyFinder(monkeyFinder: MonkeyFinder): Monkey {
        this.oneMonkey = monkeyFinder.find(one)
        this.otherMonkey = monkeyFinder.find(other)
        addOne(monkeyFinder.find(one))
        addOther(monkeyFinder.find(other))
        return this
    }

    override fun dependenciesWith(monkey: String): Int =
        oneMonkey.dependenciesWith(monkey) + otherMonkey.dependenciesWith(monkey)

    fun dependentWith(monkey: String): Monkey =
        listOf(oneMonkey, otherMonkey).first { it.dependenciesWith(monkey) > 0 }

    private fun updateValue() {
        operand1?.let { a ->
            operand2?.let { b -> operation.yield(a, b) }
        }
            ?.let { value ->
                this.subscribers.forEach { it.update(value) }
            }
    }

    private fun addOne(monkey: Monkey) {
        monkey.addSubscriber { one ->
            this.operand1 = one
            updateValue()
        }
    }

    private fun addOther(monkey: Monkey) {
        monkey.addSubscriber { other ->
            this.operand2 = other;
            updateValue()
        }
    }
}

sealed interface Operation {
    fun yield(one: BigDecimal?, other: BigDecimal?): BigDecimal? =
        one?.let { other?.let { operation(one, other) } }

    fun operation(one: BigDecimal, other: BigDecimal): BigDecimal = BigDecimal(0L)

    val traversed: Operation

    companion object {
        fun of(op: String): Operation =
            when (op) {
                "+" -> Sum
                "-" -> Subtract
                "*" -> Multiply
                else -> Divide
            }
    }
}

object Sum : Operation {
    override fun operation(one: BigDecimal, other: BigDecimal): BigDecimal = one + other
    override val traversed: Operation = Subtract
}

object Subtract : Operation {
    override fun operation(one: BigDecimal, other: BigDecimal): BigDecimal = one - other
    override val traversed: Operation = Sum
}

object Multiply : Operation {
    override fun operation(one: BigDecimal, other: BigDecimal): BigDecimal = one * other
    override val traversed: Operation = Divide
}

object Divide : Operation {
    override fun operation(one: BigDecimal, other: BigDecimal): BigDecimal =
            one / other

    override val traversed: Operation = Multiply
}

object Compare : Operation {
    override fun operation(one: BigDecimal, other: BigDecimal): BigDecimal {
        println("one: $one")
        println("other: $other")
        return one - other
    }

    override val traversed: Operation = Multiply
}
