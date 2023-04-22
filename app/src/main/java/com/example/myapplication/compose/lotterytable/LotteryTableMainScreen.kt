package com.example.myapplication.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.myapplication.R
import com.example.myapplication.compose.ViewModelStateMapper.mapToUiState
import com.example.myapplication.compose.general.LoadingView
import com.example.myapplication.compose.lotterytable.vm.LotteryTableViewModel
import org.koin.java.KoinJavaComponent.inject

@Composable
fun LotteryTableMainScreen() {
    val viewModel: LotteryTableViewModel by inject(LotteryTableViewModel::class.java)
    val state = viewModel.viewModelState.collectAsState()

    when (val value = state.value.mapToUiState()) {
        is UiState.Show -> {
            Box {
                if (value.rowList.isNotEmpty()) {
                    LotteryTable(
                        value.rowList,
                        value.tableType,
                        value.extraSpacing,
                        value.showDivideLine,
                    )
                } else if (!value.isLoading && !value.isSyncing) {
                    EmptyScreen()
                }
                if (value.isLoading) {
                    LoadingView(
                        color = Color.Cyan,
                        text = stringResource(id = R.string.loading)
                    )
                } else if (value.isSyncing) {
                    LoadingView(
                        color = Color.Cyan,
                        text = stringResource(id = R.string.syncing)
                    )
                }
            }
        }
        else -> {}
    }
}