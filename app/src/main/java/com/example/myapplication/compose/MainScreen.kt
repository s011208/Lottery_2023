package com.example.myapplication.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.vm.MyViewModel
import com.example.myapplication.vm.UiState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

@Composable
fun MainScreen() {
    val viewModel: MyViewModel by inject(MyViewModel::class.java)
    val state = viewModel.uiState.collectAsState()

    when (state.value) {
        is UiState.Empty -> {
            Box {
                Button(onClick = { /*TODO*/ }) {
                    
                }
            }
        }
        is UiState.Show -> {

        }
    }

}