package com.example.myapplication.vm

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.LotteryType
import com.example.service.cache.DisplayOrder
import com.example.service.cache.FontSize
import com.example.service.cache.SortType
import com.example.service.usecase.DisplayUseCase
import com.example.service.usecase.SettingsUseCase
import com.example.service.usecase.SyncUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MyViewModel(
    private val syncUseCase: SyncUseCase,
    private val displayUseCase: DisplayUseCase,
    private val settingsUseCase: SettingsUseCase,
) : ViewModel(), DefaultLifecycleObserver {

    private val _viewModelState: MutableStateFlow<ViewModelState>
    val viewModelState: StateFlow<ViewModelState>

    private val _eventState = MutableSharedFlow<MyEvents>()
    val eventStateSharedFlow = _eventState.asSharedFlow()

    init {
        _viewModelState = MutableStateFlow(
            ViewModelState(
                lotteryType = settingsUseCase.getLotteryType(),
                sortType = settingsUseCase.getSortType(),
                fontSize = settingsUseCase.getFontSize().toDisplaySize(),
                fontType = settingsUseCase.getFontSize(),
                displayOrder = settingsUseCase.getDisplayOrder(),
            )
        )
        viewModelState = _viewModelState.asStateFlow()
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        viewModelScope.launch(Dispatchers.IO) {
            _viewModelState.emit(
                _viewModelState.value.copy(
                    isLoading = true
                )
            )
            val lotteryType = _viewModelState.value.lotteryType
            val sortType = _viewModelState.value.sortType
            val displayOrder = _viewModelState.value.displayOrder
            _viewModelState.emit(
                _viewModelState.value.copy(
                    isLoading = false,
                    lotteryType = _viewModelState.value.lotteryType,
                    sortType = sortType,
                    rowList = getLotteryDisplayRow(lotteryType, sortType, displayOrder)
                )
            )
        }
    }

    private fun getLotteryDisplayRow(
        lotteryType: LotteryType,
        sortType: SortType,
        displayOrder: DisplayOrder
    ): List<Row> {
        val data = displayUseCase.getLotteryData(lotteryType) ?: return listOf()
        return LotteryDataMapper.map(
            data.copy(
                dataList = if (displayOrder == DisplayOrder.ASCEND) {
                    data.dataList
                } else data.dataList.reversed()
            ), sortType
        )
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
                            isLoading = true
                        )
                    )
                    val lotteryType = _viewModelState.value.lotteryType
                    val sortType = event.type
                    val displayOrder = _viewModelState.value.displayOrder

                    _viewModelState.emit(
                        _viewModelState.value.copy(
                            isLoading = false,
                            sortType = event.type,
                            rowList = getLotteryDisplayRow(lotteryType, sortType, displayOrder)
                        )
                    )
                    settingsUseCase.setSortType(event.type)
                }
            }
            is MyEvents.ChangeLotteryType -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _viewModelState.emit(
                        _viewModelState.value.copy(
                            isLoading = true
                        )
                    )
                    val lotteryType = event.type
                    val sortType = settingsUseCase.getSortType()
                    val displayOrder = _viewModelState.value.displayOrder
                    _viewModelState.emit(
                        _viewModelState.value.copy(
                            isLoading = false,
                            lotteryType = event.type,
                            rowList = getLotteryDisplayRow(lotteryType, sortType, displayOrder)
                        )
                    )
                    settingsUseCase.setLotteryType(event.type)
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
            is MyEvents.ChangeFontSize -> {
                viewModelScope.launch {
                    val fontSize = event.fontSize.toDisplaySize()
                    _viewModelState.emit(
                        _viewModelState.value.copy(fontSize = fontSize, fontType = event.fontSize)
                    )
                    _eventState.emit(MyEvents.FontSizeChanged(fontSize))
                    settingsUseCase.setFontSize(event.fontSize)
                }
            }
            is MyEvents.ChangeDisplayOrder -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _viewModelState.emit(
                        _viewModelState.value.copy(
                            isLoading = true
                        )
                    )
                    val lotteryType = _viewModelState.value.lotteryType
                    val sortType = _viewModelState.value.sortType
                    val displayOrder = event.order

                    _viewModelState.emit(
                        _viewModelState.value.copy(
                            isLoading = false,
                            displayOrder = event.order,
                            rowList = getLotteryDisplayRow(lotteryType, sortType, displayOrder)
                        )
                    )
                    settingsUseCase.setDisplayOrder(event.order)
                }
            }
            else -> {
                // ignore
            }
        }
    }
}

private fun FontSize.toDisplaySize(): Int = when (this) {
    FontSize.EXTRA_SMALL -> 12
    FontSize.SMALL -> 14
    FontSize.NORMAL -> 16
    FontSize.LARGE -> 18
    FontSize.EXTRA_LARGE -> 20
}