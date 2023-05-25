@file:Suppress("SENSELESS_COMPARISON")

package com.bj4.lottery2023.compose.possibility.vm

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bj4.lottery2023.R
import com.bj4.lottery2023.compose.general.Grid
import com.bj4.lottery2023.compose.general.Row
import com.bj4.lottery2023.compose.lotterytable.vm.getLotteryExtraSpacing
import com.bj4.lottery2023.compose.lotterytable.vm.toDisplaySize
import com.example.data.LotteryData
import com.example.data.LotteryType
import com.example.myapplication.compose.appsettings.SETTINGS_EXTRA_SPACING_LTO_539_TABLE
import com.example.myapplication.compose.appsettings.SETTINGS_EXTRA_SPACING_LTO_BIG_TABLE
import com.example.myapplication.compose.appsettings.SETTINGS_EXTRA_SPACING_LTO_HK_TABLE
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

class PossibilityScreenViewModel(
    private val displayUseCase: DisplayUseCase,
    context: Context,
) : ViewModel() {

    private val _viewModelState: MutableStateFlow<State> = MutableStateFlow(State())
    val viewModelState: StateFlow<State> = _viewModelState.asStateFlow()

    private val _eventState = MutableSharedFlow<PossibilityUiEvent>()
    val eventStateSharedFlow = _eventState.asSharedFlow()

    private val settingsDataStoreFlow = context.settingsDataStore.data

    init {
        viewModelScope.launch {
            settingsDataStoreFlow.distinctUntilChanged().collect { preference ->
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

                        SETTINGS_EXTRA_SPACING_LTO_539_TABLE -> {
                            viewModelScope.launch {
                                val current = (value as Float).toInt()
                                if (_viewModelState.value.extraSpacing != current &&
                                    _viewModelState.value.lotteryType == LotteryType.Lto539
                                ) {
                                    _viewModelState.emit(
                                        _viewModelState.value.copy(
                                            extraSpacing = current
                                        )
                                    )
                                }
                            }
                        }

                        SETTINGS_EXTRA_SPACING_LTO_BIG_TABLE -> {
                            viewModelScope.launch {
                                val current = (value as Float).toInt()
                                if (_viewModelState.value.extraSpacing != current &&
                                    _viewModelState.value.lotteryType == LotteryType.LtoBig
                                ) {
                                    _viewModelState.emit(
                                        _viewModelState.value.copy(
                                            extraSpacing = current
                                        )
                                    )
                                }
                            }
                        }

                        SETTINGS_EXTRA_SPACING_LTO_HK_TABLE -> {
                            viewModelScope.launch {
                                val current = (value as Float).toInt()
                                if (_viewModelState.value.extraSpacing != current &&
                                    _viewModelState.value.lotteryType == LotteryType.LtoHK
                                ) {
                                    _viewModelState.emit(
                                        _viewModelState.value.copy(
                                            extraSpacing = current
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

    fun handle(event: PossibilityUiEvent) {
        when (event) {
            PossibilityUiEvent.Reload -> {
                val lotteryType = _viewModelState.value.lotteryType

                viewModelScope.launch {
                    val extraSpacing: Int
                    withContext(Dispatchers.IO) {
                        extraSpacing = getLotteryExtraSpacing(
                            lotteryType,
                            settingsDataStoreFlow,
                            viewModelScope
                        )
                    }
                    val chartList =
                        reload(lotteryType, _viewModelState.value.count)
                    _viewModelState.emit(
                        _viewModelState.value.copy(
                            chartList = chartList,
                            extraSpacing = extraSpacing,
                        )
                    )
                }
            }

            is PossibilityUiEvent.ChangeNumberOfRows -> {
                val number = event.numberString.toInt()
                if (number == _viewModelState.value.count) return
                viewModelScope.launch {
                    val lotteryType = _viewModelState.value.lotteryType
                    try {
                        val chartList = reload(lotteryType, number)
                        val extraSpacing: Int
                        withContext(Dispatchers.IO) {
                            extraSpacing = getLotteryExtraSpacing(
                                lotteryType,
                                settingsDataStoreFlow,
                                viewModelScope
                            )
                        }
                        _viewModelState.emit(
                            _viewModelState.value.copy(
                                chartList = chartList, count = number,
                                extraSpacing = extraSpacing,
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
                val lotteryType = event.newLotteryType
                if (lotteryType == _viewModelState.value.lotteryType) {
                    return
                }
                viewModelScope.launch {
                    val chartList = reload(lotteryType, _viewModelState.value.count)
                    val extraSpacing: Int
                    withContext(Dispatchers.IO) {
                        extraSpacing = getLotteryExtraSpacing(
                            lotteryType,
                            settingsDataStoreFlow,
                            viewModelScope
                        )
                    }
                    _viewModelState.emit(
                        _viewModelState.value.copy(
                            chartList = chartList, lotteryType = lotteryType,
                            extraSpacing = extraSpacing,
                        )
                    )
                }
            }

            is PossibilityUiEvent.ShowOrderByIndex -> {
                viewModelScope.launch {
                    _viewModelState.emit(_viewModelState.value.copy(showByIndex = event.show))
                    handle(PossibilityUiEvent.Reload)
                }
            }

            is PossibilityUiEvent.ShowOrderByAsc -> {
                viewModelScope.launch {
                    _viewModelState.emit(_viewModelState.value.copy(showByAscent = event.show))
                    handle(PossibilityUiEvent.Reload)
                }
            }

            is PossibilityUiEvent.ShowOrderByDesc -> {
                viewModelScope.launch {
                    _viewModelState.emit(_viewModelState.value.copy(showByDescent = event.show))
                    handle(PossibilityUiEvent.Reload)
                }
            }

            else -> {}
        }
    }

    private suspend fun reload(lotteryType: LotteryType, count: Int): MutableList<Chart> {
        val chartList: MutableList<Chart> = mutableListOf()
        withContext(Dispatchers.IO) {
            possibilityChart(lotteryType, count, chartList)

            noShowUntilTodayChart(lotteryType, count, chartList)
        }
        return chartList
    }

    private fun noShowUntilTodayChart(
        lotteryType: LotteryType,
        count: Int,
        chartList: MutableList<Chart>
    ) {
        val noShowUntilTodayOrderByIndex =
            getNoShowUntilToday(lotteryType, count)
        if (_viewModelState.value.showByIndex && noShowUntilTodayOrderByIndex.isValid()) {
            chartList.add(
                noShowUntilTodayOrderByIndex.copy(
                    indexRow = noShowUntilTodayOrderByIndex.indexRow?.addIndexTitle(),
                    countRow = noShowUntilTodayOrderByIndex.countRow?.addTimeTitle()
                )
            )
        }

        if (_viewModelState.value.showByAscent && noShowUntilTodayOrderByIndex.isValid()) {
            val countRowOrderByAscent =
                noShowUntilTodayOrderByIndex.countRow!!.dataList.sortedBy { it.text.toInt() }
                    .toMutableList().also {
                        it.add(0, timeTitleGrid())
                    }
            val indexRowOrderByAscent =
                noShowUntilTodayOrderByIndex.indexRow!!.dataList.sortedBy { indexGrid ->
                    countRowOrderByAscent.indexOfFirst { grid -> grid.index == indexGrid.index }
                }.toMutableList().also {
                    it.add(0, indexTitleGrid())
                }
            chartList.add(
                Chart.TableChart.NoShowUntilTodayOrderByAscent(
                    indexRow = Row(indexRowOrderByAscent, Row.Type.Header),
                    countRow = Row(countRowOrderByAscent, Row.Type.LotteryData)
                )
            )
        }

        if (_viewModelState.value.showByDescent && noShowUntilTodayOrderByIndex.isValid()) {
            val countRowOrderByDescent =
                noShowUntilTodayOrderByIndex.countRow!!.dataList.sortedByDescending { it.text.toInt() }
                    .toMutableList().also {
                        it.add(0, timeTitleGrid())
                    }
            val indexRowOrderByDescent =
                noShowUntilTodayOrderByIndex.indexRow!!.dataList.sortedBy { indexGrid ->
                    countRowOrderByDescent.indexOfFirst { grid -> grid.index == indexGrid.index }
                }.toMutableList().also {
                    it.add(0, indexTitleGrid())
                }
            chartList.add(
                Chart.TableChart.NoShowUntilTodayOrderByDescent(
                    indexRow = Row(indexRowOrderByDescent, Row.Type.Header),
                    countRow = Row(countRowOrderByDescent, Row.Type.LotteryData)
                )
            )
        }
    }

    private fun possibilityChart(
        lotteryType: LotteryType,
        count: Int,
        chartList: MutableList<Chart>
    ) {
        val possibilityChartOrderByIndex = getPossibilityChartOrderByIndex(
            lotteryType, count
        )
        if (_viewModelState.value.showByIndex && possibilityChartOrderByIndex.isValid()) {
            chartList.add(
                possibilityChartOrderByIndex.copy(
                    indexRow = possibilityChartOrderByIndex.indexRow?.addIndexTitle(),
                    countRow = possibilityChartOrderByIndex.countRow?.addTimeTitle()
                )
            )
        }

        if (_viewModelState.value.showByAscent && possibilityChartOrderByIndex.isValid()) {
            val countRowOrderByAscent =
                possibilityChartOrderByIndex.countRow!!.dataList.sortedBy { it.text.toInt() }
                    .toMutableList().also {
                        it.add(0, timeTitleGrid())
                    }
            val indexRowOrderByAscent =
                possibilityChartOrderByIndex.indexRow!!.dataList.sortedBy { indexGrid ->
                    countRowOrderByAscent.indexOfFirst { grid -> grid.index == indexGrid.index }
                }.toMutableList().also {
                    it.add(0, indexTitleGrid())
                }
            chartList.add(
                Chart.TableChart.PossibilityListOrderByAscent(
                    indexRow = Row(indexRowOrderByAscent, Row.Type.Header),
                    countRow = Row(countRowOrderByAscent, Row.Type.LotteryData)
                )
            )
        }

        if (_viewModelState.value.showByDescent && possibilityChartOrderByIndex.isValid()) {
            val countRowOrderByDescent =
                possibilityChartOrderByIndex.countRow!!.dataList.sortedByDescending { it.text.toInt() }
                    .toMutableList().also {
                        it.add(0, timeTitleGrid())
                    }
            val indexRowOrderByDescent =
                possibilityChartOrderByIndex.indexRow!!.dataList.sortedBy { indexGrid ->
                    countRowOrderByDescent.indexOfFirst { grid -> grid.index == indexGrid.index }
                }.toMutableList().also {
                    it.add(0, indexTitleGrid())
                }
            chartList.add(
                Chart.TableChart.PossibilityListOrderByDescent(
                    indexRow = Row(indexRowOrderByDescent, Row.Type.Header),
                    countRow = Row(countRowOrderByDescent, Row.Type.LotteryData)
                )
            )
        }
    }

    private fun getNoShowUntilToday(
        lotteryType: LotteryType, count: Int
    ): Chart.TableChart.NoShowUntilToday {
        val lotteryData = displayUseCase.getLotteryData(lotteryType)
            ?: return Chart.TableChart.NoShowUntilToday()

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

        sortedRow.forEach { row ->
            countNormalMap.forEach { (key, value) -> countNormalMap[key] = value + 1 }
            countSpecialMap.forEach { (key, value) -> countNormalMap[key] = value + 1 }
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
            countNormalMap, lotteryData, countSpecialMap
        )

        return Chart.TableChart.NoShowUntilToday(
            Row(indexGridList, Row.Type.Header), Row(countGridList, Row.Type.LotteryData)
        )
    }

    private fun Row.addIndexTitle(): Row {
        return copy(dataList = mutableListOf<Grid>().also {
            it.add(
                indexTitleGrid()
            )
            it.addAll(dataList)
        })
    }

    private fun indexTitleGrid() = Grid(
        index = -1, textResource = R.string.possibility_index, type = Grid.Type.Date
    )

    private fun Row.addTimeTitle(): Row {
        return copy(dataList = mutableListOf<Grid>().also {
            it.add(
                timeTitleGrid()
            )
            it.addAll(dataList)
        })
    }

    private fun timeTitleGrid() = Grid(
        index = -1, textResource = R.string.possibility_times, type = Grid.Type.Date
    )

    private fun getPossibilityChartOrderByIndex(
        lotteryType: LotteryType, count: Int
    ): Chart.TableChart.PossibilityList {
        val lotteryData =
            displayUseCase.getLotteryData(lotteryType) ?: return Chart.TableChart.PossibilityList()

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
            if (lotteryRowData.date <= 0 || lotteryRowData.normalNumberList == null || lotteryRowData.normalNumberList.isEmpty() || lotteryRowData.specialNumberList == null || (lotteryRowData.specialNumberList.isEmpty() && lotteryData.type != LotteryType.Lto539 && lotteryData.type != LotteryType.LtoCF5)) {
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
            countNormalMap, lotteryData, countSpecialMap
        )

        return Chart.TableChart.PossibilityList(
            Row(indexGridList, Row.Type.Header), Row(countGridList, Row.Type.LotteryData)
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
                    index = index, text = index.toString(), type = Grid.Type.Normal
                )
            )
            countGridListNormal.add(
                Grid(
                    index = index, text = countNormalMap[index].toString(), type = Grid.Type.Normal
                )
            )
        }
        val indexGridListSpecial = mutableListOf<Grid>()
        val countGridListSpecial = mutableListOf<Grid>()
        if (lotteryData.isSpecialNumberSeparate) {
            for (index in 1..countSpecialMap.size) {
                indexGridListSpecial.add(
                    Grid(
                        index = index, text = index.toString(), type = Grid.Type.Special
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
        val extraSpacing: Int = 0,
        val count: Int = 100,
        val lotteryType: LotteryType = LotteryType.Lto539,
        val showByIndex: Boolean = true,
        val showByAscent: Boolean = true,
        val showByDescent: Boolean = true,
    )
}