package com.example.myapplication.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.service.usecase.SyncUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MyViewModel(private val syncUseCase: SyncUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Empty)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _eventState = MutableSharedFlow<MyEvents>()
    val eventStateSharedFlow = _eventState.asSharedFlow()

    fun handleEvent(event: MyEvents) {
        when (event) {
            MyEvents.StartSync -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _eventState.emit(MyEvents.SyncingProgress(LotteryType.Lto))
                    syncUseCase.parseLto()
                    _eventState.emit(MyEvents.SyncingProgress(LotteryType.LtoBig))
                    syncUseCase.parseLtoBig()
                    _eventState.emit(MyEvents.SyncingProgress(LotteryType.LtoHk))
                    syncUseCase.parseLtoHk()
                    _eventState.emit(MyEvents.EndSync())
                }
            }
            else -> {
                // ignore
            }
        }
    }

    enum class LotteryType {
        Lto, LtoBig, LtoHk
    }
}