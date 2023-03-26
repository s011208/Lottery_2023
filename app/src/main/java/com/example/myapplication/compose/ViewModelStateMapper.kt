package com.example.myapplication.compose

import com.example.data.LotteryType
import com.example.service.cache.SortType
import com.example.myapplication.vm.Row
import com.example.myapplication.vm.ViewModelState
import com.example.service.cache.DisplayOrder

object ViewModelStateMapper {
    fun ViewModelState.mapToUiState() : UiState {
        return if (rowList.isEmpty()) UiState.Empty
        else if (isLoading) UiState.Loading(loadingHint)
        else UiState.Show(rowList, lotteryType, sortType, displayOrder)
    }
}

sealed class UiState {
    object Empty: UiState()
    data class Loading(val hint: String): UiState()
    data class Show(val rowList: List<Row>, val lotteryType: LotteryType, val sortType: SortType, val displayOrder: DisplayOrder): UiState()
}