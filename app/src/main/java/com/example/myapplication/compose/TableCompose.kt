package com.example.myapplication.compose

import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.vm.Grid
import com.example.myapplication.vm.Row

@Composable
fun Table(rowList: List<Row>) {
    val scroll = rememberScrollState(0)

    LazyColumn(modifier = Modifier.horizontalScroll(scroll)) {
        rowList.forEach { row ->
            item {
                RowFactory(row)
            }
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
private fun getDateWidth(): Dp {
    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult: TextLayoutResult =
        textMeasurer.measure(
            text = AnnotatedString("0000/00/00"),
            style = LocalTextStyle.current
        )
    val textSize = textLayoutResult.size
    val density = LocalDensity.current

    return with(density) { textSize.width.toDp() }
}

@OptIn(ExperimentalTextApi::class)
@Composable
private fun getNumberWidth(): Dp {
    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult: TextLayoutResult =
        textMeasurer.measure(
            text = AnnotatedString("00"),
            style = LocalTextStyle.current
        )
    val textSize = textLayoutResult.size
    val density = LocalDensity.current

    return with(density) { textSize.width.toDp() }
}

@Composable
fun RowFactory(row: Row) {
    Row {
        row.dataList.forEach { grid -> GridFactory(grid, row.type) }
    }
}

@Composable
fun GridFactory(grid: Grid, rowType: Row.Type) {
    when (grid.type) {
        Grid.Type.Normal -> NormalGrid(grid)
        Grid.Type.Date -> DateGrid(grid)
        Grid.Type.Special -> SpecialGrid(grid)
    }
}


@Composable
fun DateGrid(grid: Grid) {
    Text(
        textAlign = TextAlign.Center,
        modifier = Modifier
            .border(width = 1.dp, Color.Black)
            .width(getDateWidth()),
        text = grid.text,
        color = if (grid.visible) {
            Color.Gray
        } else {
            Color.Transparent
        }
    )
}

@Composable
fun NormalGrid(grid: Grid) {
    Text(
        textAlign = TextAlign.Center,
        modifier = Modifier
            .border(width = 1.dp, Color.Black)
            .width(getNumberWidth()),
        text = grid.text,
        color = if (grid.visible) {
            Color.Unspecified
        } else {
            Color.Transparent
        }
    )
}

@Composable
fun SpecialGrid(grid: Grid) {
    Text(
        textAlign = TextAlign.Center,
        modifier = Modifier
            .border(width = 1.dp, Color.Black)
            .width(getNumberWidth()),
        text = grid.text,
        color = if (grid.visible) {
            Color.Red
        } else {
            Color.Transparent
        }
    )
}

