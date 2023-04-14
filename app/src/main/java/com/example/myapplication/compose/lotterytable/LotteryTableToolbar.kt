package com.example.myapplication.compose.lotterytable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.data.LotteryType
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.compose.UiState
import com.example.myapplication.compose.ViewModelStateMapper.mapToUiState
import com.example.myapplication.compose.appsettings.DayNightSettingsDialog
import com.example.myapplication.compose.appsettings.FontSettingsDialog
import com.example.myapplication.compose.appsettings.ResetDatabase
import com.example.myapplication.compose.general.AppToolbarSettingsDropDownText
import com.example.myapplication.compose.general.AppToolbarSettingsText
import com.example.myapplication.compose.lotterylog.vm.LotteryLogUiEvent
import com.example.myapplication.compose.lotterylog.vm.LotteryLogViewModel
import com.example.myapplication.compose.lotterytable.vm.LotteryTableEvents
import com.example.myapplication.compose.lotterytable.vm.LotteryTableViewModel
import com.example.service.cache.DisplayOrder
import com.example.service.cache.SortType
import org.koin.java.KoinJavaComponent

private const val PADDING = 4

@Composable
fun LotteryTableToolbar(navController: NavController = rememberNavController()) {
    val viewModel: LotteryTableViewModel by KoinJavaComponent.inject(LotteryTableViewModel::class.java)
    val state = viewModel.viewModelState.collectAsState()
    val value = state.value.mapToUiState()

    val logViewModel: LotteryLogViewModel by KoinJavaComponent.inject(LotteryLogViewModel::class.java)

    SmallTopAppBar(title = {
        Text(
            text = when (value) {
                is UiState.Show -> state.value.lotteryType.toUiString()
                else -> ""
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
                SettingsDropdownMenu(
                    viewModel
                ) {
                    logViewModel.handleUiEvent(LotteryLogUiEvent.RequestData)
                    navController.navigate(MainActivity.SCREEN_NAME_LOTTERY_LOG)
                }
            }
            else -> {}
        }
    })
}

@Composable
fun ScrollToBottom(viewModel: LotteryTableViewModel) {
    AppToolbarSettingsText(text = stringResource(id = R.string.scroll_to_bottom),
        Modifier.clickable { viewModel.handleEvent(LotteryTableEvents.ScrollToBottom) })
}

@Composable
fun ScrollToTop(viewModel: LotteryTableViewModel) {
    AppToolbarSettingsText(text = stringResource(id = R.string.scroll_to_top),
        Modifier.clickable { viewModel.handleEvent(LotteryTableEvents.ScrollToTop) })
}

@Composable
private fun LotteryTypeDropdownMenu(
    viewModel: LotteryTableViewModel
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
                    viewModel.handleEvent(LotteryTableEvents.ChangeLotteryType(itemValue))
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
    viewModel: LotteryTableViewModel
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
                    viewModel.handleEvent(LotteryTableEvents.ChangeSortType(itemValue))
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
    viewModel: LotteryTableViewModel
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
                    viewModel.handleEvent(LotteryTableEvents.ChangeDisplayOrder(itemValue))
                    expanded = false
                }, enabled = true, text = {
                    AppToolbarSettingsDropDownText(
                        text = when (itemValue) {
                            DisplayOrder.DESCEND -> stringResource(id = R.string.descend)
                            DisplayOrder.ASCEND -> stringResource(id = R.string.ascend)
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
private fun SettingsDropdownMenu(
    viewModel: LotteryTableViewModel,
    onLotteryDataClick: () -> Unit = {}
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    var fontSizeDialogOpen = remember {
        mutableStateOf(false)
    }

    var resetDatabaseDialogOpen = remember {
        mutableStateOf(false)
    }

    var dayNightSettingsDialogOpen = remember {
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
                        when (itemValue) {
                            AppToolbarSettings.FONT_SIZE -> {
                                fontSizeDialogOpen.value = true
                            }
                            AppToolbarSettings.UPDATE_LTO -> {
                                viewModel.handleEvent(LotteryTableEvents.UpdateData)
                            }
                            AppToolbarSettings.RESET -> {
                                resetDatabaseDialogOpen.value = true
                            }
                            AppToolbarSettings.DAY_NIGHT_MODE -> {
                                dayNightSettingsDialogOpen.value = true
                            }
                            AppToolbarSettings.LOTTERY_LOG -> {
                                onLotteryDataClick.invoke()
                            }
                        }
                        expanded = false
                    },
                    enabled = true,
                    text = {
                        AppToolbarSettingsDropDownText(
                            text = when (itemValue) {
                                AppToolbarSettings.FONT_SIZE -> stringResource(id = R.string.font_size)
                                AppToolbarSettings.UPDATE_LTO -> stringResource(id = R.string.update_lto)
                                AppToolbarSettings.RESET -> stringResource(id = R.string.reset)
                                AppToolbarSettings.DAY_NIGHT_MODE -> stringResource(id = R.string.day_night_settings)
                                AppToolbarSettings.LOTTERY_LOG -> stringResource(id = R.string.lottery_log)
                            }
                        )
                    },
                )
            }
        }
    }

    FontSettingsDialog(fontSizeDialogOpen)
    ResetDatabase(dialogOpen = resetDatabaseDialogOpen)
    DayNightSettingsDialog(dayNightSettingsDialogOpen)
}

@Composable
private fun LotteryType.toUiString() = when (this) {
    LotteryType.Lto -> stringResource(id = R.string.lto)
    LotteryType.LtoBig -> stringResource(id = R.string.lto_big)
    LotteryType.LtoHK -> stringResource(id = R.string.lto_hk)
}

enum class AppToolbarSettings {
    FONT_SIZE, UPDATE_LTO, RESET, DAY_NIGHT_MODE, LOTTERY_LOG
}