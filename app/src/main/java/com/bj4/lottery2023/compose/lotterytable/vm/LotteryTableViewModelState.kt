package com.bj4.lottery2023.compose.lotterytable.vm

import com.bj4.lottery2023.compose.general.Row
import com.example.data.LotteryType
import com.example.service.cache.DayNightMode
import com.example.service.cache.DisplayOrder
import com.example.service.cache.FontSize
import com.example.service.cache.SortType

data class ViewModelState(
    val lotteryType: LotteryType = LotteryType.Lto,
    val sortType: SortType = SortType.NormalOrder,
    val rowList: List<Row> = listOf(),
    val isLoading: Boolean = false,
    val fontSize: Int = 16,
    val fontType: FontSize = FontSize.NORMAL,
    val displayOrder: DisplayOrder = DisplayOrder.DESCEND,
    val isSyncing: Boolean = false,
    val dayNightSettings: DayNightMode = DayNightMode.AUTO,
    val normalTableExtraSpacing: Int = 3,
    val listTableExtraSpacing: Int = 0,
    val showDivideLine: Boolean = false,
    val dataCount: Int = 0,
)