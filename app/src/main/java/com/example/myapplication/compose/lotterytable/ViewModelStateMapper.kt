package com.example.myapplication.compose

import com.example.data.LotteryType
import com.example.myapplication.compose.lotterytable.vm.Row
import com.example.myapplication.compose.lotterytable.vm.ViewModelState
import com.example.service.cache.DisplayOrder
import com.example.service.cache.SortType

object ViewModelStateMapper {
    fun ViewModelState.mapToUiState(): UiState {
        return UiState.Show(rowList, lotteryType, sortType, displayOrder, isLoading, isSyncing)
    }
}

sealed class UiState {
    data class Show(
        val rowList: List<Row>,
        val lotteryType: LotteryType,
        val sortType: SortType,
        val displayOrder: DisplayOrder,
        val isLoading: Boolean,
        val isSyncing: Boolean,
    ) : UiState()
}