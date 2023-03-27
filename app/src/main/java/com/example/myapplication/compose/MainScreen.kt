package com.example.myapplication.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.myapplication.R
import com.example.myapplication.compose.ViewModelStateMapper.mapToUiState
import com.example.myapplication.compose.general.LoadingView
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
            Box {
                Table(value.rowList)

                if (value.isLoading) {
                    LoadingView(
                        color = Color.Cyan,
                        modifier = Modifier.align(Alignment.Center),
                        text = stringResource(id = R.string.loading)
                    )
                }
            }
        }
    }
}