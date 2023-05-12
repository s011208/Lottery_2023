package com.bj4.lottery2023.compose.plusminus

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bj4.lottery2023.compose.general.GridFactory
import com.bj4.lottery2023.compose.plusminus.vm.PlusMinusEvent
import com.bj4.lottery2023.compose.plusminus.vm.PlusMinusViewModel
import org.koin.java.KoinJavaComponent

@Composable
fun PlusMinusScreen() {
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

    Row {
        PlusMinusTable(
            lazyListState = lazyListState,
            stateValue = state.value,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = state.value.fontSize,
            extraSpacing = state.value.spacing
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
) {
    LazyColumn(
        content = {
            stateValue.rowListWrapper.wrapper.forEach { row ->
                item {
                    Row {
                        row.dataList.forEach { grid ->
                            GridFactory(
                                grid = grid,
                                fontSize = fontSize,
                                extraSpacing = extraSpacing
                            )
                        }
                    }
                }
            }
        },
        state = lazyListState,
        modifier = modifier
    )
}

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

        Column {
            Row {
                RadioButton(
                    selected = plusMinusValue.value == 0,
                    onClick = {
                        plusMinusValue.value = 0
                        clickEvent()
                    },
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Text(text = "0", modifier = Modifier.align(Alignment.CenterVertically))
            }
            Row {
                RadioButton(
                    selected = plusMinusValue.value == 1,
                    onClick = {
                        plusMinusValue.value = 1
                        clickEvent()
                    },
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Text(text = "1", modifier = Modifier.align(Alignment.CenterVertically))
            }
            Row {
                RadioButton(
                    selected = plusMinusValue.value == 2,
                    onClick = {
                        plusMinusValue.value = 2
                        clickEvent()
                    },
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Text(text = "2", modifier = Modifier.align(Alignment.CenterVertically))
            }
            Row {
                RadioButton(
                    selected = plusMinusValue.value == 3,
                    onClick = {
                        plusMinusValue.value = 3
                        clickEvent()
                    },
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Text(text = "3", modifier = Modifier.align(Alignment.CenterVertically))
            }
            Row {
                RadioButton(
                    selected = plusMinusValue.value == 4,
                    onClick = {
                        plusMinusValue.value = 4
                        clickEvent()
                    },
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Text(text = "4", modifier = Modifier.align(Alignment.CenterVertically))
            }
            Row {
                RadioButton(
                    selected = plusMinusValue.value == 5,
                    onClick = {
                        plusMinusValue.value = 5
                        clickEvent()
                    },
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Text(text = "5", modifier = Modifier.align(Alignment.CenterVertically))
            }
            Row {
                RadioButton(
                    selected = plusMinusValue.value == 6,
                    onClick = {
                        plusMinusValue.value = 6
                        clickEvent()
                    },
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Text(text = "6", modifier = Modifier.align(Alignment.CenterVertically))
            }
            Row {
                RadioButton(
                    selected = plusMinusValue.value == 7,
                    onClick = {
                        plusMinusValue.value = 7
                        clickEvent()
                    },
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Text(text = "7", modifier = Modifier.align(Alignment.CenterVertically))
            }
            Row {
                RadioButton(
                    selected = plusMinusValue.value == 8,
                    onClick = {
                        plusMinusValue.value = 8
                        clickEvent()
                    },
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Text(text = "8", modifier = Modifier.align(Alignment.CenterVertically))
            }
            Row {
                RadioButton(
                    selected = plusMinusValue.value == 9,
                    onClick = {
                        plusMinusValue.value = 9
                        clickEvent()
                    },
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Text(text = "9", modifier = Modifier.align(Alignment.CenterVertically))
            }
        }
    }
}