package com.example.myapplication.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.example.myapplication.compose.ViewModelStateMapper.mapToUiState
import com.example.myapplication.vm.MyViewModel
import com.example.myapplication.vm.ViewModelState
import org.koin.java.KoinJavaComponent.inject

@Composable
fun MainScreen() {
    val viewModel: MyViewModel by inject(MyViewModel::class.java)
    val state = viewModel.viewModelState.collectAsState()

    when (val value = state.value.mapToUiState()) {
        is UiState.Empty -> {
            Box {
                Button(onClick = { throw RuntimeException("Test Crash") }) {
                    Text(text = "Click me")
                }
            }
        }
        is UiState.Show -> {
            Table(value.rowList)
        }
        is UiState.Loading -> {
            Box {
                Text(text = value.hint)
            }
        }
    }
}