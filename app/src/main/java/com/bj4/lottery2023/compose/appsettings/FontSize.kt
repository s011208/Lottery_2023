package com.bj4.lottery2023.compose.appsettings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.bj4.lottery2023.compose.general.DialogText
import com.bj4.lottery2023.compose.lotterytable.vm.LotteryTableEvents
import com.bj4.lottery2023.compose.lotterytable.vm.LotteryTableViewModel
import com.example.service.cache.FontSize
import org.koin.java.KoinJavaComponent

@Composable
fun FontSettingsDialog(dialogOpen: MutableState<Boolean>) {
    val viewModel: LotteryTableViewModel by KoinJavaComponent.inject(LotteryTableViewModel::class.java)

    val type = viewModel.viewModelState.collectAsState().value.fontType

    if (dialogOpen.value) {
        Dialog(onDismissRequest = {
            dialogOpen.value = false
        }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(size = 10.dp)
            ) {
                LazyColumn(modifier = Modifier.padding(all = 16.dp)) {
                    item {
                        FontSize.values().forEach {
                            DialogText(
                                text = when (it) {
                                    FontSize.EXTRA_SMALL -> stringResource(id = com.bj4.lottery2023.R.string.x_small)
                                    FontSize.SMALL -> stringResource(id = com.bj4.lottery2023.R.string.small)
                                    FontSize.NORMAL -> stringResource(id = com.bj4.lottery2023.R.string.normal)
                                    FontSize.LARGE -> stringResource(id = com.bj4.lottery2023.R.string.large)
                                    FontSize.EXTRA_LARGE -> stringResource(id = com.bj4.lottery2023.R.string.x_large)
                                },
                                modifier = Modifier.clickable {
                                    viewModel.handleEvent(LotteryTableEvents.ChangeFontSize(it))
                                    dialogOpen.value = false
                                },
                                selected = type == it
                            )
                        }
                    }
                }
            }
        }
    }
}