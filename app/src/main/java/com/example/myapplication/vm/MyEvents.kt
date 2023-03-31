package com.example.myapplication.vm

import androidx.annotation.StringRes
import com.example.data.LotteryType
import com.example.service.cache.DayNightMode
import com.example.service.cache.DisplayOrder
import com.example.service.cache.SortType
import com.example.service.cache.FontSize

sealed class MyEvents {

    data class StartSync(val source: Source = Source.UNKNOWN) : MyEvents()

    object EndSync : MyEvents()

    data class SyncFailed(
        val error: Throwable?,
        val lotteryType: LotteryType,
        @StringRes val textResource: Int
    ) : MyEvents()

    object SyncingProgress : MyEvents()

    data class ChangeSortType(val type: SortType) : MyEvents()

    data class ChangeLotteryType(val type: LotteryType) : MyEvents()

    data class ChangeDisplayOrder(val order: DisplayOrder) : MyEvents()

    object ScrollToBottom : MyEvents()

    object ScrollToTop : MyEvents()

    data class ChangeFontSize(val fontSize: FontSize) : MyEvents()

    data class ChangeDayNightSettings(val dayNightSettings: DayNightMode): MyEvents()

    data class FontSizeChanged(val fontSize: Int) : MyEvents()

    object UpdateData : MyEvents()

    object ResetData : MyEvents()
}