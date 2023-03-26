package com.example.myapplication.vm

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.LotteryType
import com.example.myapplication.SortType
import com.example.service.usecase.DisplayUseCase
import com.example.service.usecase.SyncUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MyViewModel(
    private val syncUseCase: SyncUseCase,
    private val displayUseCase: DisplayUseCase,
) : ViewModel(), DefaultLifecycleObserver {

    private val _viewModelState = MutableStateFlow(ViewModelState())
    val viewModelState: StateFlow<ViewModelState> = _viewModelState.asStateFlow()

    private val _eventState = MutableSharedFlow<MyEvents>()
    val eventStateSharedFlow = _eventState.asSharedFlow()

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        viewModelScope.launch(Dispatchers.IO) {
            _viewModelState.emit(
                _viewModelState.value.copy(
                    loadingHint = "Loading ${_viewModelState.value.lotteryType}",
                    isLoading = true
                )
            )
            val data =
                displayUseCase.getLotteryData(_viewModelState.value.lotteryType) ?: return@launch
            val sortType = SortType.NormalOrder
            val result = LotteryDataMapper.map(data, sortType)
            android.util.Log.e("QQQQ", "${_viewModelState.value}")
            _viewModelState.emit(
                _viewModelState.value.copy(
                    isLoading = false,
                    lotteryType = LotteryType.Lto,
                    sortType = sortType,
                    rowList = result
                )
            )
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
            is MyEvents.ChangeSortType -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _viewModelState.emit(
                        _viewModelState.value.copy(
                            loadingHint = "Loading ${_viewModelState.value.lotteryType}",
                            isLoading = true
                        )
                    )
                    val data = displayUseCase.getLotteryData(_viewModelState.value.lotteryType)
                        ?: return@launch
                    val result = LotteryDataMapper.map(data, event.type)

                    _viewModelState.emit(
                        _viewModelState.value.copy(
                            isLoading = false,
                            sortType = event.type,
                            rowList = result
                        )
                    )
                }
            }
            is MyEvents.ChangeLotteryType -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _viewModelState.emit(
                        _viewModelState.value.copy(
                            loadingHint = "Loading ${event.type}",
                            isLoading = true
                        )
                    )
                    val data = displayUseCase.getLotteryData(event.type) ?: return@launch
                    val result = LotteryDataMapper.map(data, _viewModelState.value.sortType)
                    _viewModelState.emit(
                        _viewModelState.value.copy(
                            isLoading = false,
                            lotteryType = event.type,
                            rowList = result
                        )
                    )
                }
            }
            is MyEvents.ScrollToBottom -> {
                viewModelScope.launch {
                    _eventState.emit(MyEvents.ScrollToBottom)
                }
            }
            is MyEvents.ScrollToTop -> {
                viewModelScope.launch {
                    _eventState.emit(MyEvents.ScrollToTop)
                }
            }
            else -> {
                // ignore
            }
        }
    }
}