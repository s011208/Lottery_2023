package com.example.service.usecase

import com.example.data.LotteryData
import com.example.service.service.ParseService
import timber.log.Timber

class SyncUseCase(val service: ParseService) {

    fun parseLto(): Result<LotteryData> {
        return service.parseLto().also {
            Timber.i("parseLto lotteryData size: ${it.getOrNull()?.dataList?.size}")
        }
    }

    fun parseLtoBig(): Result<LotteryData> {
        return service.parseLtoBig().also {
            Timber.i("parseLtoBig lotteryData size: ${it.getOrNull()?.dataList?.size}")
        }
    }

    fun parseLtoHk(): Result<LotteryData> {
        return service.parseLtoHk().also {
            Timber.i("parseLtoHk lotteryData size: ${it.getOrNull()?.dataList?.size}")
        }
    }

    fun clearDatabase() {
        Timber.w("clearDatabase")
        service.clearDatabase()
    }
}