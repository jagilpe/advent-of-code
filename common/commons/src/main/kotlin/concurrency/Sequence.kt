package com.gilpereda.adventofcode.commons.concurrency

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.DEFAULT_CONCURRENCY
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.LongAccumulator

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
fun <A, B> Sequence<A>.transformAndCollect(
    concurrency: Int = DEFAULT_CONCURRENCY,
    transform: (A) -> B,
    collector: SequenceCollector<B>,
) = runBlocking {
    asFlow()
        .flatMapMerge(concurrency) { flow { emit(transform(it)) } }
        .flowOn(Dispatchers.IO.limitedParallelism(concurrency))
        .collect(collector.asFlowCollector)
}

fun interface SequenceCollector<A> {
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

private val <A> SequenceCollector<A>.asFlowCollector
    get() = FlowCollector<A> { value -> emit(value) }

fun <A> SequenceCollector<A>.logProgress(notifyEach: Int = 100): SequenceCollector<A> = ProgressPrinterSequenceCollector(this, notifyEach)

private class ProgressPrinterSequenceCollector<A>(
    private val delegate: SequenceCollector<A>,
    notifyEach: Int,
) : SequenceCollector<A> {
    private val progressPrinter: ProgressPrinter = ProgressPrinter(notifyEach)

    override fun emit(value: A) {
        delegate.emit(value)
        progressPrinter.emit()
    }
}

class LongSumSequenceCollector : SequenceCollector<Long> {
    private val accumulator = LongAccumulator(Long::plus, 0)

    override fun emit(value: Long) {
        accumulator.accumulate(value)
    }

    fun get(): Long = accumulator.get()

    companion object {
        fun instance(): LongSumSequenceCollector = LongSumSequenceCollector()
    }
}
