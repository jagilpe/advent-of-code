package com.gilpereda.aoc2024.utils.visualization

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.gilpereda.adventofcode.commons.geometry.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

@Composable
@Suppress("ktlint:standard:function-naming", "unused", "FunctionName")
fun Visualization(mapVisualization: MapVisualization) {
    var auto by remember { mutableStateOf(false) }
    var reversed by remember { mutableStateOf(false) }
    var fps by remember { mutableStateOf(1) }
    var step by remember { mutableStateOf(0) }
    val composableScope = rememberCoroutineScope()
    MaterialTheme {
        Column {
            Text("Frames per second: $fps")
            Row {
                Button(
                    content = { Text("^") },
                    onClick = {
                        fps++
                    },
                )
                Button(
                    content = { Text("v") },
                    onClick = {
                        if (fps > 1) {
                            fps--
                        }
                    },
                )
            }

            Text("Step: $step")
            Row {
                Button(
                    content = { Text("<<") },
                    onClick = {
                        reversed = true
                        auto = true
                    },
                )
                Button(
                    content = { Text("<") },
                    enabled = !auto,
                    onClick = {
                        auto = false
                        step = mapVisualization.previous()
                    },
                )
                Button(
                    content = { Text("||") },
                    enabled = auto,
                    onClick = {
                        auto = false
                    },
                )
                Button(
                    content = { Text(">") },
                    enabled = !auto,
                    onClick = {
                        auto = false
                        step = mapVisualization.next()
                    },
                )
                Button(
                    content = { Text(">>") },
                    onClick = {
                        auto = true
                        reversed = false
                    },
                )
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            draw(mapVisualization)
        }
    }

    composableScope.launch {
        channelFlow<Unit> {
            while (true) {
                if (auto) {
                    step =
                        if (reversed) {
                            mapVisualization.previous()
                        } else {
                            mapVisualization.next()
                        }
                    delay(1000L / fps)
                } else {
                    delay(1000L)
                }
            }
        }.flowOn(Dispatchers.IO).collect { }
    }
}

fun DrawScope.draw(mapVisualization: MapVisualization) {
    val halfWidth = mapVisualization.width / 2
    val halfHeight = mapVisualization.height / 2
    val canvasDimension = minOf(size.height, size.width)
    val horizontalStep = canvasDimension / mapVisualization.width
    val verticalStep = canvasDimension / mapVisualization.height
    val cellSize = Size(horizontalStep, verticalStep)

    fun Point.offset(): Offset = center.plus(Offset(((x - halfWidth) + 0.5f) * horizontalStep, (y - halfHeight - 0.5f) * verticalStep))

    mapVisualization
        .pointSequence()
        .forEach { (point, color) ->
            drawRect(
                color = color,
                size = cellSize,
                topLeft = point.offset(),
            )
        }
}
