package com.gilpereda.aoc2022.utils.collections

import kotlin.reflect.KClass


sealed interface ImLiList<T> {
    val head: T
    val headOrNull: T?
    val tail: ImLiList<T>

    operator fun plus(item: T): ImLiList<T> =
        List(item, this)

    operator fun contains(item: T): Boolean

    fun count(item: T): Int

    val size: Int

    companion object {
        private val empties: MutableMap<KClass<*>, Empty> = mutableMapOf()

        @Suppress("UNCHECKED_CAST")
        fun <T> singleton(item: T): ImLiList<T> = List(item, Empty as ImLiList<T>)

        fun <T> list(item: T): ImLiList<T> = singleton(item)

        data object Empty : ImLiList<Any> {
            override val head: Any
                get() = throw UnsupportedOperationException("The list is empty")

            override val headOrNull: Any? = null
            override val tail: ImLiList<Any>
                get() = this

            override fun count(item: Any): Int = 0

            override fun contains(item: Any): Boolean = false

            override val size: Int = 0
        }

        data class List<T>(
            override val head: T,
            override val tail: ImLiList<T>
        ) : ImLiList<T> {
            private val itemToCount = mutableMapOf<T, Int>()
            override val headOrNull: T = head
            override fun contains(item: T): Boolean =
                (item == head) || item in tail

            override val size: Int by lazy {
                tail.size + 1
            }

            override fun count(item: T): Int =
                itemToCount.computeIfAbsent(item) {
                    tailrec fun go(list: ImLiList<T>, acc: Int = 0): Int =
                        when (list) {
                            is Empty -> acc
                            is List -> go(list.tail, acc + if (item == head) 1 else 0)
                        }

                    go(this)
                }
        }
    }
}