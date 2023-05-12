package com.bj4.lottery2023.compose.plusminus

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.bj4.lottery2023.compose.plusminus.vm.PlusMinusEvent
import com.bj4.lottery2023.compose.plusminus.vm.PlusMinusViewModel
import com.example.data.LotteryType
import org.koin.java.KoinJavaComponent

private const val PADDING = 4

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlusMinusToolbar(navController: NavController = rememberNavController()) {
    val viewModel: PlusMinusViewModel by KoinJavaComponent.inject(PlusMinusViewModel::class.java)
    val currentLotteryType = viewModel.viewModelState.collectAsState().value.lotteryType

    TopAppBar(title = {
        Text(
            text = "${stringResource(id = R.string.plus_minus)} - ${currentLotteryType.toUiString()}"
        )
    }, navigationIcon = {
        IconButton(onClick = { navController.navigateUp() }) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = stringResource(id = com.bj4.lottery2023.R.string.back)
            )
        }
    }, actions = {
        ScrollToBottom(viewModel)
        ScrollToTop(viewModel)
        LotteryTypeDropdownMenu()
        StartSettingsScreen {
            navController.navigate(
                MainActivity.SCREEN_NAME_PREFERENCE
            )
        }
    })
}

@Composable
fun ScrollToBottom(viewModel: PlusMinusViewModel) {
    AppToolbarSettingsText(text = stringResource(id = R.string.scroll_to_bottom),
        Modifier.clickable { viewModel.handleEvent(PlusMinusEvent.ScrollToBottom) })
}

@Composable
fun ScrollToTop(viewModel: PlusMinusViewModel) {
    AppToolbarSettingsText(text = stringResource(id = R.string.scroll_to_top),
        Modifier.clickable { viewModel.handleEvent(PlusMinusEvent.ScrollToTop) })
}

@Composable
private fun LotteryTypeDropdownMenu() {
    var expanded by remember {
        mutableStateOf(false)
    }

    val viewModel: PlusMinusViewModel by KoinJavaComponent.inject(PlusMinusViewModel::class.java)
    val currentLotteryType = viewModel.viewModelState.collectAsState().value.lotteryType

    Box(modifier = Modifier.padding(PADDING.dp)) {
        AppToolbarSettingsText(
            stringResource(id = R.string.lottery_type),
            Modifier.clickable { expanded = true })

        DropdownMenu(expanded = expanded, onDismissRequest = {
            expanded = false
        }) {
            LotteryType.values()
                .filterNot { it == LotteryType.LtoList4 || it == LotteryType.LtoList3 || it == LotteryType.Lto }
                .forEachIndexed { _, itemValue ->
                    DropdownMenuItem(onClick = {
                        viewModel.handleEvent(PlusMinusEvent.ChangeLotteryType(itemValue))
                        expanded = false
                    }, enabled = true, text = {
                        AppToolbarSettingsDropDownText(
                            text = itemValue.toUiString(),
                        )
                    }, trailingIcon = if (currentLotteryType == itemValue) {
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

@Composable
private fun LotteryType.toUiString() = when (this) {
    LotteryType.Lto -> stringResource(id = R.string.lto)
    LotteryType.LtoBig -> stringResource(id = R.string.lto_big)
    LotteryType.LtoHK -> stringResource(id = R.string.lto_hk)
    LotteryType.Lto539 -> stringResource(id = R.string.lto_539)
    LotteryType.LtoList3 -> stringResource(id = R.string.lto_list3)
    LotteryType.LtoList4 -> stringResource(id = R.string.lto_list4)
}