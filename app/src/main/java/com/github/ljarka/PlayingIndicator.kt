package com.github.ljarka

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.random.Random

data class LineData(val heightFraction: Float)

@Composable
fun PlayingIndicator(
    modifier: Modifier = Modifier,
    spaceBetween: Dp = 2.dp,
    horizontalPadding: Dp = 16.dp,
    initialLines: List<LineData> = listOf(
        LineData(0.2f),
        LineData(0.4f),
        LineData(0.7f),
        LineData(0.9f),
        LineData(0.5f),
        LineData(0.2f),
    ),
) {
    BoxWithConstraints(
        modifier = modifier.padding(horizontal = horizontalPadding),
        contentAlignment = Alignment.Center,
    ) {
        val availableSpace = maxWidth.dpToPx() - horizontalPadding.dpToPx()
        val numberOfSpacesBetween = initialLines.size - 1
        val maxHeightPx = maxHeight.dpToPx()
        val lineWidthPx =
            (availableSpace - numberOfSpacesBetween * spaceBetween.dpToPx()) / initialLines.size

        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.spacedBy(spaceBetween),
        ) {
            initialLines.forEach {
                val lineMaxHeight = maxHeightPx - lineWidthPx
                Line(
                    lineWidth = lineWidthPx,
                    initialHeight = it.heightFraction * lineMaxHeight,
                    lineMaxHeight = lineMaxHeight,
                )
            }
        }
    }
}

@Composable
private fun Line(
    lineWidth: Int,
    lineMaxHeight: Int,
    initialHeight: Float,
    modifier: Modifier = Modifier,
) {
    val contentColor = LocalContentColor.current
    val lineMaxHeightPx = LocalDensity.current.run { lineMaxHeight }
    val height = remember { Animatable(lineMaxHeightPx - initialHeight) }

    LaunchedEffect(height) {
        launch {
            while (true) {
                val randomValue = Random.nextInt(lineMaxHeightPx).toFloat()

                if (randomValue < height.value) {
                    height.animateTo(
                        targetValue = randomValue,
                        animationSpec = tween(150, easing = FastOutSlowInEasing),
                    )
                } else {
                    val maxDuration = 500
                    val distance = randomValue - height.value

                    height.animateTo(
                        targetValue = randomValue,
                        animationSpec = tween(
                            durationMillis = (distance * maxDuration / lineMaxHeightPx).toInt(),
                            easing = LinearEasing,
                        ),
                    )
                }
            }
        }
    }
    Canvas(
        modifier = modifier
            .width(lineWidth.pxToDp())
            .height(lineMaxHeight.pxToDp()),
    ) {
        val startX = size.width / 2f
        drawLine(
            start = Offset(startX, height.value),
            end = Offset(startX, lineMaxHeightPx.toFloat()),
            strokeWidth = lineWidth.toFloat(),
            color = contentColor,
            cap = StrokeCap.Round,
        )
    }
}

@Preview
@Composable
fun PlayingIndicatorPreview() {
    PlayingIndicator(modifier = Modifier.size(height = 100.dp, width = 200.dp))
}

@Preview
@Composable
fun PlayingIndicatorSpaceBetweenPreview() {
    PlayingIndicator(
        spaceBetween = 16.dp,
        modifier = Modifier.size(height = 100.dp, width = 200.dp)
    )
}

@Preview
@Composable
fun PlayingIndicatorHorizontalPaddingPreview() {
    PlayingIndicator(
        modifier = Modifier.size(height = 100.dp, width = 200.dp),
        horizontalPadding = 40.dp
    )
}