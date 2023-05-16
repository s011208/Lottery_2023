package com.bj4.lottery2023.compose.possibility

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
import com.bj4.lottery2023.compose.general.toUiString
import com.bj4.lottery2023.compose.possibility.vm.PossibilityScreenViewModel
import com.bj4.lottery2023.compose.possibility.vm.PossibilityUiEvent
import com.example.data.LotteryType
import org.koin.java.KoinJavaComponent

private const val PADDING = 4

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PossibilityToolbar(navController: NavController = rememberNavController()) {
    val viewModel: PossibilityScreenViewModel by KoinJavaComponent.inject(PossibilityScreenViewModel::class.java)
    val currentLotteryType = viewModel.viewModelState.collectAsState().value.lotteryType

    TopAppBar(title = {
        Text(
            text = "${stringResource(id = R.string.possibility)} - ${currentLotteryType.toUiString()}"
        )
    }, navigationIcon = {
        IconButton(onClick = { navController.navigateUp() }) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = stringResource(id = R.string.back)
            )
        }
    }, actions = {
        LotteryTypeDropdownMenu()
        StartSettingsScreen {
            navController.navigate(
                MainActivity.SCREEN_NAME_PREFERENCE
            )
        }
    }, modifier = Modifier.padding(vertical = 16.dp))
}

@Composable
private fun LotteryTypeDropdownMenu() {
    var expanded by remember {
        mutableStateOf(false)
    }

    val viewModel: PossibilityScreenViewModel by KoinJavaComponent.inject(PossibilityScreenViewModel::class.java)
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
                        viewModel.handle(PossibilityUiEvent.ChangeLotteryType(itemValue))
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
