package com.example.myapplication.compose.lotterytable.vm

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
    val normalTableExtraSpacing: Int = 0,
    val listTableExtraSpacing: Int = 0,
)

data class Row(
    val dataList: List<Grid>,
    val type: Type
) {
    enum class Type {
        MonthlyTotal, Header, LotteryData
    }
}

data class Grid(
    val index: Int = -1,
    val text: String = "",
    val visible: Boolean = true,
    val type: Type
) {
    enum class Type {
        Normal, Date, Special, NormalPossibility, SpecialPossibility,
    }
}