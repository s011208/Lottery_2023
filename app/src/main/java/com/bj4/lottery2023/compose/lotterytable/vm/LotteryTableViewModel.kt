package com.bj4.lottery2023.compose.lotterytable.vm

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bj4.lottery2023.R
import com.bj4.lottery2023.compose.general.Row
import com.example.analytics.Analytics
import com.example.data.LotteryType
import com.example.myapplication.compose.appsettings.SETTINGS_EXTRA_SPACING_LIST_TABLE
import com.example.myapplication.compose.appsettings.SETTINGS_EXTRA_SPACING_LTO_539_TABLE
import com.example.myapplication.compose.appsettings.SETTINGS_EXTRA_SPACING_LTO_BIG_TABLE
import com.example.myapplication.compose.appsettings.SETTINGS_EXTRA_SPACING_LTO_HK_TABLE
import com.example.myapplication.compose.appsettings.SETTINGS_EXTRA_SPACING_LTO_TABLE
import com.example.myapplication.compose.appsettings.SETTINGS_KEY_DAY_NIGHT_MODE
import com.example.myapplication.compose.appsettings.SETTINGS_KEY_FONT_SIZE
import com.example.myapplication.compose.appsettings.SETTINGS_SHOW_DIVIDE_LINE
import com.example.myapplication.compose.appsettings.settingsDataStore
import com.example.service.cache.DayNightMode
import com.example.service.cache.DisplayOrder
import com.example.service.cache.FontSize
import com.example.service.cache.SortType
import com.example.service.usecase.DisplayUseCase
import com.example.service.usecase.SettingsUseCase
import com.example.service.usecase.SyncUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent
import timber.log.Timber
import java.util.Calendar

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

    private val settingsDataStoreFlow = context.settingsDataStore.data

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
            settingsDataStoreFlow
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

                            SETTINGS_EXTRA_SPACING_LTO_TABLE -> {
                                viewModelScope.launch {
                                    val current = (value as Float).toInt()
                                    if (_viewModelState.value.normalTableExtraSpacing != current &&
                                        _viewModelState.value.lotteryType == LotteryType.Lto
                                    ) {
                                        changeExtraSpacingNormalTable(current)
                                    }
                                }
                            }

                            SETTINGS_EXTRA_SPACING_LTO_539_TABLE -> {
                                viewModelScope.launch {
                                    val current = (value as Float).toInt()
                                    if (_viewModelState.value.normalTableExtraSpacing != current &&
                                        _viewModelState.value.lotteryType == LotteryType.Lto539
                                    ) {
                                        changeExtraSpacingNormalTable(current)
                                    }
                                }
                            }

                            SETTINGS_EXTRA_SPACING_LTO_BIG_TABLE -> {
                                viewModelScope.launch {
                                    val current = (value as Float).toInt()
                                    if (_viewModelState.value.normalTableExtraSpacing != current &&
                                        _viewModelState.value.lotteryType == LotteryType.LtoBig
                                    ) {
                                        changeExtraSpacingNormalTable(current)
                                    }
                                }
                            }

                            SETTINGS_EXTRA_SPACING_LTO_HK_TABLE -> {
                                viewModelScope.launch {
                                    val current = (value as Float).toInt()
                                    if (_viewModelState.value.normalTableExtraSpacing != current &&
                                        _viewModelState.value.lotteryType == LotteryType.LtoHK
                                    ) {
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
    ): Pair<List<Row>, Int> {
        val data = displayUseCase.getLotteryData(lotteryType) ?: return Pair(listOf(), 0)
        return Pair(
            LotteryDataMapper.map(
                data.copy(
                    dataList = if (displayOrder == DisplayOrder.ASCEND) {
                        data.dataList
                    } else data.dataList.reversed()
                ), sortType, lotteryType
            ), data.dataList.size
        )
    }

    fun handleEvent(event: LotteryTableEvents) {
        viewModelScope.launch {
            Timber.e("handleEvent: $event")
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

                LotteryTableEvents.DropData -> {
                    dropData()
                }

                is LotteryTableEvents.ClearLotteryData -> {
                    viewModelScope.launch {
                        withContext(Dispatchers.IO) {
                            syncUseCase.service.deleteLottery(event.lotteryType)
                        }
                    }
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
        val dataCount: Int
        withContext(Dispatchers.IO) {
            getLotteryDisplayRow(lotteryType, sortType, displayOrder).also {
                rowList = it.first
                dataCount = it.second
            }
        }

        _viewModelState.emit(
            _viewModelState.value.copy(
                isLoading = false,
                sortType = event.type,
                rowList = rowList,
                dataCount = dataCount,
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
        val dataCount: Int
        val extraSpacing: Int
        withContext(Dispatchers.IO) {
            getLotteryDisplayRow(lotteryType, sortType, displayOrder).also {
                rowList = it.first
                dataCount = it.second
            }
            extraSpacing =
                getLotteryExtraSpacing(lotteryType, settingsDataStoreFlow, viewModelScope)
        }

        _viewModelState.emit(
            _viewModelState.value.copy(
                isLoading = false,
                lotteryType = event.type,
                rowList = rowList,
                normalTableExtraSpacing = extraSpacing,
                listTableExtraSpacing = extraSpacing,
                dataCount = dataCount,
            )
        )
        _eventState.emit(event)
        settingsUseCase.setLotteryType(event.type)

        analytics.trackLotteryTypeClick(lotteryType.name)
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
        val dataCount: Int
        withContext(Dispatchers.IO) {
            getLotteryDisplayRow(lotteryType, sortType, displayOrder).also {
                rowList = it.first
                dataCount = it.second
            }
        }

        _viewModelState.emit(
            _viewModelState.value.copy(
                isLoading = false,
                displayOrder = event.order,
                rowList = rowList,
                dataCount = dataCount,
            )
        )
        settingsUseCase.setDisplayOrder(event.order)

        analytics.trackSortingType(sortType.name)
    }

    private suspend fun resetData() {
        val lotteryType = _viewModelState.value.lotteryType
        val sortType = _viewModelState.value.sortType
        val displayOrder = _viewModelState.value.displayOrder
        val rowList: List<Row>
        val dataCount: Int
        withContext(Dispatchers.IO) {
            syncUseCase.clearDatabase()
            getLotteryDisplayRow(lotteryType, sortType, displayOrder).also {
                rowList = it.first
                dataCount = it.second
            }
        }
        _viewModelState.emit(
            _viewModelState.value.copy(
                isLoading = false,
                lotteryType = lotteryType,
                sortType = sortType,
                rowList = rowList,
                dataCount = dataCount,
            )
        )
        syncData(Source.UI)
        reloadLotteryUiData()
    }

    private suspend fun dropData() {
        val lotteryType = _viewModelState.value.lotteryType
        val sortType = _viewModelState.value.sortType
        withContext(Dispatchers.IO) {
            syncUseCase.clearDatabase()
        }
        _viewModelState.emit(
            _viewModelState.value.copy(
                isLoading = false,
                lotteryType = lotteryType,
                sortType = sortType,
                rowList = listOf()
            )
        )
    }

    private suspend fun syncData(source: Source) {
        if (_viewModelState.value.isSyncing) {
            Timber.w("cancel sync because someone is syncing")
            return
        }
        _eventState.emit(LotteryTableEvents.SyncingProgress)
        _viewModelState.emit(_viewModelState.value.copy(isSyncing = true))
        withContext(Dispatchers.IO) {
            val taskId = Calendar.getInstance().time.time.toString()

            LotteryType.values().map { lotteryType ->
                async {
                    syncUseCase.parse(taskId, source.name, lotteryType, this).also {
                        if (it.isFailure) {
                            _eventState.emit(
                                LotteryTableEvents.SyncFailed(
                                    it.exceptionOrNull(),
                                    lotteryType,
                                    when (lotteryType) {
                                        LotteryType.Lto -> R.string.failed_to_load_lto
                                        LotteryType.LtoBig -> R.string.failed_to_load_lto_big
                                        LotteryType.LtoHK -> R.string.failed_to_load_lto_hk
                                        LotteryType.Lto539 -> R.string.failed_to_load_lto_539
                                        LotteryType.LtoCF5 -> R.string.failed_to_load_lto_cf5
                                        LotteryType.LtoList3 -> R.string.failed_to_load_lto_list3
                                        LotteryType.LtoList4 -> R.string.failed_to_load_lto_list4
                                    }
                                )
                            )
                            it.exceptionOrNull()?.let { throwable ->
                                analytics.recordException(throwable)
                            }
                        }
                    }
                }
            }.awaitAll()
        }
        _viewModelState.emit(_viewModelState.value.copy(isSyncing = false))
        _eventState.emit(LotteryTableEvents.EndSync)
        withContext(Dispatchers.IO) {
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
        val dataCount: Int
        val extraSpacing: Int
        withContext(Dispatchers.IO) {
            getLotteryDisplayRow(lotteryType, sortType, displayOrder).also {
                rowList = it.first
                dataCount = it.second
            }
            extraSpacing =
                getLotteryExtraSpacing(lotteryType, settingsDataStoreFlow, viewModelScope)
        }

        _viewModelState.emit(
            _viewModelState.value.copy(
                isLoading = false,
                lotteryType = _viewModelState.value.lotteryType,
                sortType = sortType,
                rowList = rowList,
                normalTableExtraSpacing = extraSpacing,
                listTableExtraSpacing = extraSpacing,
                dataCount = dataCount,
            )
        )
        Timber.e("reloadLotteryUiData")
    }
}

suspend fun getLotteryExtraSpacing(
    lotteryType: LotteryType,
    settingsDataStoreFlow: Flow<Preferences>,
    viewModelScope: CoroutineScope
) = when (lotteryType) {
    LotteryType.Lto -> {
        settingsDataStoreFlow.stateIn(viewModelScope).value.get(
            floatPreferencesKey(
                SETTINGS_EXTRA_SPACING_LTO_TABLE
            )
        )?.toInt() ?: 3
    }

    LotteryType.LtoBig -> {
        settingsDataStoreFlow.stateIn(viewModelScope).value.get(
            floatPreferencesKey(
                SETTINGS_EXTRA_SPACING_LTO_BIG_TABLE
            )
        )?.toInt() ?: 2
    }

    LotteryType.LtoHK -> {
        settingsDataStoreFlow.stateIn(viewModelScope).value.get(
            floatPreferencesKey(
                SETTINGS_EXTRA_SPACING_LTO_HK_TABLE
            )
        )?.toInt() ?: 2
    }

    LotteryType.Lto539, LotteryType.LtoCF5 -> {
        settingsDataStoreFlow.stateIn(viewModelScope).value.get(
            floatPreferencesKey(
                SETTINGS_EXTRA_SPACING_LTO_539_TABLE
            )
        )?.toInt() ?: 5
    }

    LotteryType.LtoList3 -> {
        settingsDataStoreFlow.stateIn(viewModelScope).value.get(
            floatPreferencesKey(
                SETTINGS_EXTRA_SPACING_LIST_TABLE
            )
        )?.toInt() ?: 10
    }

    LotteryType.LtoList4 -> {
        settingsDataStoreFlow.stateIn(viewModelScope).value.get(
            floatPreferencesKey(
                SETTINGS_EXTRA_SPACING_LIST_TABLE
            )
        )?.toInt() ?: 10
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