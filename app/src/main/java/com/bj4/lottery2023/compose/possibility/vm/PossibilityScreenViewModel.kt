@file:Suppress("SENSELESS_COMPARISON")

package com.bj4.lottery2023.compose.possibility.vm

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bj4.lottery2023.compose.general.Grid
import com.bj4.lottery2023.compose.general.Row
import com.bj4.lottery2023.compose.lotterytable.vm.toDisplaySize
import com.example.data.LotteryData
import com.example.data.LotteryType
import com.example.myapplication.compose.appsettings.SETTINGS_EXTRA_SPACING_LTO_TABLE
import com.example.myapplication.compose.appsettings.SETTINGS_KEY_FONT_SIZE
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

class PossibilityScreenViewModel(
    private val displayUseCase: DisplayUseCase,
    context: Context,
) : ViewModel() {

    private val _viewModelState: MutableStateFlow<State> = MutableStateFlow(State())
    val viewModelState: StateFlow<State> = _viewModelState.asStateFlow()

    private val _eventState = MutableSharedFlow<PossibilityUiEvent>()
    val eventStateSharedFlow = _eventState.asSharedFlow()

    init {
        viewModelScope.launch {
            context.settingsDataStore.data
                .distinctUntilChanged()
                .collect { preference ->
                    preference.asMap().forEach { (key, value) ->
                        when (key.name) {
                            SETTINGS_KEY_FONT_SIZE -> {
                                viewModelScope.launch {
                                    _viewModelState.emit(
                                        _viewModelState.value.copy(
                                            fontSize = FontSize.valueOf(
                                                value as String
                                            ).toDisplaySize()
                                        )
                                    )
                                }
                            }

                            SETTINGS_EXTRA_SPACING_LTO_TABLE -> {
                                viewModelScope.launch {
                                    _viewModelState.emit(
                                        _viewModelState.value.copy(
                                            normalExtraSpacing = (value as Float).toInt()
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
        }
    }

    fun handle(event: PossibilityUiEvent) {
        when (event) {
            PossibilityUiEvent.Reload -> {
                viewModelScope.launch {
                    val chartList =
                        reload(_viewModelState.value.lotteryType, _viewModelState.value.count)
                    _viewModelState.emit(_viewModelState.value.copy(chartList = chartList))
                }
            }

            is PossibilityUiEvent.ChangeNumberOfRows -> {
                viewModelScope.launch {
                    try {
                        val number = event.numberString.toInt()
                        val chartList = reload(_viewModelState.value.lotteryType, number)
                        _viewModelState.emit(
                            _viewModelState.value.copy(
                                chartList = chartList,
                                count = number
                            )
                        )
                    } catch (
                        throwable: Throwable
                    ) {
                        _eventState.emit(PossibilityUiEvent.WrongFormat(event.numberString))
                    }
                }
            }

            is PossibilityUiEvent.ChangeLotteryType -> {
                viewModelScope.launch {
                    val chartList =
                        reload(event.newLotteryType, _viewModelState.value.count)
                    _viewModelState.emit(
                        _viewModelState.value.copy(
                            chartList = chartList,
                            lotteryType = event.newLotteryType
                        )
                    )
                }
            }

            else -> {}
        }
    }

    private suspend fun reload(lotteryType: LotteryType, count: Int): MutableList<Chart> {
        val chartList: MutableList<Chart> = mutableListOf()
        withContext(Dispatchers.IO) {
            chartList.addAll(
                getPossibilityChart(
                    lotteryType,
                    count
                ).makePossibilityOrderList()
            )
            chartList.add(
                getPossibilityListNoShowUntilToday(
                    lotteryType,
                    count
                )
            )
        }
        return chartList
    }

    private fun getPossibilityListNoShowUntilToday(
        lotteryType: LotteryType,
        count: Int
    ): Chart.PossibilityListNoShowUntilToday {
        val lotteryData =
            displayUseCase.getLotteryData(lotteryType)
                ?: return Chart.PossibilityListNoShowUntilToday()

        val sortedRow = lotteryData.dataList.sortedByDescending { it.date }
            .subList(0, count.coerceAtMost(lotteryData.dataList.size)).reversed()

        val countNormalMap = mutableMapOf<Int, Int>()
        val countSpecialMap = mutableMapOf<Int, Int>()
        for (index in 1..lotteryData.normalNumberCount) {
            countNormalMap[index] = 0
        }
        for (index in 1..lotteryData.specialNumberCount) {
            countSpecialMap[index] = 0
        }
        Timber.e("sortedRow size: ${sortedRow.size}")
        sortedRow.forEach { row ->
            countNormalMap.forEach { (key, value) -> countNormalMap[key] = value + 1 }
            countSpecialMap.forEach { (key, value) -> countNormalMap[key] = value + 1 }

            Timber.e("$countNormalMap")


            row.normalNumberList.forEach { number ->
                countNormalMap[number] = 0
            }
            if (!lotteryData.isSpecialNumberSeparate) {
                row.specialNumberList.forEach { number ->
                    countNormalMap[number] = 0
                }
            } else {
                row.specialNumberList.forEach { number ->
                    countSpecialMap[number] = 0
                }
            }
        }

        val (indexGridList, countGridList) = convertToRowPair(
            countNormalMap,
            lotteryData,
            countSpecialMap
        )

        return Chart.PossibilityListNoShowUntilToday(
            Row(indexGridList, Row.Type.Header),
            Row(countGridList, Row.Type.LotteryData)
        )
    }

    private fun Chart.PossibilityList.makePossibilityOrderList(): List<Chart> {
        val rtn = mutableListOf<Chart>()
        if (this.isValid()) {
            rtn.add(this)

            val countRowOrderByLowPossibility =
                this.countRow!!.dataList.sortedBy { it.text.toInt() }
            val indexRowOrderByLowPossibility =
                this.indexRow!!.dataList.sortedBy { indexGrid ->
                    countRowOrderByLowPossibility.indexOfFirst { grid -> grid.index == indexGrid.index }
                }

            rtn.add(
                Chart.PossibilityListOrderByLowest(
                    Row(
                        indexRowOrderByLowPossibility,
                        Row.Type.Header
                    ),
                    Row(countRowOrderByLowPossibility, Row.Type.LotteryData)
                )
            )

            rtn.add(
                Chart.PossibilityListOrderByHighest(
                    Row(
                        indexRowOrderByLowPossibility.reversed(),
                        Row.Type.Header
                    ),
                    Row(
                        countRowOrderByLowPossibility.reversed(),
                        Row.Type.LotteryData
                    )
                )
            )
        }
        return rtn
    }

    private fun getPossibilityChart(
        lotteryType: LotteryType,
        count: Int
    ): Chart.PossibilityList {
        val lotteryData =
            displayUseCase.getLotteryData(lotteryType) ?: return Chart.PossibilityList()

        val sortedRow = lotteryData.dataList.sortedByDescending { it.date }
            .subList(0, count.coerceAtMost(lotteryData.dataList.size))
        val countNormalMap = mutableMapOf<Int, Int>()
        val countSpecialMap = mutableMapOf<Int, Int>()

        for (index in 1..lotteryData.normalNumberCount) {
            countNormalMap[index] = 0
        }
        for (index in 1..lotteryData.specialNumberCount) {
            countSpecialMap[index] = 0
        }

        sortedRow.forEach { lotteryRowData ->
            if (lotteryRowData.date <= 0 ||
                lotteryRowData.normalNumberList == null ||
                lotteryRowData.normalNumberList.isEmpty() ||
                lotteryRowData.specialNumberList == null ||
                (lotteryRowData.specialNumberList.isEmpty() && lotteryData.type != LotteryType.Lto539)
            ) {
                // safe check for unexpected data
                return@forEach
            }
            lotteryRowData.normalNumberList.forEach { number ->
                countNormalMap[number] = countNormalMap[number]!! + 1
            }

            if (!lotteryData.isSpecialNumberSeparate) {
                lotteryRowData.specialNumberList.forEach { number ->
                    countNormalMap[number] = countNormalMap[number]!! + 1
                }
            } else {
                lotteryRowData.specialNumberList.forEach { number ->
                    countSpecialMap[number] = countSpecialMap[number]!! + 1
                }
            }
        }

        val (indexGridList, countGridList) = convertToRowPair(
            countNormalMap,
            lotteryData,
            countSpecialMap
        )

        return Chart.PossibilityList(
            Row(indexGridList, Row.Type.Header),
            Row(countGridList, Row.Type.LotteryData)
        )
    }

    private fun convertToRowPair(
        countNormalMap: MutableMap<Int, Int>,
        lotteryData: LotteryData,
        countSpecialMap: MutableMap<Int, Int>
    ): Pair<MutableList<Grid>, MutableList<Grid>> {
        val indexGridListNormal = mutableListOf<Grid>()
        val countGridListNormal = mutableListOf<Grid>()
        for (index in 1..countNormalMap.size) {
            indexGridListNormal.add(
                Grid(
                    index = index,
                    text = index.toString(),
                    type = Grid.Type.Normal
                )
            )
            countGridListNormal.add(
                Grid(
                    index = index,
                    text = countNormalMap[index].toString(),
                    type = Grid.Type.Normal
                )
            )
        }
        val indexGridListSpecial = mutableListOf<Grid>()
        val countGridListSpecial = mutableListOf<Grid>()
        if (lotteryData.isSpecialNumberSeparate) {
            for (index in 1..countSpecialMap.size) {
                indexGridListSpecial.add(
                    Grid(
                        index = index,
                        text = index.toString(),
                        type = Grid.Type.Special
                    )
                )
                countGridListSpecial.add(
                    Grid(
                        index = index,
                        text = countSpecialMap[index].toString(),
                        type = Grid.Type.Special
                    )
                )
            }
            indexGridListSpecial.addAll(indexGridListSpecial)
            countGridListSpecial.addAll(countGridListSpecial)
        }
        return Pair(indexGridListNormal, countGridListNormal)
    }

    data class State(
        val chartList: List<Chart> = listOf(),
        val fontSize: Int = FontSize.NORMAL.toDisplaySize(),
        val normalExtraSpacing: Int = 0,
        val count: Int = 100,
        val lotteryType: LotteryType = LotteryType.Lto539,
    )
}

sealed class Chart {
    data class PossibilityList(val indexRow: Row? = null, val countRow: Row? = null) : Chart() {
        fun isValid() = !indexRow?.dataList.isNullOrEmpty() && !countRow?.dataList.isNullOrEmpty()
    }

    data class PossibilityListOrderByLowest(val indexRow: Row? = null, val countRow: Row? = null) :
        Chart()

    data class PossibilityListOrderByHighest(val indexRow: Row? = null, val countRow: Row? = null) :
        Chart()

    data class PossibilityListNoShowUntilToday(
        val indexRow: Row? = null,
        val countRow: Row? = null
    ) : Chart()
}

sealed class PossibilityUiEvent {
    object Reload : PossibilityUiEvent()

    data class ChangeNumberOfRows(val numberString: String) : PossibilityUiEvent()

    data class ChangeLotteryType(
        val newLotteryType: LotteryType,
    ) : PossibilityUiEvent()

    data class WrongFormat(val text: String) : PossibilityUiEvent()
}