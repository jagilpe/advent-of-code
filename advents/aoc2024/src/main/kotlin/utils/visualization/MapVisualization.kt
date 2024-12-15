package com.gilpereda.aoc2024.utils.visualization

import androidx.compose.ui.graphics.Color
import com.gilpereda.adventofcode.commons.geometry.Point
import com.gilpereda.adventofcode.commons.map.TypedTwoDimensionalMap

interface MapVisualization {
    val width: Int
    val height: Int

    fun next(): Int

    fun previous(): Int

    fun pointSequence(): Sequence<Pair<Point, Color>>

    companion object {
        fun <T> forMap(
            map: TypedTwoDimensionalMap<T>,
            cellToColor: (Point, T) -> Color,
            next: (TypedTwoDimensionalMap<T>) -> TypedTwoDimensionalMap<T>,
            previous: (TypedTwoDimensionalMap<T>) -> TypedTwoDimensionalMap<T> = {
                throw IllegalStateException("Going back is not supported for this map")
            },
        ): MapVisualization =
            object : MapVisualization {
                private var current: TypedTwoDimensionalMap<T> = map
                private var step = 0

                override val width: Int
                    get() = current.width
                override val height: Int
                    get() = current.height

                override fun next(): Int {
                    current = next(current)
                    step++
                    return step
                }

                override fun previous(): Int {
                    if (step > 0) {
                        current = previous(current)
                        step--
                    }
                    return step
                }

                override fun pointSequence(): Sequence<Pair<Point, Color>> =
                    current.valuesIndexed().map { (point, cell) -> point to cellToColor(point, cell) }.asSequence()
            }
    }
}
