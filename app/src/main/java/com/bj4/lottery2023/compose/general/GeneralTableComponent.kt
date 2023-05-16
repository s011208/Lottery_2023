package com.bj4.lottery2023.compose.general

import android.content.res.Resources
import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bj4.lottery2023.compose.GRID_HORIZONTAL_PADDING
import com.bj4.lottery2023.compose.SPECIAL_COLOR
import com.bj4.lottery2023.compose.getNumberWidth


data class Row(
    val dataList: List<Grid>,
    val type: Type
) {
    enum class Type {
        MonthlyTotal, Header, LotteryData
    }
}

data class Grid(
    val index: Int = -1,
    val text: String = "",
    val visible: Boolean = true,
    val type: Type,
    @StringRes val textResource: Int = Resources.ID_NULL,
) {
    enum class Type {
        Normal, Date, Special, NormalLast, SpecialLast,
    }
}


@Composable
fun GridFactory(grid: Grid, fontSize: Int, fontSizeRatio: Float = 1f, extraSpacing: Int) {
    when (grid.type) {
        Grid.Type.Normal -> NormalGrid(grid, fontSize, fontSizeRatio, extraSpacing)
        Grid.Type.Date -> DateGrid(grid, fontSize, fontSizeRatio, extraSpacing)
        Grid.Type.Special -> SpecialGrid(grid, fontSize, fontSizeRatio, extraSpacing)
        Grid.Type.NormalLast -> NormalLastGrid(grid, fontSize, fontSizeRatio, extraSpacing)
        Grid.Type.SpecialLast -> SpecialLastGrid(grid, fontSize, fontSizeRatio, extraSpacing)
    }
}


@Composable
fun DateGrid(grid: Grid, fontSize: Int, fontSizeRatio: Float = 1f, extraSpacing: Int) {
    Text(
        textAlign = TextAlign.Center,
        modifier = Modifier
            .border(width = 1.dp, Color.Black)
            .width(getDateWidth(fontSize) * fontSizeRatio + (2 * extraSpacing).dp),
        text = gridText(grid),
        color = if (grid.visible) {
            Color.Gray
        } else {
            Color.Transparent
        },
        fontSize = fontSize.sp * fontSizeRatio
    )
}

@Composable
fun NormalGrid(grid: Grid, fontSize: Int, fontSizeRatio: Float = 1f, extraSpacing: Int) {
    Text(
        text = gridText(grid),
        textAlign = TextAlign.Center,
        modifier = Modifier
            .border(width = 1.dp, Color.Black)
            .width(getNumberWidth(fontSize) + (2 * extraSpacing).dp),
        color = if (grid.visible) {
            Color.Unspecified
        } else {
            Color.Transparent
        },
        fontSize = fontSize.sp * fontSizeRatio
    )
}

@Composable
fun NormalLastGrid(grid: Grid, fontSize: Int, fontSizeRatio: Float = 1f, extraSpacing: Int) {
    Text(
        text = gridText(grid),
        textAlign = TextAlign.Center,
        modifier = Modifier
            .border(width = 1.dp, Color.Black)
            .width(getNumberWidth(fontSize) + (2 * extraSpacing).dp),
        color = if (grid.visible) {
            Color.Unspecified
        } else {
            Color.Transparent
        },
        fontSize = fontSize.sp * fontSizeRatio
    )
}

@Composable
fun SpecialGrid(grid: Grid, fontSize: Int, fontSizeRatio: Float = 1f, extraSpacing: Int) {
    Text(
        textAlign = TextAlign.Center,
        modifier = Modifier
            .border(width = 1.dp, Color.Black)
            .width(getNumberWidth(fontSize) + (2 * extraSpacing).dp),
        text = gridText(grid),
        color = if (grid.visible) {
            SPECIAL_COLOR
        } else {
            Color.Transparent
        },
        fontSize = fontSize.sp * fontSizeRatio
    )
}

@Composable
fun SpecialLastGrid(grid: Grid, fontSize: Int, fontSizeRatio: Float = 1f, extraSpacing: Int) {
    Text(
        textAlign = TextAlign.Center,
        modifier = Modifier
            .border(width = 1.dp, Color.Black)
            .width(getNumberWidth(fontSize) + (2 * extraSpacing).dp),
        text = gridText(grid),
        color = if (grid.visible) {
            SPECIAL_COLOR
        } else {
            Color.Transparent
        },
        fontSize = fontSize.sp * fontSizeRatio
    )
}

@Composable
private fun gridText(grid: Grid) =
    if (grid.textResource != Resources.ID_NULL) {
        stringResource(id = grid.textResource)
    } else {
        grid.text
    }


@OptIn(ExperimentalTextApi::class)
@Composable
fun getDateWidth(fontSize: Int): Dp {
    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult: TextLayoutResult =
        textMeasurer.measure(
            text = AnnotatedString("0000/00/00"),
            style = LocalTextStyle.current.copy(fontSize = fontSize.sp)
        )
    val textSize = textLayoutResult.size
    val density = LocalDensity.current

    return with(density) { textSize.width.toDp() } + GRID_HORIZONTAL_PADDING.dp
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun getPossibilityNumberWidth(fontSize: Int): Dp {
    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult: TextLayoutResult =
        textMeasurer.measure(
            text = AnnotatedString(".00"),
            style = LocalTextStyle.current.copy(fontSize = fontSize.sp)
        )
    val textSize = textLayoutResult.size
    val density = LocalDensity.current

    return with(density) { textSize.width.toDp() } + GRID_HORIZONTAL_PADDING.dp
}


