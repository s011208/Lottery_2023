package com.bj4.lottery2023.compose.plusminus

import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bj4.lottery2023.compose.general.Grid
import com.bj4.lottery2023.compose.general.GridFactory
import com.bj4.lottery2023.compose.general.Row
import com.bj4.lottery2023.compose.plusminus.vm.PlusMinusEvent
import com.bj4.lottery2023.compose.plusminus.vm.PlusMinusViewModel
import com.example.analytics.Analytics
import org.koin.java.KoinJavaComponent

@Composable
fun PlusMinusScreen(navController: NavController = rememberNavController()) {
    LaunchedEffect(key1 = Unit) {
        val analytics: Analytics by KoinJavaComponent.inject(Analytics::class.java)
        analytics.trackScreen("PlusMinusScreen")
    }

    Scaffold(
        topBar = { PlusMinusToolbar(navController) },
        content = { paddingValues -> PlusMinusContent(Modifier.padding(paddingValues)) }
    )
}

@Composable
fun PlusMinusContent(modifier: Modifier) {
    val viewModel: PlusMinusViewModel by KoinJavaComponent.inject(PlusMinusViewModel::class.java)
    val lazyListState = rememberLazyListState(0)
    val plusMinusSignValue = remember {
        mutableStateOf(
            if (viewModel.viewModelState.value.deltaValue >= 0) {
                1
            } else {
                -1
            }
        )
    }
    val plusMinusValue = remember {
        mutableStateOf(Math.abs(viewModel.viewModelState.value.deltaValue))
    }

    val state = viewModel.viewModelState.collectAsState()

    Row(modifier = modifier) {
        PlusMinusTable(
            lazyListState = lazyListState,
            stateValue = state.value,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = state.value.fontSize,
            extraSpacing = state.value.spacing,
            showDivider = state.value.showDivideLine,
        )

        PlusMinusGroup(
            modifier = Modifier.padding(vertical = 8.dp),
            plusMinusSignValue = plusMinusSignValue,
            plusMinusValue = plusMinusValue,
        )
    }

    LaunchedEffect(key1 = state) {
        viewModel.eventStateSharedFlow.collect { event ->
            when (event) {
                PlusMinusEvent.ScrollToBottom -> {
                    lazyListState.scrollToItem(viewModel.viewModelState.value.rowListWrapper.wrapper.size)
                }

                PlusMinusEvent.ScrollToTop -> {
                    lazyListState.scrollToItem(0)
                }

                else -> {}
            }
        }
    }
}


@Composable
fun PlusMinusTable(
    lazyListState: LazyListState,
    stateValue: PlusMinusViewModel.State,
    modifier: Modifier,
    fontSize: Int,
    extraSpacing: Int,
    showDivider: Boolean,
) {
    val horizontalScrollState = rememberScrollState(0)

    LazyColumn(
        content = {
            stateValue.rowListWrapper.wrapper.forEach { row ->
                item {
                    Row(
                        modifier = if (row.type == Row.Type.MonthlyTotal && showDivider) {
                            Modifier.border(2.dp, Color.Red)
                        } else {
                            Modifier
                        }
                    ) {
                        row.dataList.forEach { grid ->
                            GridFactory(
                                grid = grid,
                                fontSize = fontSize,
                                extraSpacing = extraSpacing,
                                textColor = if (grid.type == Grid.Type.Special) {
                                    Color.Red
                                } else {
                                    Color.Unspecified
                                }
                            )
                        }
                    }
                }
            }
        },
        state = lazyListState,
        modifier = modifier
            .horizontalScroll(horizontalScrollState)
    )
}

private const val RADIO_GROUP_COLUMN_COUNT = 3
private const val RADIO_GROUP_ROW_COUNT = 10

@Composable
fun PlusMinusGroup(
    modifier: Modifier,
    plusMinusSignValue: MutableState<Int>,
    plusMinusValue: MutableState<Int>
) {
    val viewModel: PlusMinusViewModel by KoinJavaComponent.inject(PlusMinusViewModel::class.java)
    val clickEvent =
        { viewModel.handleEvent(PlusMinusEvent.ChangeDeltaValue(plusMinusSignValue.value * plusMinusValue.value)) }

    Row(modifier = modifier) {
        Column {
            Row {
                RadioButton(
                    selected = plusMinusSignValue.value > 0,
                    onClick = {
                        plusMinusSignValue.value = 1
                        clickEvent()
                    },
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Text(text = "+", modifier = Modifier.align(Alignment.CenterVertically))
            }

            Row {
                RadioButton(
                    selected = plusMinusSignValue.value < 0,
                    onClick = {
                        plusMinusSignValue.value = -1
                        clickEvent()
                    },
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Text(text = "-", modifier = Modifier.align(Alignment.CenterVertically))
            }
        }

        for (columnCount in 0 until RADIO_GROUP_COLUMN_COUNT) {
            Column {
                for (rowCount in 0 until RADIO_GROUP_ROW_COUNT) {
                    val buttonValue = columnCount * 10 + rowCount
                    Row {
                        RadioButton(
                            selected = plusMinusValue.value == buttonValue,
                            onClick = {
                                plusMinusValue.value = buttonValue
                                clickEvent()
                            },
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                        Text(
                            text = buttonValue.toString(),
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }
            }
        }
    }
}