package com.bj4.lottery2023.compose.lotterylog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bj4.lottery2023.compose.lotterylog.vm.LotteryLogUiEvent
import com.bj4.lottery2023.compose.lotterylog.vm.LotteryLogViewModel
import com.example.analytics.Analytics
import org.koin.java.KoinJavaComponent
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LotteryLog() {
    val viewModel: LotteryLogViewModel by KoinJavaComponent.inject(LotteryLogViewModel::class.java)
    val state = viewModel.viewModelState.collectAsState()

    val dateFormat = SimpleDateFormat("yyyy/MM/dd hh:mm:ss", Locale.TAIWAN)

    val itemList = state.value.taskGroupLList

    Timber.i("log size: ${itemList.size}")

    LaunchedEffect(key1 = Unit) {
        viewModel.handleUiEvent(LotteryLogUiEvent.RequestData)
    }

    LaunchedEffect(key1 = Unit) {
        val analytics: Analytics by KoinJavaComponent.inject(Analytics::class.java)
        analytics.trackScreen("LotteryLog")
    }

    if (itemList.isEmpty()) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(16.dp)) {
            Text(text = stringResource(id = com.bj4.lottery2023.R.string.no_lottery_log))
        }
    } else {
        Column {
            Button(
                onClick = { viewModel.handleUiEvent(LotteryLogUiEvent.ClearCache) },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = stringResource(id = com.bj4.lottery2023.R.string.clear_data))
            }

            LazyColumn(
                modifier = Modifier.padding(16.dp),
                content = {
                    item {
                        state.value.taskGroupLList.forEach { groupList ->
                            Row {
                                Text(
                                    text = groupList.timeStamp,
                                    modifier = Modifier
                                        .width(600.dp)
                                        .padding(top = 16.dp))

                                Text(text = groupList.source)
                            }
                            Divider(modifier = Modifier.width(600.dp))
                            groupList.itemList.forEach { item ->
                                Row {
                                    Text(
                                        text = dateFormat.format(
                                            Date(item.timeStamp)
                                        ),
                                        modifier = Modifier.width(300.dp)
                                    )

                                    Text(
                                        text = item.type.toString(),
                                        modifier = Modifier.width(150.dp)
                                    )

                                    Text(
                                        text = item.result.toString(),
                                        modifier = Modifier.width(150.dp)
                                    )

                                    Text(text = item.message, modifier = Modifier.fillMaxWidth())
                                }
                            }
                        }
                    }
                })
        }
    }
}