package com.example.myapplication.vm

import com.example.data.LotteryType
import com.example.service.cache.DisplayOrder
import com.example.service.cache.SortType
import com.example.service.cache.FontSize

sealed class MyEvents {

    data class StartSync(val source: Source = Source.UNKNOWN) : MyEvents()

    data class EndSync(val error: Throwable? = null) : MyEvents()

    object SyncingProgress : MyEvents()

    data class ChangeSortType(val type: SortType): MyEvents()

    data class ChangeLotteryType(val type: LotteryType): MyEvents()

    data class ChangeDisplayOrder(val order: DisplayOrder): MyEvents()

    object ScrollToBottom: MyEvents()

    object ScrollToTop: MyEvents()

    data class ChangeFontSize(val fontSize: FontSize): MyEvents()

    data class FontSizeChanged(val fontSize: Int): MyEvents()

    object UpdateData: MyEvents()

    object ResetData: MyEvents()
}