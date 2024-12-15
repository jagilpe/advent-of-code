package com.gilpereda.aoc2024.day14

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.gilpereda.adventofcode.commons.geometry.Point
import com.gilpereda.adventofcode.commons.map.TypedTwoDimensionalMap
import com.gilpereda.aoc2024.utils.visualization.MapVisualization
import com.gilpereda.aoc2024.utils.visualization.Visualization

val example: String =
    """
    w=11 h=7
    p=0,4 v=3,-3
    p=6,3 v=-1,-3
    p=10,3 v=-1,2
    p=2,0 v=2,-1
    p=0,0 v=1,3
    p=3,0 v=-2,-2
    p=7,6 v=-1,-3
    p=3,0 v=-1,-2
    p=9,3 v=2,3
    p=7,3 v=-1,2
    p=2,4 v=2,-3
    p=9,5 v=-3,-3
    """.trimIndent()

private const val LOAD_REAL = true

fun main() =
    application {
        val input =
            if (LOAD_REAL) {
                RobotsVisualization::class.java
                    .getResourceAsStream("/day14/input")!!
                    .bufferedReader()
                    .lineSequence()
            } else {
                example.splitToSequence("\n")
            }
        val robotsVisualization = RobotsVisualization(input)
        Window(onCloseRequest = ::exitApplication) {
            Visualization(robotsVisualization)
        }
    }

private class RobotsVisualization(
    input: Sequence<String>,
) : MapVisualization {
    private val map: TypedTwoDimensionalMap<Int>
    private var robots: List<Robot>
    private var step = 0

    init {
        val (map, robots) = parseInput(input)
        this.map = map
        this.robots = robots
    }

    override val width: Int = map.width
    override val height: Int = map.height

    override fun next(): Int {
        robots = robots.next(map.width, map.height)
        step++
        return step
    }

    override fun previous(): Int {
        if (step > 0) {
            robots = robots.previous(map.width, map.height)
            step--
        }
        return step
    }

    override fun pointSequence(): Sequence<Pair<Point, Color>> {
        val robotPositions = robots.map { it.position }
        return map
            .valuesIndexed()
            .map { (point, _) ->
                point to if (point in robotPositions) Color.Black else Color.White
            }.asSequence()
    }
}
