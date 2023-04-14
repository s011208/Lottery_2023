package com.example.myapplication.compose.lotterytable.vm

import androidx.annotation.StringRes
import com.example.data.LotteryType
import com.example.service.cache.DayNightMode
import com.example.service.cache.DisplayOrder
import com.example.service.cache.SortType
import com.example.service.cache.FontSize

sealed class LotteryTableEvents {

    data class StartSync(val source: Source = Source.UNKNOWN) : LotteryTableEvents()

    object EndSync : LotteryTableEvents()

    data class SyncFailed(
        val error: Throwable?,
        val lotteryType: LotteryType,
        @StringRes val textResource: Int
    ) : LotteryTableEvents()

    object SyncingProgress : LotteryTableEvents()

    data class ChangeSortType(val type: SortType) : LotteryTableEvents()

    data class ChangeLotteryType(val type: LotteryType) : LotteryTableEvents()

    data class ChangeDisplayOrder(val order: DisplayOrder) : LotteryTableEvents()

    object ScrollToBottom : LotteryTableEvents()

    object ScrollToTop : LotteryTableEvents()

    data class ChangeFontSize(val fontSize: FontSize) : LotteryTableEvents()

    data class ChangeDayNightSettings(val dayNightSettings: DayNightMode): LotteryTableEvents()

    data class FontSizeChanged(val fontSize: Int) : LotteryTableEvents()

    object UpdateData : LotteryTableEvents()

    object ResetData : LotteryTableEvents()
}