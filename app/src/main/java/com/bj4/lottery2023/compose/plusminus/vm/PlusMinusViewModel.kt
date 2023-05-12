package com.bj4.lottery2023.compose.plusminus.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bj4.lottery2023.ImmutableListWrapper
import com.bj4.lottery2023.compose.general.Grid
import com.bj4.lottery2023.compose.general.Row
import com.example.data.LotteryData
import com.example.data.LotteryRowData
import com.example.data.LotteryType
import com.example.service.usecase.DisplayUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class PlusMinusViewModel(
    private val displayUseCase: DisplayUseCase
) : ViewModel() {

    companion object {
        private const val DATE_FORMAT = "yyyy/MM/dd"
    }

    private val _viewModelState: MutableStateFlow<State> = MutableStateFlow(
        State()
    )
    val viewModelState: StateFlow<State> = _viewModelState.asStateFlow()

    private val _eventState = MutableSharedFlow<PlusMinusEvent>()
    val eventStateSharedFlow = _eventState.asSharedFlow()

    init {
        viewModelScope.launch {
            loadLotteryData(lotteryType = LotteryType.Lto539, deltaValue = 0)
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
        val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        val rtn = mutableListOf<Row>()
        val rowCount = lotteryData.dataList.size
        val sortedRow = lotteryData.dataList.sortedBy { it.date }
        sortedRow.forEachIndexed { rowIndex, lotteryRawData ->
            val gridList = grids(
                dateFormat,
                lotteryRawData,
                rowIndex,
                rowCount,
                sortedRow,
                deltaValue,
                lotteryData
            )
            rtn.add(Row(gridList, Row.Type.LotteryData))
        }
        viewModelScope.launch {
            _viewModelState.emit(
                State(
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
        val lotteryType: LotteryType = LotteryType.Lto,
        val deltaValue: Int = 0,
        val fontSize: Int = 30,
        val spacing: Int = 20,
        val rowListWrapper: ImmutableListWrapper<Row> = ImmutableListWrapper(listOf())
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