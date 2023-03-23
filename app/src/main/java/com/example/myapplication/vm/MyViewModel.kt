package com.example.myapplication.vm

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.LotteryType
import com.example.service.SortType
import com.example.service.usecase.DisplayUseCase
import com.example.service.usecase.SyncUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MyViewModel(
    private val syncUseCase: SyncUseCase,
    private val displayUseCase: DisplayUseCase,
) : ViewModel(), DefaultLifecycleObserver {
    private val _uiState = MutableStateFlow<UiState>(UiState.Empty)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _eventState = MutableSharedFlow<MyEvents>()
    val eventStateSharedFlow = _eventState.asSharedFlow()

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        viewModelScope.launch(Dispatchers.IO) {
            val data = displayUseCase.getLotteryData(LotteryType.Lto) ?: return@launch
            _uiState.emit(UiState.Show(LotteryType.Lto, SortType.Normal, LotteryDataMapper.map(data, SortType.Normal)))
        }
    }

    fun handleEvent(event: MyEvents) {
        when (event) {
            MyEvents.StartSync -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _eventState.emit(MyEvents.SyncingProgress(LotteryType.Lto))
                    syncUseCase.parseLto()
                    _eventState.emit(MyEvents.SyncingProgress(LotteryType.LtoBig))
                    syncUseCase.parseLtoBig()
                    _eventState.emit(MyEvents.SyncingProgress(LotteryType.LtoHK))
                    syncUseCase.parseLtoHk()
                    _eventState.emit(MyEvents.EndSync())
                }
            }
            else -> {
                // ignore
            }
        }
    }
}