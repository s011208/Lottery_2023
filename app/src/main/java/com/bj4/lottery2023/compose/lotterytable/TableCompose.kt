package com.bj4.lottery2023.compose.lotterytable

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bj4.lottery2023.ImmutableListWrapper
import com.bj4.lottery2023.compose.SPECIAL_COLOR
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
    Column {
        if (rowListWrapper.wrapper.isNotEmpty()) {
            val first = rowListWrapper.wrapper.first()
            Column(modifier = Modifier.horizontalScroll(horizontalScrollState)) {
                RowFactory(
                    row = first,
                    fontSize = fontSize.value,
                    extraSpacing = extraSpacing,
                )
            }
        }

        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .horizontalScroll(horizontalScrollState)
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
    Column {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .horizontalScroll(horizontalScrollState)
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
) {
    val canShowDivider = showDivider && (row.type == Row.Type.MonthlyTotal ||  row.type == Row.Type.Header)

    if (canShowDivider) {
        RowFactoryWithDivider(
            row,
            fontSize,
            modifier,
            fontSizeRatio,
            extraSpacing,
            clickedDate,
        )
    } else {
        RowFactory(row, fontSize, modifier, fontSizeRatio, extraSpacing, clickedDate)
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
fun RowFactoryWithDivider(
    row: Row,
    fontSize: Int,
    modifier: Modifier = Modifier,
    fontSizeRatio: Float = 1f,
    extraSpacing: Int,
    clickedDate: MutableState<String> = remember {
        mutableStateOf(UNDEF)
    },
) {
    Column {
        RowFactory(row, fontSize, modifier.border(1.dp, SPECIAL_COLOR), fontSizeRatio, extraSpacing, clickedDate)
    }
}

private const val UNDEF = "UNDEF"
private const val UNKNOWN = "UNKNOWN"
