package com.bj4.lottery2023.compose.lotterytable

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.bj4.lottery2023.ImmutableListWrapper
import com.bj4.lottery2023.compose.general.LoadingView
import com.bj4.lottery2023.compose.lotterytable.ViewModelStateMapper.mapToUiState
import com.bj4.lottery2023.compose.lotterytable.vm.LotteryTableViewModel
import com.example.analytics.Analytics
import org.koin.java.KoinJavaComponent.inject

@Composable
fun LotteryTableMainScreen() {
    val viewModel: LotteryTableViewModel by inject(LotteryTableViewModel::class.java)
    val state = viewModel.viewModelState.collectAsState()

    LaunchedEffect(key1 = Unit) {
        val analytics: Analytics by inject(Analytics::class.java)
        analytics.trackScreen("LotteryTableMainScreen")
    }

    when (val value = state.value.mapToUiState()) {
        is UiState.Show -> {
            Box {
                if (value.rowList.isNotEmpty()) {
                    LotteryTable(
                        ImmutableListWrapper(value.rowList),
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
                        text = stringResource(id = com.bj4.lottery2023.R.string.loading)
                    )
                } else if (value.isSyncing) {
                    LoadingView(
                        color = Color.Cyan,
                        text = stringResource(id = com.bj4.lottery2023.R.string.syncing)
                    )
                }
            }
        }
    }
}