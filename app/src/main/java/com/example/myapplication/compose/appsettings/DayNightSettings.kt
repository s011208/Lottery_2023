package com.example.myapplication.compose.appsettings

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
import com.example.myapplication.R
import com.example.myapplication.compose.general.DialogText
import com.example.myapplication.compose.lotterytable.vm.LotteryTableEvents
import com.example.myapplication.compose.lotterytable.vm.LotteryTableViewModel
import com.example.service.cache.DayNightMode
import org.koin.java.KoinJavaComponent

@Composable
fun DayNightSettingsDialog(dialogOpen: MutableState<Boolean>) {
    val viewModel: LotteryTableViewModel by KoinJavaComponent.inject(LotteryTableViewModel::class.java)

    val type = viewModel.viewModelState.collectAsState().value.dayNightSettings

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
                        DayNightMode.values().forEach {
                            DialogText(
                                text = when (it) {
                                    DayNightMode.DAY -> stringResource(id = R.string.mode_day)
                                    DayNightMode.NIGHT -> stringResource(id = R.string.mode_night)
                                    DayNightMode.AUTO -> stringResource(id = R.string.mode_system)
                                },
                                modifier = Modifier.clickable {
                                    viewModel.handleEvent(LotteryTableEvents.ChangeDayNightSettings(it))
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