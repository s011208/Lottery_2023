package com.bj4.lottery2023.compose.lotterytable

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bj4.lottery2023.ImmutableListWrapper
import com.bj4.lottery2023.compose.lotterytable.vm.Grid
import com.bj4.lottery2023.compose.lotterytable.vm.LotteryTableEvents
import com.bj4.lottery2023.compose.lotterytable.vm.LotteryTableViewModel
import com.bj4.lottery2023.compose.lotterytable.vm.Row
import org.koin.java.KoinJavaComponent

private const val GRID_HORIZONTAL_PADDING = 4

private const val LIST_LOTTERY_TABLE_FONT_SIZE_RATIO = 1.5f

@Composable
fun LotteryTable(
    rowListWrapper: ImmutableListWrapper<Row>,
    tableType: TableType,
    extraSpacing: Int,
    showDivider: Boolean,
) {
    val horizontalScrollState = rememberScrollState(0)
    val lazyListState = rememberLazyListState(0)
    val viewModel: LotteryTableViewModel by KoinJavaComponent.inject(LotteryTableViewModel::class.java)
    val fontSize: MutableState<Int> =
        remember { mutableStateOf(viewModel.viewModelState.value.fontSize) }
    val clickedDate: MutableState<String> = remember {
        mutableStateOf(UNDEF)
    }

    LaunchedEffect("Table") {
        viewModel.eventStateSharedFlow.collect { myEvent ->
            when (myEvent) {
                LotteryTableEvents.ScrollToBottom -> {
                    lazyListState.scrollToItem(rowListWrapper.wrapper.size)
                }

                LotteryTableEvents.ScrollToTop -> {
                    lazyListState.scrollToItem(0)
                }

                is LotteryTableEvents.FontSizeChanged -> {
                    fontSize.value = myEvent.fontSize
                }

                is LotteryTableEvents.ChangeLotteryType -> {
                    clickedDate.value = UNDEF
                }

                else -> {}
            }
        }
    }

    when (tableType) {
        TableType.NORMAL -> NormalLotteryTable(
            rowListWrapper,
            horizontalScrollState,
            fontSize,
            lazyListState,
            extraSpacing,
            clickedDate,
            showDivider,
        )

        TableType.LIST -> ListLotteryTable(
            rowListWrapper,
            horizontalScrollState,
            fontSize,
            lazyListState,
            extraSpacing,
            clickedDate,
            showDivider,
        )
    }
}

@Composable
private fun NormalLotteryTable(
    rowListWrapper: ImmutableListWrapper<Row>,
    horizontalScrollState: ScrollState,
    fontSize: MutableState<Int>,
    lazyListState: LazyListState,
    extraSpacing: Int,
    clickedDate: MutableState<String>,
    showDivider: Boolean,
) {
    var width by remember { mutableStateOf(0) }
    val density = LocalDensity.current
    val widthDp = remember(width, density) { with(density) { width.toDp() } }

    Column {
        if (rowListWrapper.wrapper.isNotEmpty()) {
            val first = rowListWrapper.wrapper.first()
            Column(modifier = Modifier.horizontalScroll(horizontalScrollState)) {
                RowFactory(
                    row = first,
                    fontSize = fontSize.value,
                    extraSpacing = extraSpacing,
                    widthDp = widthDp,
                )
            }
        }

        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .horizontalScroll(horizontalScrollState)
                .onSizeChanged {
                    width = it.width
                }
        ) {
            rowListWrapper.wrapper.forEachIndexed { index, row ->
                if (index == 0) return@forEachIndexed
                item {
                    RowFactory(
                        row = row,
                        fontSize = fontSize.value,
                        extraSpacing = extraSpacing,
                        clickedDate = clickedDate,
                        showDivider = showDivider,
                        widthDp = widthDp,
                    )
                }
            }
        }
    }
}

@Composable
private fun ListLotteryTable(
    rowListWrapper: ImmutableListWrapper<Row>,
    horizontalScrollState: ScrollState,
    fontSize: MutableState<Int>,
    lazyListState: LazyListState,
    extraSpacing: Int,
    clickedDate: MutableState<String>,
    showDivider: Boolean,
) {
    var width by remember { mutableStateOf(0) }
    val density = LocalDensity.current
    val widthDp = remember(width, density) { with(density) { width.toDp() } }

    Column {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .horizontalScroll(horizontalScrollState)
                .onSizeChanged {
                    width = it.width
                }
        ) {
            rowListWrapper.wrapper.forEachIndexed { index, row ->
                if (index == 0) return@forEachIndexed
                item {
                    RowFactory(
                        row = row,
                        fontSize = fontSize.value,
                        fontSizeRatio = LIST_LOTTERY_TABLE_FONT_SIZE_RATIO,
                        extraSpacing = extraSpacing,
                        clickedDate = clickedDate,
                        showDivider = showDivider,
                        widthDp = widthDp,
                    )
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

@OptIn(ExperimentalTextApi::class)
@Composable
private fun getPossibilityNumberWidth(fontSize: Int): Dp {
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

@Composable
fun RowFactory(
    row: Row,
    fontSize: Int,
    modifier: Modifier = Modifier,
    fontSizeRatio: Float = 1f,
    extraSpacing: Int,
    clickedDate: MutableState<String> = remember {
        mutableStateOf(UNDEF)
    },
    showDivider: Boolean = false,
    widthDp: Dp = 0.dp,
) {
    val canShowDivider = showDivider && row.type == Row.Type.MonthlyTotal
    val canShowBottomDivider = row.type == Row.Type.Header

    if (canShowDivider) {
        RowFactoryWithTopAndBottomDivider(
            row,
            fontSize,
            modifier,
            fontSizeRatio,
            extraSpacing,
            clickedDate,
            widthDp
        )
    } else if (canShowBottomDivider) {
        RowFactoryWithBottomDivider(
            row,
            fontSize,
            modifier,
            fontSizeRatio,
            extraSpacing,
            clickedDate,
            widthDp
        )
    } else {
        RowFactoryWithoutDivider(row, fontSize, modifier, fontSizeRatio, extraSpacing, clickedDate)
    }
}

@Composable
fun RowFactoryWithoutDivider(
    row: Row,
    fontSize: Int,
    modifier: Modifier = Modifier,
    fontSizeRatio: Float = 1f,
    extraSpacing: Int,
    clickedDate: MutableState<String> = remember {
        mutableStateOf(UNDEF)
    },
) {
    val rowDate = row.dataList.firstOrNull { it.type == Grid.Type.Date }?.text ?: UNKNOWN

    Row(
        modifier = modifier
            .clickable {
                if (row.type == Row.Type.Header) {
                    return@clickable
                }
                if (clickedDate.value == rowDate) {
                    clickedDate.value = UNDEF
                } else {
                    clickedDate.value = rowDate
                }
            }
            .background(
                if (clickedDate.value == rowDate) {
                    MaterialTheme.colorScheme.secondary.copy(alpha = .3f)
                } else {
                    MaterialTheme.colorScheme.background
                }
            )
    ) {
        row.dataList.forEach { grid ->
            GridFactory(
                grid,
                fontSize,
                fontSizeRatio,
                extraSpacing
            )
        }
    }
}

@Composable
fun RowFactoryWithBottomDivider(
    row: Row,
    fontSize: Int,
    modifier: Modifier = Modifier,
    fontSizeRatio: Float = 1f,
    extraSpacing: Int,
    clickedDate: MutableState<String> = remember {
        mutableStateOf(UNDEF)
    },
    widthDp: Dp = 0.dp,
) {
    Column {
        RowFactoryWithoutDivider(row, fontSize, modifier, fontSizeRatio, extraSpacing, clickedDate)
        MonthlyTotalDivider(modifier.width(widthDp))
    }
}

@Composable
fun RowFactoryWithTopAndBottomDivider(
    row: Row,
    fontSize: Int,
    modifier: Modifier = Modifier,
    fontSizeRatio: Float = 1f,
    extraSpacing: Int,
    clickedDate: MutableState<String> = remember {
        mutableStateOf(UNDEF)
    },
    widthDp: Dp = 0.dp,
) {
    Column {
        MonthlyTotalDivider(modifier.width(widthDp))
        RowFactoryWithoutDivider(row, fontSize, modifier, fontSizeRatio, extraSpacing, clickedDate)
        MonthlyTotalDivider(modifier.width(widthDp))
    }
}

@Composable
private fun MonthlyTotalDivider(modifier: Modifier = Modifier) {
    Divider(
        color = MaterialTheme.colorScheme.inversePrimary,
        thickness = 1.dp,
        modifier = modifier
    )
}

private const val UNDEF = "UNDEF"
private const val UNKNOWN = "UNKNOWN"

@Composable
fun GridFactory(grid: Grid, fontSize: Int, fontSizeRatio: Float = 1f, extraSpacing: Int) {
    when (grid.type) {
        Grid.Type.Normal -> NormalGrid(grid, fontSize, fontSizeRatio, extraSpacing)
        Grid.Type.Date -> DateGrid(grid, fontSize, fontSizeRatio, extraSpacing)
        Grid.Type.Special -> SpecialGrid(grid, fontSize, fontSizeRatio, extraSpacing)
        Grid.Type.NormalPossibility -> NormalPossibilityGrid(
            grid,
            fontSize,
            fontSizeRatio,
            extraSpacing
        )

        Grid.Type.SpecialPossibility -> SpecialPossibilityGrid(
            grid,
            fontSize,
            fontSizeRatio,
            extraSpacing
        )

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
        text = grid.text,
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
        text = grid.text,
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
        text = grid.text,
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
fun NormalPossibilityGrid(grid: Grid, fontSize: Int, fontSizeRatio: Float = 1f, extraSpacing: Int) {
    Text(
        text = grid.text,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .border(width = 1.dp, Color.Black)
            .width(getPossibilityNumberWidth(fontSize) + (2 * extraSpacing).dp),
        color = if (grid.visible) {
            Color.Unspecified
        } else {
            Color.Transparent
        },
        fontSize = fontSize.sp * fontSizeRatio
    )
}

private val SPECIAL_COLOR = Color.Blue

@Composable
fun SpecialGrid(grid: Grid, fontSize: Int, fontSizeRatio: Float = 1f, extraSpacing: Int) {
    Text(
        textAlign = TextAlign.Center,
        modifier = Modifier
            .border(width = 1.dp, Color.Black)
            .width(getNumberWidth(fontSize) + (2 * extraSpacing).dp),
        text = grid.text,
        color = if (grid.visible) {
            SPECIAL_COLOR
        } else {
            Color.Transparent
        },
        fontSize = fontSize.sp
    )
}

@Composable
fun SpecialLastGrid(grid: Grid, fontSize: Int, fontSizeRatio: Float = 1f, extraSpacing: Int) {
    Text(
        textAlign = TextAlign.Center,
        modifier = Modifier
            .border(width = 1.dp, Color.Black)
            .width(getNumberWidth(fontSize) + (2 * extraSpacing).dp),
        text = grid.text,
        color = if (grid.visible) {
            SPECIAL_COLOR
        } else {
            Color.Transparent
        },
        fontSize = fontSize.sp
    )
}

@Composable
fun SpecialPossibilityGrid(
    grid: Grid,
    fontSize: Int,
    fontSizeRatio: Float = 1f,
    extraSpacing: Int
) {
    Text(
        textAlign = TextAlign.Center,
        modifier = Modifier
            .border(width = 1.dp, Color.Black)
            .width(getPossibilityNumberWidth(fontSize) + (2 * extraSpacing).dp),
        text = grid.text,
        color = if (grid.visible) {
            SPECIAL_COLOR
        } else {
            Color.Transparent
        },
        fontSize = fontSize.sp
    )
}

