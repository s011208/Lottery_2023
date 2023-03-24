package com.example.myapplication.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.LotteryType
import com.example.myapplication.SortType
import com.example.myapplication.compose.ViewModelStateMapper.mapToUiState
import com.example.myapplication.vm.MyEvents
import com.example.myapplication.vm.MyViewModel
import com.example.myapplication.vm.ViewModelState
import org.koin.java.KoinJavaComponent

@Composable
fun AppToolbar() {
    val viewModel: MyViewModel by KoinJavaComponent.inject(MyViewModel::class.java)
    val state = viewModel.viewModelState.collectAsState()
    val value = state.value.mapToUiState()

    SmallTopAppBar(
        title = {
            Text(
                text = when (value) {
                    UiState.Empty -> "Empty"
                    is UiState.Show -> value.lotteryType.toString()
                    is UiState.Loading -> value.hint
                }
            )
        },
        actions = {
            when (value) {
                is UiState.Show -> {
                    LotteryTypeDropdownMenu(value, viewModel)
                    SortTypeDropdownMenu(value, viewModel)
                }
                else -> {}
            }
        }
    )
}

@Composable
private fun LotteryTypeDropdownMenu(
    value: UiState.Show,
    viewModel: MyViewModel
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    Box(modifier = Modifier.padding(4.dp)) {
        Text(
            text = value.lotteryType.name,
            modifier = Modifier.clickable { expanded = true }
        )

        DropdownMenu(expanded = expanded, onDismissRequest = {
            expanded = false
        }) {
            LotteryType.values().forEachIndexed { itemIndex, itemValue ->
                DropdownMenuItem(
                    onClick = {
                        viewModel.handleEvent(MyEvents.ChangeLotteryType(itemValue))
                        expanded = false
                    },
                    enabled = true,
                    text = { Text(text = itemValue.name) }
                )
            }
        }
    }
}

@Composable
private fun SortTypeDropdownMenu(
    value: UiState.Show,
    viewModel: MyViewModel
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    Box(modifier = Modifier.padding(4.dp)) {
        Text(
            text = value.sortType.name,
            modifier = Modifier.clickable { expanded = true }
        )

        DropdownMenu(expanded = expanded, onDismissRequest = {
            expanded = false
        }) {
            SortType.values().forEachIndexed { itemIndex, itemValue ->
                DropdownMenuItem(
                    onClick = {
                        viewModel.handleEvent(MyEvents.ChangeSortType(itemValue))
                        expanded = false
                    },
                    enabled = true,
                    text = { Text(text = itemValue.name) }
                )
            }
        }
    }
}
