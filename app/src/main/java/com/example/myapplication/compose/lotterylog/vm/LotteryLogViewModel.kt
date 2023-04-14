package com.example.myapplication.compose.lotterylog.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.LoadingState
import com.example.data.LotteryType
import com.example.service.usecase.LotteryLogUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LotteryLogViewModel(private val lotteryLogUseCase: LotteryLogUseCase) : ViewModel() {
    data class DisplayItem(
        val type: LotteryType,
        val timeStamp: Long,
        val result: LoadingState,
        val message: String
    )

    data class ViewModelState(val itemList: List<DisplayItem> = listOf())

    private val _viewModelState: MutableStateFlow<ViewModelState> = MutableStateFlow(
        ViewModelState()
    )
    val viewModelState: StateFlow<ViewModelState> = _viewModelState.asStateFlow()

    fun handleUiEvent(event: LotteryLogUiEvent) {
        when (event) {
            LotteryLogUiEvent.RequestData -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _viewModelState.emit(
                        ViewModelState(
                            lotteryLogUseCase.getAll()
                                .map {
                                    DisplayItem(
                                        it.type,
                                        it.timeStamp,
                                        it.state,
                                        it.errorMessage
                                    )
                                })
                    )
                }
            }
        }
    }
}

sealed class LotteryLogUiEvent {
    object RequestData : LotteryLogUiEvent()
}