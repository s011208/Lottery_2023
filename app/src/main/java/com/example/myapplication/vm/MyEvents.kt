package com.example.myapplication.vm

import com.example.data.LotteryType

sealed class MyEvents {

    object StartSync : MyEvents()

    data class EndSync(val error: Throwable? = null) : MyEvents()

    data class SyncingProgress(val type: LotteryType) : MyEvents()


}