package com.bj4.lottery2023.compose

import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

const val GRID_HORIZONTAL_PADDING = 4
val SPECIAL_COLOR = Color.Blue


@OptIn(ExperimentalTextApi::class)
@Composable
fun getNumberWidth(fontSize: Int): Dp {
    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult: TextLayoutResult =
        textMeasurer.measure(
            text = AnnotatedString("00"),
            style = LocalTextStyle.current.copy(fontSize = fontSize.sp)
        )
    val textSize = textLayoutResult.size
    val density = LocalDensity.current

    return with(density) { textSize.width.toDp() } + GRID_HORIZONTAL_PADDING.dp
}