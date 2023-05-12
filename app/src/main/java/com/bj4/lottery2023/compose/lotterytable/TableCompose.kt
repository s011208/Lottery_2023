package com.bj4.lottery2023.compose.lotterytable

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bj4.lottery2023.ImmutableListWrapper
import com.bj4.lottery2023.compose.general.Grid
import com.bj4.lottery2023.compose.general.GridFactory
import com.bj4.lottery2023.compose.general.Row
import com.bj4.lottery2023.compose.lotterytable.vm.LotteryTableEvents
import com.bj4.lottery2023.compose.lotterytable.vm.LotteryTableViewModel
import org.koin.java.KoinJavaComponent

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

    LaunchedEffect(rowListWrapper) {
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
