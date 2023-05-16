package com.bj4.lottery2023.compose.lotterytable

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
import com.bj4.lottery2023.MainActivity
import com.bj4.lottery2023.R
import com.bj4.lottery2023.compose.general.AppToolbarSettingsDropDownText
import com.bj4.lottery2023.compose.general.AppToolbarSettingsText
import com.bj4.lottery2023.compose.general.toUiString
import com.bj4.lottery2023.compose.lotterytable.ViewModelStateMapper.mapToUiState
import com.bj4.lottery2023.compose.lotterytable.vm.LotteryTableEvents
import com.bj4.lottery2023.compose.lotterytable.vm.LotteryTableViewModel
import com.example.data.LotteryType
import com.example.service.cache.DisplayOrder
import com.example.service.cache.SortType
import org.koin.java.KoinJavaComponent

private const val PADDING = 4

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LotteryTableToolbar(navController: NavController = rememberNavController()) {
    val viewModel: LotteryTableViewModel by KoinJavaComponent.inject(LotteryTableViewModel::class.java)
    val state = viewModel.viewModelState.collectAsState()
    val value = state.value.mapToUiState()

    TopAppBar(title = {
        Text(
            text = when (value) {
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
                if (!value.isSyncing) {
                    StartPlusMinusScreen { navController.navigate(MainActivity.SCREEN_NAME_PLUS_MINUS) }
                    StartPossibilityScreen { navController.navigate(MainActivity.SCREEN_NAME_POSSIBILITY) }
                }
                StartSettingsScreen { navController.navigate(MainActivity.SCREEN_NAME_PREFERENCE) }
            }
        }
    }, modifier = Modifier.padding(vertical = 16.dp))
}

@Composable
fun StartPossibilityScreen(event: () -> Unit) {
    AppToolbarSettingsText(text = stringResource(id = R.string.possibility),
        Modifier.clickable { event() })
}

@Composable
fun StartPlusMinusScreen(event: () -> Unit) {
    AppToolbarSettingsText(text = stringResource(id = R.string.plus_minus),
        Modifier.clickable { event() })
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
            LotteryType.values().forEachIndexed { _, itemValue ->
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
            SortType.values().forEachIndexed { _, itemValue ->
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
            DisplayOrder.values().forEachIndexed { _, itemValue ->
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
private fun StartSettingsScreen(
    onSettingsClick: () -> Unit = {},
) {
    Box(modifier = Modifier.padding(PADDING.dp)) {
        AppToolbarSettingsText(
            stringResource(id = R.string.settings),
            Modifier.clickable {
                onSettingsClick()
            })
    }
}