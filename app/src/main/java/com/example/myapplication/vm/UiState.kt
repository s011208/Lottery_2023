package com.example.myapplication.vm

import com.example.data.LotteryType
import com.example.service.SortType

sealed class UiState {
    object Empty : UiState()

    data class Show(
        val lotteryType: LotteryType,
        val sortType: SortType,
        val rowList: List<Row>
    ) : UiState()
}

data class Row(
    val dataList: List<Grid>
)

data class Grid(
    val index: Int = -1,
    val text: String = "",
    val visible: Boolean = true,
    val type: Type
) {
    enum class Type {
        Normal, MonthlyTotal, Total, Date, ColumnTitle, Special, SpecialColumnTitle
    }
}