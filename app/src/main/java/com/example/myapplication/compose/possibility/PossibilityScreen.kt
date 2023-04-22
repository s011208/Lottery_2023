package com.example.myapplication.compose.possibility

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.LotteryType
import com.example.myapplication.R
import com.example.myapplication.compose.RowFactory
import com.example.myapplication.compose.possibility.vm.PossibilityItem
import com.example.myapplication.compose.possibility.vm.PossibilityScreenViewModel
import com.example.myapplication.compose.possibility.vm.PossibilityUiEvent
import org.koin.java.KoinJavaComponent

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PossibilityScreen() {
    val viewModel: PossibilityScreenViewModel by KoinJavaComponent.inject(PossibilityScreenViewModel::class.java)

    var dialogOpen by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.handle(PossibilityUiEvent.Reload)
    }

    Scaffold(floatingActionButton = { FloatingButton { dialogOpen = true } }, content = {
        Surface(modifier = Modifier.padding(16.dp)) {
            Column {
                Text(
                    text = stringResource(
                        id = R.string.choose_number_title,
                        viewModel.viewModelState.collectAsState().value.count
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.secondary
                )
                PossibilityDataScreen()
                Dialogs(dialogOpen) { dialogOpen = false }
            }
        }
    })
}

@Composable
fun Dialogs(dialogOpen: Boolean, onDialogClose: () -> Unit) {
    val viewModel: PossibilityScreenViewModel by KoinJavaComponent.inject(PossibilityScreenViewModel::class.java)

    if (dialogOpen) {
        Dialog(onDismissRequest = {
            onDialogClose()
        }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(size = 10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(id = R.string.choose_number_dialog_title),
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.secondary
                    )

                    LazyColumn(
                        content = {
                            for (count in 5..200 step 5) {
                                item {
                                    Text(
                                        text = count.toString(),
                                        modifier = Modifier
                                            .fillParentMaxWidth()
                                            .align(Alignment.CenterHorizontally)
                                            .padding(8.dp)
                                            .clickable {
                                                viewModel.handle(
                                                    PossibilityUiEvent.ChangeNumberOfRows(
                                                        count
                                                    )
                                                )
                                                onDialogClose()
                                            },
                                        fontSize = 24.sp
                                    )
                                }
                            }
                        }, modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun FloatingButton(onClick: () -> Unit) {
    val viewModel: PossibilityScreenViewModel by KoinJavaComponent.inject(PossibilityScreenViewModel::class.java)
    FloatingActionButton(onClick = {
        onClick()
    }) {
        Icon(
            Icons.Filled.DateRange, stringResource(id = R.string.choose_number)
        )
    }
}

@Composable
fun PossibilityDataScreen() {
    val viewModel: PossibilityScreenViewModel by KoinJavaComponent.inject(PossibilityScreenViewModel::class.java)
    val state = viewModel.viewModelState.collectAsState().value
    val horizontalScrollState = rememberScrollState(0)
    LazyColumn(content = {
        state.itemList.forEach { possibilityItem ->
            item {
                PossibilityColumn(
                    possibilityItem,
                    state.fontSize,
                    state.normalExtraSpacing,
                    state.listExtraSpacing,
                )
            }
        }
    }, modifier = Modifier.horizontalScroll(horizontalScrollState))
}

@Composable
fun PossibilityColumn(
    possibilityItem: PossibilityItem,
    fontSize: Int,
    normalExtraSpacing: Int,
    listExtraSpacing: Int,
) {
    Column(
        modifier = Modifier
            .padding(top = 32.dp)
            .border(1.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(0.dp))
            .padding(10.dp)
    ) {
        Text(
            text = when (possibilityItem.lotteryType) {
                LotteryType.Lto -> stringResource(id = R.string.lto)
                LotteryType.LtoBig -> stringResource(id = R.string.lto_big)
                LotteryType.LtoHK -> stringResource(id = R.string.lto_hk)
                LotteryType.LtoList3 -> stringResource(id = R.string.lto_list3)
                LotteryType.LtoList4 -> stringResource(id = R.string.lto_list4)
            },
            color = MaterialTheme.colorScheme.secondary,
        )

        possibilityItem.rowList.forEach {
            RowFactory(
                row = it, fontSize = fontSize, extraSpacing = when (possibilityItem.lotteryType) {
                    LotteryType.LtoList3, LotteryType.LtoList4 -> {
                        listExtraSpacing
                    }
                    else -> {
                        normalExtraSpacing
                    }
                }
            )
        }
    }
}