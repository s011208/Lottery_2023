package com.example.myapplication.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.compose.lotterytable.vm.LotteryTableEvents
import com.example.myapplication.compose.lotterytable.vm.LotteryTableViewModel
import org.koin.java.KoinJavaComponent

@Composable
fun EmptyScreen() {
    val viewModel: LotteryTableViewModel by KoinJavaComponent.inject(LotteryTableViewModel::class.java)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = stringResource(id = R.string.empty_msg), modifier = Modifier.padding(16.dp))
            Button(onClick = { viewModel.handleEvent(LotteryTableEvents.UpdateData) }) {
                Text(text = stringResource(id = R.string.click_to_update))
            }
        }
    }
}