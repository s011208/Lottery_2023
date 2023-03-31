package com.example.myapplication.vm

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.analytics.Analytics
import com.example.data.LotteryType
import com.example.myapplication.R
import com.example.service.cache.DisplayOrder
import com.example.service.cache.FontSize
import com.example.service.cache.SortType
import com.example.service.usecase.DisplayUseCase
import com.example.service.usecase.SettingsUseCase
import com.example.service.usecase.SyncUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.java.KoinJavaComponent
import timber.log.Timber

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
                dayNightSettings = settingsUseCase.getDayNight(),
            )
        )
        viewModelState = _viewModelState.asStateFlow()
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        viewModelScope.launch {
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
        viewModelScope.launch {
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
                is MyEvents.ChangeDayNightSettings -> {
                    changeDayNightSettings(event)
                }
                else -> {
                    // ignore
                }
            }
        }
    }

    private suspend fun changeDayNightSettings(event: MyEvents.ChangeDayNightSettings) {
        _eventState.emit(event)
        _viewModelState.emit(_viewModelState.value.copy(dayNightSettings = event.dayNightSettings))
        settingsUseCase.setDayNightSettings(event.dayNightSettings)
    }

    private suspend fun startSync(source: Source) {
        syncData(source)
        reloadLotteryUiData()
    }

    private suspend fun updateData() {
        syncData(Source.UI)
        reloadLotteryUiData()
    }

    private suspend fun changeSortType(event: MyEvents.ChangeSortType) {
        _viewModelState.emit(
            _viewModelState.value.copy(
                isLoading = true
            )
        )
        val lotteryType = _viewModelState.value.lotteryType
        val sortType = event.type
        val displayOrder = _viewModelState.value.displayOrder
        val rowList: List<Row>
        withContext(Dispatchers.IO) {
            rowList = getLotteryDisplayRow(lotteryType, sortType, displayOrder)
        }

        _viewModelState.emit(
            _viewModelState.value.copy(
                isLoading = false,
                sortType = event.type,
                rowList = rowList
            )
        )
        settingsUseCase.setSortType(event.type)
    }

    private suspend fun changeLotteryType(event: MyEvents.ChangeLotteryType) {
        _viewModelState.emit(
            _viewModelState.value.copy(
                isLoading = true
            )
        )
        val lotteryType = event.type
        val sortType = settingsUseCase.getSortType()
        val displayOrder = _viewModelState.value.displayOrder
        val rowList: List<Row>
        withContext(Dispatchers.IO) {
            rowList = getLotteryDisplayRow(lotteryType, sortType, displayOrder)
        }
        _viewModelState.emit(
            _viewModelState.value.copy(
                isLoading = false,
                lotteryType = event.type,
                rowList = rowList
            )
        )
        settingsUseCase.setLotteryType(event.type)
    }

    private suspend fun scrollToBottom() {
        _eventState.emit(MyEvents.ScrollToBottom)
    }

    private suspend fun scrollToTop() {
        _eventState.emit(MyEvents.ScrollToTop)
    }

    private suspend fun changeFontSize(event: MyEvents.ChangeFontSize) {
        val fontSize = event.fontSize.toDisplaySize()
        _viewModelState.emit(
            _viewModelState.value.copy(fontSize = fontSize, fontType = event.fontSize)
        )
        _eventState.emit(MyEvents.FontSizeChanged(fontSize))
        settingsUseCase.setFontSize(event.fontSize)
    }

    private suspend fun changeDisplayOrder(event: MyEvents.ChangeDisplayOrder) {
        _viewModelState.emit(
            _viewModelState.value.copy(
                isLoading = true
            )
        )
        val lotteryType = _viewModelState.value.lotteryType
        val sortType = _viewModelState.value.sortType
        val displayOrder = event.order
        val rowList: List<Row>
        withContext(Dispatchers.IO) {
            rowList = getLotteryDisplayRow(lotteryType, sortType, displayOrder)
        }

        _viewModelState.emit(
            _viewModelState.value.copy(
                isLoading = false,
                displayOrder = event.order,
                rowList = rowList
            )
        )
        settingsUseCase.setDisplayOrder(event.order)
    }

    private suspend fun resetData() {
        val lotteryType = _viewModelState.value.lotteryType
        val sortType = _viewModelState.value.sortType
        val displayOrder = _viewModelState.value.displayOrder
        val rowList: List<Row>
        withContext(Dispatchers.IO) {
            syncUseCase.clearDatabase()
            rowList = getLotteryDisplayRow(lotteryType, sortType, displayOrder)
        }
        _viewModelState.emit(
            _viewModelState.value.copy(
                isLoading = false,
                lotteryType = lotteryType,
                sortType = sortType,
                rowList = rowList
            )
        )
        syncData(Source.UI)
        reloadLotteryUiData()
    }

    private suspend fun syncData(source: Source) {
        if (_viewModelState.value.isSyncing) {
            Timber.w("cancel sync because someone is syncing")
            return
        }
        _eventState.emit(MyEvents.SyncingProgress)
        _viewModelState.emit(_viewModelState.value.copy(isSyncing = true))
        withContext(Dispatchers.IO) {
            awaitAll(
                async {
                    syncUseCase.parseLto().also {
                        if (it.isFailure) {
                            _eventState.emit(
                                MyEvents.SyncFailed(
                                    it.exceptionOrNull(),
                                    LotteryType.Lto,
                                    R.string.failed_to_load_lto,
                                )
                            )
                            it.exceptionOrNull()?.let { throwable ->
                                analytics.recordException(throwable)
                            }
                        }
                    }
                },
                async {
                    syncUseCase.parseLtoBig().also { it ->
                        if (it.isFailure) {
                            _eventState.emit(
                                MyEvents.SyncFailed(
                                    it.exceptionOrNull(),
                                    LotteryType.LtoBig,
                                    R.string.failed_to_load_lto_big,
                                )
                            )
                            it.exceptionOrNull()?.let { throwable ->
                                analytics.recordException(throwable)
                            }
                        }
                    }
                },
                async {
                    syncUseCase.parseLtoHk().also {
                        if (it.isFailure) {
                            _eventState.emit(
                                MyEvents.SyncFailed(
                                    it.exceptionOrNull(),
                                    LotteryType.LtoHK,
                                    R.string.failed_to_load_lto_hk,
                                )
                            )
                            it.exceptionOrNull()?.let { throwable ->
                                analytics.recordException(throwable)
                            }
                        }
                    }
                },
            )
        }
        _viewModelState.emit(_viewModelState.value.copy(isSyncing = false))
        _eventState.emit(MyEvents.EndSync)
        viewModelScope.launch(Dispatchers.IO) {
            analytics.trackSyncSource(source.name)
        }
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

        val rowList: List<Row>
        withContext(Dispatchers.IO) {
            rowList = getLotteryDisplayRow(lotteryType, sortType, displayOrder)
        }
        _viewModelState.emit(
            _viewModelState.value.copy(
                isLoading = false,
                lotteryType = _viewModelState.value.lotteryType,
                sortType = sortType,
                rowList = rowList
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