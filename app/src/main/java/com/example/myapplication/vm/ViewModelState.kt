package com.example.myapplication.vm

import com.example.data.LotteryType
import com.example.myapplication.SortType

data class ViewModelState(
    val lotteryType: LotteryType = LotteryType.Lto,
    val sortType: SortType = SortType.NormalOrder,
    val rowList: List<Row> = listOf(),
    val isLoading: Boolean = false,
    val loadingHint: String = "",
    val fontSize: Int = 16,
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
        Normal, Date, Special
    }
}