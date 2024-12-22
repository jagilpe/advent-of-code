package com.gilpereda.aoc2024.day22

import com.gilpereda.adventofcode.commons.concurrency.LongSumSequenceCollector
import com.gilpereda.adventofcode.commons.concurrency.SequenceCollector
import com.gilpereda.adventofcode.commons.concurrency.transformAndCollect
import java.util.concurrent.ConcurrentHashMap

fun firstTask(input: Sequence<String>): String {
    val collector = LongSumSequenceCollector.instance()
    input
        .map { it.toLong() }
        .transformAndCollect(
            transform = { secretSequence(it).take(2001).last() },
            collector = collector,
        )
    return collector.get().toString()
}

/**
 * 1747 too high
 */
fun secondTask(input: Sequence<String>): String {
    val collector = PriceSequenceCollector()

    input
        .flatMapIndexed { monkey, secret -> priceSeqItemSequence(input = secret.toLong(), monkey = monkey) }
        .transformAndCollect(
            transform = { it },
            collector = collector,
        )

    val bestSequence = collector.getBestSequence()
    return bestSequence.second.toString()
}

fun secretSequence(initial: Long): Sequence<Long> = generateSequence(initial) { nextSecret(it) }

private fun nextSecret(secret: Long): Long {
    val first = mix(secret * 64, secret).pruned()
    val second = mix(first / 32, first).pruned()
    return mix(second * 2048, second).pruned()
}

private fun mix(
    value: Long,
    secret: Long,
): Long = value.xor(secret)

private fun Long.pruned(): Long = this % 16777216

private fun priceSeqItemSequence(
    input: Long,
    monkey: Int,
): Sequence<PriceSeqItem> =
    secretSequence(input)
        .take(2001)
        .map { (it % 10).toInt() }
        .windowed(2)
        .map { (previous, current) -> current to (current - previous) }
        .windowed(4)
        .map {
            PriceSeqItem(
                monkey = monkey,
                price = it[3].first,
                priceSeq =
                    PriceSeq(
                        first = it[0].second,
                        second = it[1].second,
                        third = it[2].second,
                        fourth = it[3].second,
                    ),
            )
        }

data class PriceSeq(
    val first: Int,
    val second: Int,
    val third: Int,
    val fourth: Int,
)

data class PriceSeqItem(
    val monkey: Int,
    val price: Int,
    val priceSeq: PriceSeq,
)

class PriceSequenceCollector : SequenceCollector<PriceSeqItem, Map<PriceSeq, Map<Int, Int>>> {
    private val priceSeqResult = ConcurrentHashMap<PriceSeq, MutableMap<Int, Int>>()

    override fun emit(value: PriceSeqItem) {
        priceSeqResult
            .computeIfAbsent(value.priceSeq) { ConcurrentHashMap() }
            .computeIfAbsent(value.monkey) { _ -> value.price }
    }

    fun getBestSequence(): Pair<PriceSeq, Int> =
        priceSeqResult
            .mapValues { (_, values) -> values.values.sum() }
            .entries
            .maxBy { it.value }
            .toPair()
}
