package com.gilpereda.adventofcode.commons.concurrency

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.LongAccumulator

@OptIn(ExperimentalCoroutinesApi::class)
fun <A, B, C> Sequence<A>.transformAndCollect(
    transform: (A) -> B,
    collector: SequenceCollector<B, C>,
) = runBlocking {
    asFlow()
        .flatMapMerge { flow { emit(transform(it)) } }
        .flowOn(Dispatchers.IO)
        .collect(collector.asFlowCollector)
}

fun interface SequenceCollector<A, B> {
    fun emit(value: A)
}

class ProgressPrinter(
    private val notifyEach: Int = 100,
) {
    private val start = System.currentTimeMillis()
    private val counter = LongAccumulator(Long::plus, 0)

    fun emit() {
        counter.accumulate(1)
        if (counter.get() % notifyEach == 0L) printProgress()
    }

    private fun printProgress() {
        println("Processed ${counter.get()} in ${System.currentTimeMillis() - start}ms")
    }
}

private val <A> SequenceCollector<A, *>.asFlowCollector
    get() = FlowCollector<A> { value -> emit(value) }

class LongSumSequenceCollector private constructor(
    private val progressPrinter: ProgressPrinter = ProgressPrinter(),
) : SequenceCollector<Long, Long> {
    private val accumulator = LongAccumulator(Long::plus, 0)

    override fun emit(value: Long) {
        accumulator.accumulate(value)
        progressPrinter.emit()
    }

    fun get(): Long = accumulator.get()

    companion object {
        fun instance(notifyEach: Int = 100): LongSumSequenceCollector = LongSumSequenceCollector(ProgressPrinter(notifyEach))
    }
}