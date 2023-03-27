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

private const val PADDING = 4

@Composable
fun AppToolbar() {
    val viewModel: MyViewModel by KoinJavaComponent.inject(MyViewModel::class.java)
    val state = viewModel.viewModelState.collectAsState()
    val value = state.value.mapToUiState()

    SmallTopAppBar(title = {
        Text(
            text = when (value) {
                UiState.Empty -> "Empty"
                is UiState.Show -> state.value.lotteryType.toUiString()
            }
        )
    }, actions = {
        when (value) {
            is UiState.Show -> {
                ScrollToBottom(viewModel)
                ScrollToTop(viewModel)
                LotteryTypeDropdownMenu(viewModel)
                SortTypeDropdownMenu(viewModel)
                DisplayOrderDropdownMenu(viewModel)
                SettingsDropdownMenu()
            }
            else -> {}
        }
    })
}

@Composable
fun ScrollToBottom(viewModel: MyViewModel) {
    AppToolbarSettingsText(text = stringResource(id = R.string.scroll_to_bottom),
        Modifier.clickable { viewModel.handleEvent(MyEvents.ScrollToBottom) })
}

@Composable
fun ScrollToTop(viewModel: MyViewModel) {
    AppToolbarSettingsText(text = stringResource(id = R.string.scroll_to_top),
        Modifier.clickable { viewModel.handleEvent(MyEvents.ScrollToTop) })
}

@Composable
private fun LotteryTypeDropdownMenu(
    viewModel: MyViewModel
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    val type = viewModel.viewModelState.collectAsState().value.lotteryType

    Box(modifier = Modifier.padding(PADDING.dp)) {
        AppToolbarSettingsText(
            stringResource(id = R.string.lottery_type),
            Modifier.clickable { expanded = true })

        DropdownMenu(expanded = expanded, onDismissRequest = {
            expanded = false
        }) {
            LotteryType.values().forEachIndexed { itemIndex, itemValue ->
                DropdownMenuItem(onClick = {
                    viewModel.handleEvent(MyEvents.ChangeLotteryType(itemValue))
                    expanded = false
                }, enabled = true, text = {
                    AppToolbarSettingsDropDownText(
                        text = itemValue.toUiString(),
                    )
                }, trailingIcon = if (type == itemValue) {
                    {
                        Icon(
                            Icons.Rounded.Check,
                            stringResource(id = R.string.check_icon_description),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                } else null)
            }
        }
    }
}

@Composable
private fun SortTypeDropdownMenu(
    viewModel: MyViewModel
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    val type = viewModel.viewModelState.collectAsState().value.sortType

    Box(modifier = Modifier.padding(PADDING.dp)) {
        AppToolbarSettingsText(
            stringResource(id = R.string.sort_type),
            Modifier.clickable { expanded = true })

        DropdownMenu(expanded = expanded, onDismissRequest = {
            expanded = false
        }) {
            SortType.values().forEachIndexed { itemIndex, itemValue ->
                DropdownMenuItem(onClick = {
                    viewModel.handleEvent(MyEvents.ChangeSortType(itemValue))
                    expanded = false
                }, enabled = true, text = {
                    AppToolbarSettingsDropDownText(
                        text = when (itemValue) {
                            SortType.NormalOrder -> stringResource(id = R.string.normal_order)
                            SortType.AddToTen -> stringResource(id = R.string.add_to_ten)
                            SortType.LastDigit -> stringResource(id = R.string.last_digit)
                        }
                    )
                }, trailingIcon = if (type == itemValue) {
                    {
                        Icon(
                            Icons.Rounded.Check,
                            stringResource(id = R.string.check_icon_description),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                } else null)
            }
        }
    }
}

@Composable
private fun DisplayOrderDropdownMenu(
    viewModel: MyViewModel
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    val type = viewModel.viewModelState.collectAsState().value.displayOrder

    Box(modifier = Modifier.padding(PADDING.dp)) {
        AppToolbarSettingsText(
            stringResource(id = R.string.display_order),
            Modifier.clickable { expanded = true })

        DropdownMenu(expanded = expanded, onDismissRequest = {
            expanded = false
        }) {
            DisplayOrder.values().forEachIndexed { itemIndex, itemValue ->
                DropdownMenuItem(onClick = {
                    viewModel.handleEvent(MyEvents.ChangeDisplayOrder(itemValue))
                    expanded = false
                },
                    enabled = true,
                    text = {
                        AppToolbarSettingsDropDownText(
                            text =
                            when (itemValue) {
                                DisplayOrder.DESCEND -> stringResource(id = R.string.descend)
                                DisplayOrder.ASCEND -> stringResource(id = R.string.ascend)
                            }
                        )
                    },
                    trailingIcon = if (type == itemValue) {
                        {
                            Icon(
                                Icons.Rounded.Check,
                                stringResource(id = R.string.check_icon_description),
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    } else null)
            }
        }
    }
}

@Composable
private fun SettingsDropdownMenu() {
    var expanded by remember {
        mutableStateOf(false)
    }

    var fontSizeDialogOpen = remember {
        mutableStateOf(false)
    }

    Box(modifier = Modifier.padding(PADDING.dp)) {
        AppToolbarSettingsText(
            stringResource(id = R.string.settings),
            Modifier.clickable { expanded = true })

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
                    text = {
                        AppToolbarSettingsDropDownText(
                            text =
                            when (itemValue) {
                                AppToolbarSettings.FONT_SIZE -> stringResource(id = R.string.font_size)
                            }
                        )
                    },
                )
            }
        }
    }

    FontSettingsDialog(fontSizeDialogOpen)
}

@Composable
private fun LotteryType.toUiString() = when (this) {
    LotteryType.Lto -> stringResource(id = R.string.lto)
    LotteryType.LtoBig -> stringResource(id = R.string.lto_big)
    LotteryType.LtoHK -> stringResource(id = R.string.lto_hk)
}

enum class AppToolbarSettings {
    FONT_SIZE,
}