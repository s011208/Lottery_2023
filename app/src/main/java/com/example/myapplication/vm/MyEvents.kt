package com.example.myapplication.vm

import com.example.data.LotteryType
import com.example.myapplication.SortType
import com.example.myapplication.compose.AppToolbarSettings
import com.example.myapplication.compose.appsettings.FontSize

sealed class MyEvents {

    object StartSync : MyEvents()

    data class EndSync(val error: Throwable? = null) : MyEvents()

    data class SyncingProgress(val type: LotteryType) : MyEvents()

    data class ChangeSortType(val type: SortType): MyEvents()

    data class ChangeLotteryType(val type: LotteryType): MyEvents()

    object ScrollToBottom: MyEvents()

    object ScrollToTop: MyEvents()

    data class ChangeFontSize(val fontSize: FontSize): MyEvents()

    data class FontSizeChanged(val fontSize: Int): MyEvents()
}