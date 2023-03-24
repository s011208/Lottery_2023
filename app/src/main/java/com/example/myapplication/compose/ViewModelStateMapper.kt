package com.example.myapplication.compose

import com.example.data.LotteryType
import com.example.myapplication.SortType
import com.example.myapplication.vm.Row
import com.example.myapplication.vm.ViewModelState

object ViewModelStateMapper {
    fun ViewModelState.mapToUiState() : UiState {
        if (rowList.isEmpty()) return UiState.Empty
        else if (isLoading) return UiState.Loading(loadingHint)
        else return UiState.Show(rowList, lotteryType, sortType)
    }
}

sealed class UiState {
    object Empty: UiState()
    data class Loading(val hint: String): UiState()
    data class Show(val rowList: List<Row>, val lotteryType: LotteryType, val sortType: SortType): UiState()
}