package com.example.service.usecase

import com.example.service.service.ParseService

class SyncUseCase(val service: ParseService) {

    fun parseLto() {
        val lotteryData = service.parseLto()
        android.util.Log.v("QQQQ", "parseLto lotteryData size: ${lotteryData}")
    }

    fun parseLtoBig() {
        val lotteryData = service.parseLtoBig()
        android.util.Log.v("QQQQ", "parseLtoBig lotteryData size: ${lotteryData}")
    }

    fun parseLtoHk() {
        val lotteryData = service.parseLtoHk()
        android.util.Log.v("QQQQ", "parseLtoHk lotteryData size: ${lotteryData}")
    }

    fun clearDatabase() {
        service.clearDatabase()
    }
}