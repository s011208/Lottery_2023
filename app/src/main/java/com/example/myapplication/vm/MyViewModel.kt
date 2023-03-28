package com.example.myapplication.vm

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.analytics.Analytics
import com.example.data.LotteryType
import com.example.service.cache.DisplayOrder
import com.example.service.cache.FontSize
import com.example.service.cache.SortType
import com.example.service.usecase.DisplayUseCase
import com.example.service.usecase.SettingsUseCase
import com.example.service.usecase.SyncUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.java.KoinJavaComponent

class MyViewModel(
    private val syncUseCase: SyncUseCase,
    private val displayUseCase: DisplayUseCase,
    private val settingsUseCase: SettingsUseCase,
) : ViewModel(), DefaultLifecycleObserver {

    private val _viewModelState: MutableStateFlow<ViewModelState>
    val viewModelState: StateFlow<ViewModelState>

    private val _eventState = MutableSharedFlow<MyEvents>()
    val eventStateSharedFlow = _eventState.asSharedFlow()

    private val analytics: Analytics by KoinJavaComponent.inject(Analytics::class.java)

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
            reloadLotteryUiData()
            startSync(Source.APP_START)
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
            is MyEvents.StartSync -> {
                startSync(event.source)
            }
            MyEvents.UpdateData -> {
                updateData()
            }
            is MyEvents.ChangeSortType -> {
                changeSortType(event)
            }
            is MyEvents.ChangeLotteryType -> {
                changeLotteryType(event)
            }
            is MyEvents.ScrollToBottom -> {
                scrollToBottom()
            }
            is MyEvents.ScrollToTop -> {
                scrollToTop()
            }
            is MyEvents.ChangeFontSize -> {
                changeFontSize(event)
            }
            is MyEvents.ChangeDisplayOrder -> {
                changeDisplayOrder(event)
            }
            MyEvents.ResetData -> {
                resetData()
            }
            else -> {
                // ignore
            }
        }
    }

    private fun startSync(source: Source) {
        viewModelScope.launch(Dispatchers.IO) {
            syncData(source)
            reloadLotteryUiData()
        }
    }

    private fun updateData() {
        viewModelScope.launch(Dispatchers.IO) {
            syncData(Source.UI)
            reloadLotteryUiData()
        }
    }

    private fun changeSortType(event: MyEvents.ChangeSortType) {
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

    private fun changeLotteryType(event: MyEvents.ChangeLotteryType) {
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

    private fun scrollToBottom() {
        viewModelScope.launch {
            _eventState.emit(MyEvents.ScrollToBottom)
        }
    }

    private fun scrollToTop() {
        viewModelScope.launch {
            _eventState.emit(MyEvents.ScrollToTop)
        }
    }

    private fun changeFontSize(event: MyEvents.ChangeFontSize) {
        viewModelScope.launch {
            val fontSize = event.fontSize.toDisplaySize()
            _viewModelState.emit(
                _viewModelState.value.copy(fontSize = fontSize, fontType = event.fontSize)
            )
            _eventState.emit(MyEvents.FontSizeChanged(fontSize))
            settingsUseCase.setFontSize(event.fontSize)
        }
    }

    private fun changeDisplayOrder(event: MyEvents.ChangeDisplayOrder) {
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

    private fun resetData() {
        viewModelScope.launch(Dispatchers.IO) {
            syncUseCase.clearDatabase()
            val lotteryType = _viewModelState.value.lotteryType
            val sortType = _viewModelState.value.sortType
            val displayOrder = _viewModelState.value.displayOrder
            _viewModelState.emit(
                _viewModelState.value.copy(
                    isLoading = false,
                    lotteryType = lotteryType,
                    sortType = sortType,
                    rowList = getLotteryDisplayRow(lotteryType, sortType, displayOrder)
                )
            )
            syncData(Source.UI)
            reloadLotteryUiData()
        }
    }

    private suspend fun CoroutineScope.syncData(source: Source) {
        _eventState.emit(MyEvents.SyncingProgress)
        awaitAll(
            async { syncUseCase.parseLto() },
            async { syncUseCase.parseLtoBig() },
            async { syncUseCase.parseLtoHk() },
        )
        _eventState.emit(MyEvents.EndSync())
        analytics.trackSyncSource(source.name)
    }

    private suspend fun reloadLotteryUiData() {
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
                rowList = getLotteryDisplayRow(lotteryType, sortType, displayOrder).also {
                    android.util.Log.e("QQQQ", "reloadLotteryUiData size: ${it.size}")
                }
            )
        )
    }
}

private fun FontSize.toDisplaySize(): Int = when (this) {
    FontSize.EXTRA_SMALL -> 12
    FontSize.SMALL -> 14
    FontSize.NORMAL -> 16
    FontSize.LARGE -> 18
    FontSize.EXTRA_LARGE -> 20
}

enum class Source {
    ONE_TIME, PERIODIC, UNKNOWN, UI, APP_START
}