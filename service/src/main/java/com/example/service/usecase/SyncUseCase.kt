package com.example.service.usecase

import com.example.service.service.ParseService
import timber.log.Timber

class SyncUseCase(val service: ParseService) {

    fun parseLto() {
        val lotteryData = service.parseLto()
        Timber.d("parseLto lotteryData size: ${lotteryData.getOrNull()?.dataList?.size}")
    }

    fun parseLtoBig() {
        val lotteryData = service.parseLtoBig()
        Timber.d("parseLtoBig lotteryData size: ${lotteryData.getOrNull()?.dataList?.size}")
    }

    fun parseLtoHk() {
        val lotteryData = service.parseLtoHk()
        Timber.d("parseLtoHk lotteryData size: ${lotteryData.getOrNull()?.dataList?.size}")
    }

    fun clearDatabase() {
        Timber.w("clearDatabase")
        service.clearDatabase()
    }
}