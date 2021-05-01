package com.fabirt.podcastapp.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun StaggeredVerticalGrid(
    modifier: Modifier = Modifier,
    crossAxisCount: Int,
    spacing: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        check(constraints.hasBoundedWidth) {
            "Unbounded width not supported"
        }
        val spacingWidth = spacing.roundToPx() / crossAxisCount
        val columnWidth = constraints.maxWidth / crossAxisCount
        val itemConstraints =
            constraints.copy(maxWidth = columnWidth - spacingWidth)
        val colHeights = IntArray(crossAxisCount) { 0 } // track each column's height
        val placeables = measurables.map { measurable ->
            val column = shortestColumn(colHeights)
            val placeable = measurable.measure(itemConstraints)
            colHeights[column] += placeable.height
            placeable
        }

        val height = colHeights.maxOrNull()?.coerceIn(constraints.minHeight, constraints.maxHeight)
            ?: constraints.minHeight
        layout(
            width = constraints.maxWidth,
            height = height
        ) {
            val colY = IntArray(crossAxisCount) { 0 }
            placeables.forEachIndexed { index, placeable ->
                val column = shortestColumn(colY)
                val offset = if (column > 0) spacingWidth else 0

                placeable.place(
                    x = (columnWidth + offset) * column,
                    y = colY[column]
                )
                colY[column] += placeable.height
            }
        }
    }
}

private fun shortestColumn(colHeights: IntArray): Int {
    var minHeight = Int.MAX_VALUE
    var column = 0
    colHeights.forEachIndexed { index, height ->
        if (height < minHeight) {
            minHeight = height
            column = index
        }
    }
    return column
}