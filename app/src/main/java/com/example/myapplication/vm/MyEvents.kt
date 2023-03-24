package com.example.myapplication.vm

import com.example.data.LotteryType
import com.example.myapplication.SortType

sealed class MyEvents {

    object StartSync : MyEvents()

    data class EndSync(val error: Throwable? = null) : MyEvents()

    data class SyncingProgress(val type: LotteryType) : MyEvents()

    data class ChangeSortType(val type: SortType): MyEvents()

    data class ChangeLotteryType(val type: LotteryType): MyEvents()
}