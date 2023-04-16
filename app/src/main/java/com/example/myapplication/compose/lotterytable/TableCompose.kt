package com.example.myapplication.compose

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.compose.lotterytable.vm.*
import org.koin.java.KoinJavaComponent
import timber.log.Timber

private const val GRID_HORIZONTAL_PADDING = 4

@Composable
fun LotteryTable(
    rowList: List<Row>,
    tableType: TableType,
) {
    val horizontalScrollState = rememberScrollState(0)
    val lazyListState = rememberLazyListState(0)
    val viewModel: LotteryTableViewModel by KoinJavaComponent.inject(LotteryTableViewModel::class.java)
    val fontSize: MutableState<Int> =
        remember { mutableStateOf(viewModel.viewModelState.value.fontSize) }

    LaunchedEffect("Table") {
        viewModel.eventStateSharedFlow.collect { myEvent ->
            when (myEvent) {
                LotteryTableEvents.ScrollToBottom -> {
                    lazyListState.scrollToItem(rowList.size)
                }
                LotteryTableEvents.ScrollToTop -> {
                    lazyListState.scrollToItem(0)
                }
                is LotteryTableEvents.FontSizeChanged -> {
                    fontSize.value = myEvent.fontSize
                }
                else -> {}
            }
        }
    }

    when (tableType) {
        TableType.NORMAL -> NormalLotteryTable(
            rowList,
            horizontalScrollState,
            fontSize,
            lazyListState
        )
        TableType.LIST -> ListLotteryTable(
            rowList,
            horizontalScrollState,
            fontSize,
            lazyListState
        )
    }
}

@Composable
private fun NormalLotteryTable(
    rowList: List<Row>,
    horizontalScrollState: ScrollState,
    fontSize: MutableState<Int>,
    lazyListState: LazyListState
) {
    Column {
        if (rowList.isNotEmpty()) {
            val first = rowList.first()
            Column(modifier = Modifier.horizontalScroll(horizontalScrollState)) {
                RowFactory(
                    first,
                    fontSize.value,
                )
            }
        }

        LazyColumn(
            state = lazyListState,
            modifier = Modifier.horizontalScroll(horizontalScrollState)
        ) {
            rowList.forEachIndexed { index, row ->
                if (index == 0) return@forEachIndexed
                item {
                    RowFactory(row, fontSize.value)
                }
            }
        }
    }
}

@Composable
private fun ListLotteryTable(
    rowList: List<Row>,
    horizontalScrollState: ScrollState,
    fontSize: MutableState<Int>,
    lazyListState: LazyListState
) {
    Column {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.horizontalScroll(horizontalScrollState)
        ) {
            rowList.forEachIndexed { index, row ->
                Timber.v("row: $row")
                if (index == 0) return@forEachIndexed
                item {
                    RowFactory(row, fontSize.value)
                }
            }
        }
    }
}


@OptIn(ExperimentalTextApi::class)
@Composable
private fun getDateWidth(fontSize: Int): Dp {
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
private fun getNumberWidth(fontSize: Int): Dp {
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

@Composable
fun RowFactory(row: Row, fontSize: Int, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        row.dataList.forEach { grid -> GridFactory(grid, row.type, fontSize) }
    }
}

@Composable
fun GridFactory(grid: Grid, rowType: Row.Type, fontSize: Int) {
    when (grid.type) {
        Grid.Type.Normal -> NormalGrid(grid, fontSize)
        Grid.Type.Date -> DateGrid(grid, fontSize)
        Grid.Type.Special -> SpecialGrid(grid, fontSize)
    }
}


@Composable
fun DateGrid(grid: Grid, fontSize: Int) {
    Text(
        textAlign = TextAlign.Center,
        modifier = Modifier
            .border(width = 1.dp, Color.Black)
            .width(getDateWidth(fontSize)),
        text = grid.text,
        color = if (grid.visible) {
            Color.Gray
        } else {
            Color.Transparent
        },
        fontSize = fontSize.sp
    )
}

@Composable
fun NormalGrid(grid: Grid, fontSize: Int) {
    Text(
        text = grid.text,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .border(width = 1.dp, Color.Black)
            .width(getNumberWidth(fontSize)),
        color = if (grid.visible) {
            Color.Unspecified
        } else {
            Color.Transparent
        },
        fontSize = fontSize.sp
    )
}

@Composable
fun SpecialGrid(grid: Grid, fontSize: Int) {
    Text(
        textAlign = TextAlign.Center,
        modifier = Modifier
            .border(width = 1.dp, Color.Black)
            .width(getNumberWidth(fontSize)),
        text = grid.text,
        color = if (grid.visible) {
            Color.Red
        } else {
            Color.Transparent
        },
        fontSize = fontSize.sp
    )
}

