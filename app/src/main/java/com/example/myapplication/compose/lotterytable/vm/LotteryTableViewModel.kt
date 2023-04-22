package com.example.myapplication.compose.lotterytable.vm

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.analytics.Analytics
import com.example.data.LotteryType
import com.example.myapplication.R
import com.example.myapplication.compose.appsettings.*
import com.example.service.cache.DayNightMode
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

class LotteryTableViewModel(
    private val syncUseCase: SyncUseCase,
    private val displayUseCase: DisplayUseCase,
    private val settingsUseCase: SettingsUseCase,
    context: Context,
) : ViewModel(), DefaultLifecycleObserver {

    private val _viewModelState: MutableStateFlow<ViewModelState>
    val viewModelState: StateFlow<ViewModelState>

    private val _eventState = MutableSharedFlow<LotteryTableEvents>()
    val eventStateSharedFlow = _eventState.asSharedFlow()

    private val analytics: Analytics by KoinJavaComponent.inject(Analytics::class.java)

    init {
        _viewModelState = MutableStateFlow(
            ViewModelState(
                lotteryType = settingsUseCase.getLotteryType(),
                sortType = settingsUseCase.getSortType(),
                displayOrder = settingsUseCase.getDisplayOrder(),
            )
        )
        viewModelState = _viewModelState.asStateFlow()

        viewModelScope.launch {
            context.settingsDataStore.data
                .distinctUntilChanged()
                .collect { preference ->
                    preference.asMap().forEach { (key, value) ->
                        when (key.name) {
                            SETTINGS_KEY_DAY_NIGHT_MODE -> {
                                viewModelScope.launch {
                                    val current = DayNightMode.valueOf(value as String)
                                    if (_viewModelState.value.dayNightSettings != current) {
                                        changeDayNightSettings(current)
                                    }
                                }
                            }
                            SETTINGS_KEY_FONT_SIZE -> {
                                viewModelScope.launch {
                                    val current = FontSize.valueOf(value as String)
                                    if (_viewModelState.value.fontType != current) {
                                        changeFontSize(current)
                                    }
                                }
                            }
                            SETTINGS_EXTRA_SPACING_NORMAL_TABLE -> {
                                viewModelScope.launch {
                                    val current = (value as Float).toInt()
                                    if (_viewModelState.value.normalTableExtraSpacing != current) {
                                        changeExtraSpacingNormalTable(current)
                                    }
                                }
                            }
                            SETTINGS_EXTRA_SPACING_LIST_TABLE -> {
                                viewModelScope.launch {
                                    val current = (value as Float).toInt()
                                    if (_viewModelState.value.listTableExtraSpacing != current) {
                                        changeExtraSpacingListTable(current)
                                    }
                                }
                            }
                            SETTINGS_SHOW_DIVIDE_LINE -> {
                                viewModelScope.launch {
                                    val current = value as Boolean
                                    if (_viewModelState.value.showDivideLine != current) {
                                        _viewModelState.emit(
                                            _viewModelState.value.copy(
                                                showDivideLine = current
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
        }
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
            ), sortType, lotteryType
        )
    }

    fun handleEvent(event: LotteryTableEvents) {
        viewModelScope.launch {
            when (event) {
                is LotteryTableEvents.StartSync -> {
                    startSync(event.source)
                }
                LotteryTableEvents.UpdateData -> {
                    updateData()
                }
                is LotteryTableEvents.ChangeSortType -> {
                    changeSortType(event)
                }
                is LotteryTableEvents.ChangeLotteryType -> {
                    changeLotteryType(event)
                }
                is LotteryTableEvents.ScrollToBottom -> {
                    scrollToBottom()
                }
                is LotteryTableEvents.ScrollToTop -> {
                    scrollToTop()
                }
                is LotteryTableEvents.ChangeDisplayOrder -> {
                    changeDisplayOrder(event)
                }
                LotteryTableEvents.ResetData -> {
                    resetData()
                }
                else -> {
                    // ignore
                }
            }
        }
    }

    private suspend fun changeDayNightSettings(mode: DayNightMode) {
        _eventState.emit(LotteryTableEvents.ChangeDayNightSettings(mode))
        _viewModelState.emit(_viewModelState.value.copy(dayNightSettings = mode))
    }

    private suspend fun startSync(source: Source) {
        syncData(source)
        reloadLotteryUiData()
    }

    private suspend fun updateData() {
        syncData(Source.UI)
        reloadLotteryUiData()
    }

    private suspend fun changeSortType(event: LotteryTableEvents.ChangeSortType) {
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

    private suspend fun changeLotteryType(event: LotteryTableEvents.ChangeLotteryType) {
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
        _eventState.emit(event)
        settingsUseCase.setLotteryType(event.type)
    }

    private suspend fun scrollToBottom() {
        _eventState.emit(LotteryTableEvents.ScrollToBottom)
    }

    private suspend fun scrollToTop() {
        _eventState.emit(LotteryTableEvents.ScrollToTop)
    }

    private suspend fun changeFontSize(fontSizeRaw: FontSize) {
        val fontSize = fontSizeRaw.toDisplaySize()
        _viewModelState.emit(
            _viewModelState.value.copy(fontSize = fontSize, fontType = fontSizeRaw)
        )
        _eventState.emit(LotteryTableEvents.FontSizeChanged(fontSize))
    }

    private suspend fun changeExtraSpacingNormalTable(extraSpacing: Int) {
        _viewModelState.emit(
            _viewModelState.value.copy(normalTableExtraSpacing = extraSpacing)
        )
    }

    private suspend fun changeExtraSpacingListTable(extraSpacing: Int) {
        _viewModelState.emit(
            _viewModelState.value.copy(listTableExtraSpacing = extraSpacing)
        )
    }

    private suspend fun changeDisplayOrder(event: LotteryTableEvents.ChangeDisplayOrder) {
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
        _eventState.emit(LotteryTableEvents.SyncingProgress)
        _viewModelState.emit(_viewModelState.value.copy(isSyncing = true))
        withContext(Dispatchers.IO) {
            awaitAll(
                async {
                    syncUseCase.parseLto().also {
                        if (it.isFailure) {
                            _eventState.emit(
                                LotteryTableEvents.SyncFailed(
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
                                LotteryTableEvents.SyncFailed(
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
                                LotteryTableEvents.SyncFailed(
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
                async {
                    syncUseCase.parseLtoList3().also {
                        if (it.isFailure) {
                            _eventState.emit(
                                LotteryTableEvents.SyncFailed(
                                    it.exceptionOrNull(),
                                    LotteryType.LtoList3,
                                    R.string.failed_to_load_lto_list3,
                                )
                            )
                            it.exceptionOrNull()?.let { throwable ->
                                analytics.recordException(throwable)
                            }
                        }
                    }
                },
                async {
                    syncUseCase.parseLtoList4().also {
                        if (it.isFailure) {
                            _eventState.emit(
                                LotteryTableEvents.SyncFailed(
                                    it.exceptionOrNull(),
                                    LotteryType.LtoList4,
                                    R.string.failed_to_load_lto_list4,
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
        _eventState.emit(LotteryTableEvents.EndSync)
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

fun FontSize.toDisplaySize(): Int = when (this) {
    FontSize.EXTRA_SMALL -> 12
    FontSize.SMALL -> 14
    FontSize.NORMAL -> 16
    FontSize.LARGE -> 18
    FontSize.EXTRA_LARGE -> 20
}

enum class Source {
    ONE_TIME, PERIODIC, UNKNOWN, UI, APP_START
}