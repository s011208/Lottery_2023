package com.example.myapplication.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.data.LotteryType
import com.example.myapplication.R
import com.example.service.cache.SortType
import com.example.myapplication.compose.ViewModelStateMapper.mapToUiState
import com.example.myapplication.compose.appsettings.FontSettingsDialog
import com.example.myapplication.compose.general.AppToolbarSettingsDropDownText
import com.example.myapplication.compose.general.AppToolbarSettingsText
import com.example.myapplication.vm.MyEvents
import com.example.myapplication.vm.MyViewModel
import com.example.service.cache.DisplayOrder
import org.koin.java.KoinJavaComponent

private val PADDING = 4

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
                    ScrollToBottom(viewModel)
                    ScrollToTop(viewModel)
                    LotteryTypeDropdownMenu(value, viewModel)
                    SortTypeDropdownMenu(value, viewModel)
                    DisplayOrderDropdownMenu(value, viewModel)
                    SettingsDropdownMenu(value, viewModel)
                }
                else -> {}
            }
        }
    )
}

@Composable
fun ScrollToBottom(viewModel: MyViewModel) {
    AppToolbarSettingsText(
        text = "Scroll to bottom",
        Modifier.clickable { viewModel.handleEvent(MyEvents.ScrollToBottom) })
}

@Composable
fun ScrollToTop(viewModel: MyViewModel) {
    AppToolbarSettingsText(
        text = "Scroll to top",
        Modifier.clickable { viewModel.handleEvent(MyEvents.ScrollToTop) })
}

@Composable
private fun LotteryTypeDropdownMenu(
    value: UiState.Show,
    viewModel: MyViewModel
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    val type = viewModel.viewModelState.collectAsState().value.lotteryType

    Box(modifier = Modifier.padding(PADDING.dp)) {
        AppToolbarSettingsText(value.lotteryType.name, Modifier.clickable { expanded = true })

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
                    text = {
                        AppToolbarSettingsDropDownText(
                            text = itemValue.name,
                        )
                    },
                    trailingIcon = if (type == itemValue) {
                        {
                            Icon(
                                Icons.Rounded.Check,
                                stringResource(id = R.string.check_icon_description),
                                modifier = Modifier
                                    .padding(start = 4.dp)
                            )
                        }
                    } else null
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

    val type = viewModel.viewModelState.collectAsState().value.sortType

    Box(modifier = Modifier.padding(PADDING.dp)) {
        AppToolbarSettingsText(value.sortType.name, Modifier.clickable { expanded = true })

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
                    text = {
                        AppToolbarSettingsDropDownText(
                            text = itemValue.name
                        )
                    },
                    trailingIcon = if (type == itemValue) {
                        {
                            Icon(
                                Icons.Rounded.Check,
                                stringResource(id = R.string.check_icon_description),
                                modifier = Modifier
                                    .padding(start = 4.dp)
                            )
                        }
                    } else null
                )
            }
        }
    }
}

@Composable
private fun DisplayOrderDropdownMenu(
    value: UiState.Show,
    viewModel: MyViewModel
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    val type = viewModel.viewModelState.collectAsState().value.displayOrder

    Box(modifier = Modifier.padding(PADDING.dp)) {
        AppToolbarSettingsText(value.displayOrder.name, Modifier.clickable { expanded = true })

        DropdownMenu(expanded = expanded, onDismissRequest = {
            expanded = false
        }) {
            DisplayOrder.values().forEachIndexed { itemIndex, itemValue ->
                DropdownMenuItem(
                    onClick = {
                        viewModel.handleEvent(MyEvents.ChangeDisplayOrder(itemValue))
                        expanded = false
                    },
                    enabled = true,
                    text = { AppToolbarSettingsDropDownText(text = itemValue.name) },
                    trailingIcon = if (type == itemValue) {
                        {
                            Icon(
                                Icons.Rounded.Check,
                                stringResource(id = R.string.check_icon_description),
                                modifier = Modifier
                                    .padding(start = 4.dp)
                            )
                        }
                    } else null
                )
            }
        }
    }
}

@Composable
private fun SettingsDropdownMenu(
    value: UiState.Show,
    viewModel: MyViewModel
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    var fontSizeDialogOpen = remember {
        mutableStateOf(false)
    }

    Box(modifier = Modifier.padding(PADDING.dp)) {
        AppToolbarSettingsText("Settings", Modifier.clickable { expanded = true })

        DropdownMenu(expanded = expanded, onDismissRequest = {
            expanded = false
        }) {
            AppToolbarSettings.values().forEachIndexed { itemIndex, itemValue ->
                DropdownMenuItem(
                    onClick = {
                        if (itemValue == AppToolbarSettings.FONT_SIZE) {
                            fontSizeDialogOpen.value = true
                        }
                        expanded = false
                    },
                    enabled = true,
                    text = { AppToolbarSettingsDropDownText(text = itemValue.name) },
                )
            }
        }
    }

    FontSettingsDialog(fontSizeDialogOpen)
}

enum class AppToolbarSettings {
    FONT_SIZE,
}