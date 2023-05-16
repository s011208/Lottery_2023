package com.bj4.lottery2023.compose.possibility.vm

import com.example.data.LotteryType

sealed class PossibilityUiEvent {
    object Reload : PossibilityUiEvent()

    data class ChangeNumberOfRows(val numberString: String) : PossibilityUiEvent()

    data class ChangeLotteryType(
        val newLotteryType: LotteryType,
    ) : PossibilityUiEvent()

    data class WrongFormat(val text: String) : PossibilityUiEvent()

    data class ShowOrderByIndex(val show: Boolean) : PossibilityUiEvent()

    data class ShowOrderByAsc(val show: Boolean) : PossibilityUiEvent()

    data class ShowOrderByDesc(val show: Boolean) : PossibilityUiEvent()
}