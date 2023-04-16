package com.example.myapplication.compose.lotterylog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.compose.lotterylog.vm.LotteryLogViewModel
import org.koin.java.KoinJavaComponent
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun LotteryLog() {
    val viewModel: LotteryLogViewModel by KoinJavaComponent.inject(LotteryLogViewModel::class.java)
    val state = viewModel.viewModelState.collectAsState()

    val dateFormat = SimpleDateFormat("yyyy/MM/dd hh:mm:ss", Locale.getDefault())

    val itemList = state.value.itemList

    Timber.i("log size: ${itemList.size}")

    if (itemList.isEmpty()) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = stringResource(id = R.string.no_lottery_log))
        }
    } else {
        LazyColumn(content = {
            item {
                state.value.itemList.forEach { item ->
                    Row {
                        Text(
                            text = dateFormat.format(
                                Date(item.timeStamp)
                            ),
                            modifier = Modifier.width(300.dp)
                        )

                        Text(text = item.type.toString(), modifier = Modifier.width(150.dp))

                        Text(text = item.result.toString(), modifier = Modifier.width(150.dp))

                        Text(text = item.message, modifier = Modifier.fillMaxWidth())
                    }


                }
            }
        })
    }
}