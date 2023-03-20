package com.example.service.usecase

import com.example.data.LotteryData
import com.example.service.service.ParseService

class SyncUseCase(val service: ParseService) {

    fun parseLto() {
        val lotteryData = service.parseLto()
        android.util.Log.v("QQQQ", "parseLto lotteryData size: ${lotteryData}")
    }
}