package com.bj4.lottery2023.compose.plusminus.vm

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bj4.lottery2023.ImmutableListWrapper
import com.bj4.lottery2023.compose.general.Grid
import com.bj4.lottery2023.compose.general.Row
import com.bj4.lottery2023.getMonth
import com.example.data.LotteryData
import com.example.data.LotteryRowData
import com.example.data.LotteryType
import com.example.myapplication.compose.appsettings.SETTINGS_EXTRA_SPACING_PLUS_MINUS
import com.example.myapplication.compose.appsettings.SETTINGS_FONT_SIZE_PLUS_MINUS
import com.example.myapplication.compose.appsettings.SETTINGS_SHOW_DIVIDE_LINE_PLUS_MINUS
import com.example.myapplication.compose.appsettings.settingsDataStore
import com.example.service.cache.FontSize
import com.example.service.usecase.DisplayUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Suppress("SENSELESS_COMPARISON")
class PlusMinusViewModel(
    private val displayUseCase: DisplayUseCase,
    context: Context,
) : ViewModel() {

    private val settingsDataStoreFlow = context.settingsDataStore.data

    companion object {
        private const val DATE_FORMAT = "yyyy/MM/dd"
    }

    private val _viewModelState: MutableStateFlow<State> = MutableStateFlow(
        State(
            fontSize = FontSize.NORMAL.toFontSize(),
            spacing = 20,
            showDivideLine = false,
        )
    )
    val viewModelState: StateFlow<State> = _viewModelState.asStateFlow()

    private val _eventState = MutableSharedFlow<PlusMinusEvent>()
    val eventStateSharedFlow = _eventState.asSharedFlow()

    init {
        viewModelScope.launch {
            loadLotteryData(lotteryType = LotteryType.Lto539, deltaValue = 0)
        }

        viewModelScope.launch {
            settingsDataStoreFlow
                .distinctUntilChanged()
                .collect { preference ->
                    preference.asMap().forEach { (key, value) ->
                        Timber.e("key: $key")
                        when (key.name) {
                            SETTINGS_EXTRA_SPACING_PLUS_MINUS -> {
                                viewModelScope.launch {
                                    changeExtraSpacing((value as Float).toInt())
                                }
                            }

                            SETTINGS_SHOW_DIVIDE_LINE_PLUS_MINUS -> {
                                viewModelScope.launch {
                                    changeShowDivideLine(value as Boolean)
                                }
                            }

                            SETTINGS_FONT_SIZE_PLUS_MINUS -> {
                                viewModelScope.launch {
                                    changeFontSize(FontSize.valueOf(value as String))
                                }
                            }

                            else -> {}
                        }
                    }
                }
        }
    }

    fun handleEvent(event: PlusMinusEvent) {
        when (event) {
            is PlusMinusEvent.ChangeLotteryType -> {
                viewModelScope.launch {
                    changeLotteryType(event.newLotteryType)
                }
            }

            is PlusMinusEvent.ChangeDeltaValue -> {
                viewModelScope.launch {
                    changeDeltaValue(event.newValue)
                }
            }

            PlusMinusEvent.ScrollToBottom,
            PlusMinusEvent.ScrollToTop -> {
                viewModelScope.launch {
                    _eventState.emit(event)
                }
            }
        }
    }

    private suspend fun changeShowDivideLine(show: Boolean) {
        viewModelScope.launch {
            _viewModelState.emit(
                _viewModelState.value.copy(
                    showDivideLine = show
                )
            )
        }
    }

    private suspend fun changeExtraSpacing(spacing: Int) {
        viewModelScope.launch {
            _viewModelState.emit(
                _viewModelState.value.copy(
                    spacing = spacing
                )
            )
        }
    }

    private suspend fun changeFontSize(fontSize: FontSize) {
        viewModelScope.launch {
            _viewModelState.emit(
                _viewModelState.value.copy(
                    fontSize = fontSize.toFontSize()
                )
            )
        }
    }

    private fun FontSize.toFontSize() = when (this) {
        FontSize.EXTRA_SMALL -> 20
        FontSize.SMALL -> 24
        FontSize.NORMAL -> 28
        FontSize.LARGE -> 32
        FontSize.EXTRA_LARGE -> 36
    }

    private suspend fun changeLotteryType(newLotteryType: LotteryType) {
        loadLotteryData(newLotteryType, _viewModelState.value.deltaValue)
    }

    private suspend fun changeDeltaValue(newValue: Int) {
        loadLotteryData(_viewModelState.value.lotteryType, newValue)
    }

    private suspend fun loadLotteryData(lotteryType: LotteryType, deltaValue: Int) {
        val lotteryData: LotteryData =
            withContext(Dispatchers.IO) {
                displayUseCase.getLotteryData(
                    lotteryType
                )
            } ?: return
        val monthlyTotalDateFormat = SimpleDateFormat("MM", Locale.getDefault())

        val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        val rtn = mutableListOf<Row>()
        val rowCount = lotteryData.dataList.size
        val sortedRow = lotteryData.dataList.sortedBy { it.date }

        var previousMonth =
            if (lotteryData.dataList.isEmpty()) {
                Calendar.JANUARY
            } else {
                sortedRow.first().date.getMonth()
            }
        val monthlyCountList = mutableMapOf<Int, Int>()

        sortedRow.forEachIndexed { rowIndex, lotteryRowData ->
            if (lotteryRowData.date <= 0 ||
                lotteryRowData.normalNumberList == null ||
                lotteryRowData.normalNumberList.isEmpty() ||
                lotteryRowData.specialNumberList == null ||
                (lotteryRowData.specialNumberList.isEmpty() && lotteryData.type != LotteryType.Lto539)
            ) {
                // safe check for unexpected data
                return@forEachIndexed
            }

            val gridList = grids(
                dateFormat,
                lotteryRowData,
                rowIndex,
                rowCount,
                sortedRow,
                deltaValue,
                lotteryData
            )


            val currentMonth = lotteryRowData.date.getMonth()
            if (previousMonth != lotteryRowData.date.getMonth() || rowIndex == lotteryData.dataList.size - 1) {
                val addDataFirst = rowIndex == lotteryData.dataList.size - 1
                if (addDataFirst) {
                    gridList.forEachIndexed { index, grid ->
                        if (grid.type == Grid.Type.Special) {
                            monthlyCountList[index] = monthlyCountList.getOrDefault(index, 0) + 1
                        }
                    }
                    rtn.add(Row(gridList, Row.Type.LotteryData))
                }
                previousMonth = currentMonth
                val subTotalGridList = mutableListOf(
                    Grid(
                        text = monthlyTotalDateFormat.format(
                            sortedRow[rowIndex - if (addDataFirst) {
                                0
                            } else {
                                1
                            }].date
                        ),
                        type = Grid.Type.Date,
                    )
                )
                for (index in 1..(lotteryRowData.specialNumberList.size + lotteryRowData.normalNumberList.size)) {
                    subTotalGridList.add(
                        Grid(
                            index = index,
                            text = monthlyCountList.getOrDefault(index, 0).toString(),
                            type = Grid.Type.Special
                        )
                    )
                }
                rtn.add(Row(subTotalGridList, Row.Type.MonthlyTotal))
                monthlyCountList.clear()
                if (!addDataFirst) {
                    gridList.forEachIndexed { index, grid ->
                        if (grid.type == Grid.Type.Special) {
                            monthlyCountList[index] = monthlyCountList.getOrDefault(index, 0) + 1
                        }
                    }
                    rtn.add(Row(gridList, Row.Type.LotteryData))
                }
            } else {
                gridList.forEachIndexed { index, grid ->
                    if (grid.type == Grid.Type.Special) {
                        monthlyCountList[index] = monthlyCountList.getOrDefault(index, 0) + 1
                    }
                }
                rtn.add(Row(gridList, Row.Type.LotteryData))
            }
        }

        viewModelScope.launch {
            _viewModelState.emit(
                _viewModelState.value.copy(
                    lotteryType = lotteryType,
                    deltaValue = deltaValue,
                    rowListWrapper = ImmutableListWrapper(rtn)
                )
            )
        }
    }

    private fun grids(
        dateFormat: SimpleDateFormat,
        lotteryRawData: LotteryRowData,
        rowIndex: Int,
        rowCount: Int,
        sortedRow: List<LotteryRowData>,
        deltaValue: Int,
        lotteryData: LotteryData
    ): MutableList<Grid> {
        val gridList = mutableListOf<Grid>()
        gridList.add(Grid(text = dateFormat.format(lotteryRawData.date), type = Grid.Type.Date))
        (lotteryRawData.normalNumberList + lotteryRawData.specialNumberList).forEachIndexed { index, value ->
            handleNumbers(
                rowIndex,
                rowCount,
                gridList,
                index,
                value,
                sortedRow,
                deltaValue,
                lotteryData
            )
        }
        return gridList
    }

    private fun handleNumbers(
        rowIndex: Int,
        rowCount: Int,
        gridList: MutableList<Grid>,
        index: Int,
        value: Int,
        sortedRow: List<LotteryRowData>,
        deltaValue: Int,
        lotteryData: LotteryData
    ) {
        val isNotTheLastLine = rowIndex < rowCount - 1
        if (isNotTheLastLine) {
            gridList.add(
                Grid(
                    index = index,
                    text = value.toString(),
                    type = if (hit(
                            sortedRow[rowIndex + 1],
                            value,
                            deltaValue,
                            lotteryData.normalNumberCount
                        )
                    ) {
                        Grid.Type.Special
                    } else {
                        Grid.Type.Normal
                    }
                )
            )
        } else {
            gridList.add(
                Grid(
                    index = index,
                    text = value.toString(),
                    type = Grid.Type.Normal
                )
            )
        }
    }

    private fun hit(
        nextLotteryRowData: LotteryRowData,
        compareValue: Int,
        delta: Int,
        numberCount: Int
    ): Boolean {
        val numberList = nextLotteryRowData.normalNumberList + nextLotteryRowData.specialNumberList
        return ((compareValue + delta + numberCount) % numberCount in numberList)
    }

    data class State(
        val lotteryType: LotteryType = LotteryType.Lto539,
        val deltaValue: Int = 0,
        val fontSize: Int = 30,
        val spacing: Int = 20,
        val rowListWrapper: ImmutableListWrapper<Row> = ImmutableListWrapper(listOf()),
        val showDivideLine: Boolean = false,
    )
}

sealed class PlusMinusEvent {

    data class ChangeLotteryType(
        val newLotteryType: LotteryType,
    ) : PlusMinusEvent()

    data class ChangeDeltaValue(
        val newValue: Int,
    ) : PlusMinusEvent()

    object ScrollToBottom : PlusMinusEvent()

    object ScrollToTop : PlusMinusEvent()
}