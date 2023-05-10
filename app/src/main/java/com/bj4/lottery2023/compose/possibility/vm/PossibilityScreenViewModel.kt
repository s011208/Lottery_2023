package com.bj4.lottery2023.compose.possibility.vm

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bj4.lottery2023.compose.lotterytable.vm.Grid
import com.bj4.lottery2023.compose.lotterytable.vm.Row
import com.bj4.lottery2023.compose.lotterytable.vm.toDisplaySize
import com.example.data.LotteryData
import com.example.data.LotteryRowData
import com.example.data.LotteryType
import com.example.myapplication.compose.appsettings.SETTINGS_EXTRA_SPACING_LIST_TABLE
import com.example.myapplication.compose.appsettings.SETTINGS_EXTRA_SPACING_LTO_TABLE
import com.example.myapplication.compose.appsettings.SETTINGS_KEY_FONT_SIZE
import com.example.myapplication.compose.appsettings.settingsDataStore
import com.example.service.cache.FontSize
import com.example.service.usecase.DisplayUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat

class PossibilityScreenViewModel(
    private val displayUseCase: DisplayUseCase,
    context: Context,
) : ViewModel() {

    private val _viewModelState: MutableStateFlow<State> = MutableStateFlow(State())
    val viewModelState: StateFlow<State> = _viewModelState.asStateFlow()

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

                            SETTINGS_EXTRA_SPACING_LIST_TABLE -> {
                                viewModelScope.launch {
                                    _viewModelState.emit(
                                        _viewModelState.value.copy(
                                            listExtraSpacing = (value as Float).toInt()
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
                    val newList: List<PossibilityItem>
                    withContext(Dispatchers.IO) {
                        newList = loadData()
                    }
                    _viewModelState.emit(_viewModelState.value.copy(itemList = newList))
                }
            }

            is PossibilityUiEvent.ChangeNumberOfRows -> {
                viewModelScope.launch {
                    val newList: List<PossibilityItem>
                    withContext(Dispatchers.IO) {
                        newList = loadData(event.number)
                    }
                    _viewModelState.emit(
                        _viewModelState.value.copy(
                            itemList = newList,
                            count = event.number
                        )
                    )
                }
            }
        }
    }

    private fun loadData(count: Int = 100): List<PossibilityItem> {
        val rtn = mutableListOf<PossibilityItem>()
        LotteryType.values().forEach { lotteryType ->
            val data = displayUseCase.getLotteryData(lotteryType) ?: return@forEach
            val realCount = count.coerceAtMost(data.dataList.size)
            val subList = data.dataList.subList(0, realCount)
            when (lotteryType) {
                LotteryType.Lto -> {
                    rtn.add(getNormalLtoPossibility(subList, data, realCount))
                }

                LotteryType.LtoBig -> {
                    rtn.add(getNormalLtoPossibility(subList, data, realCount))
                }

                LotteryType.LtoHK -> {
                    rtn.add(getNormalLtoPossibility(subList, data, realCount))
                }

                LotteryType.LtoList3 -> {

                }

                LotteryType.LtoList4 -> {

                }

                LotteryType.Lto539 -> {
                    rtn.add(getNormalLtoPossibility(subList, data, realCount))
                }
            }
        }
        return rtn
    }

    private fun getNormalLtoPossibility(
        subList: List<LotteryRowData>,
        data: LotteryData,
        count: Int
    ): PossibilityItem {
        val rowList = mutableListOf<Row>()
        val headerGridList = mutableListOf<Grid>()
        val possibilityGridList = mutableListOf<Grid>()
        val normalNumberMap = mutableMapOf<Int, Int>()
        val specialNumberMap = mutableMapOf<Int, Int>()
        val df = DecimalFormat(".##")

        subList.forEach { lotteryRowData ->
            lotteryRowData.normalNumberList.forEach {
                normalNumberMap[it] = normalNumberMap.getOrDefault(it, 0) + 1
            }
            if (!data.isSpecialNumberSeparate) {
                lotteryRowData.specialNumberList.forEach {
                    normalNumberMap[it] = normalNumberMap.getOrDefault(it, 0) + 1
                }
            } else {
                lotteryRowData.specialNumberList.forEach {
                    specialNumberMap[it] = specialNumberMap.getOrDefault(it, 0) + 1
                }
            }
        }
        for (i in 1..data.normalNumberCount) {
            headerGridList.add(
                Grid(
                    index = i,
                    text = i.toString(),
                    visible = true,
                    Grid.Type.NormalPossibility
                )
            )

            val numberCount = normalNumberMap.getOrDefault(
                i,
                0
            )
            possibilityGridList.add(
                Grid(
                    index = i,
                    text = if (numberCount == 0) "0" else df.format(numberCount / count.toFloat())
                        .toString(),
                    visible = true,
                    Grid.Type.NormalPossibility
                )
            )
        }
        if (data.isSpecialNumberSeparate) {
            for (i in 1..data.specialNumberCount) {
                headerGridList.add(
                    Grid(
                        index = i,
                        text = i.toString(),
                        visible = true,
                        Grid.Type.SpecialPossibility
                    )
                )
                val numberCount = specialNumberMap.getOrDefault(
                    i,
                    0
                )
                possibilityGridList.add(
                    Grid(
                        index = i,
                        text = if (numberCount == 0) "0" else df.format(numberCount / count.toFloat())
                            .toString(),
                        visible = true,
                        Grid.Type.SpecialPossibility
                    )
                )
            }
        }
        rowList.add(Row(headerGridList, Row.Type.Header))
        rowList.add(Row(possibilityGridList, Row.Type.LotteryData))
        return PossibilityItem(data.type, rowList)
    }

    data class State(
        val itemList: List<PossibilityItem> = listOf(),
        val fontSize: Int = FontSize.NORMAL.toDisplaySize(),
        val normalExtraSpacing: Int = 0,
        val listExtraSpacing: Int = 0,
        val count: Int = 100,
    )
}

data class PossibilityItem(val lotteryType: LotteryType, val rowList: List<Row>)

sealed class PossibilityUiEvent {
    object Reload : PossibilityUiEvent()

    data class ChangeNumberOfRows(val number: Int) : PossibilityUiEvent()
}