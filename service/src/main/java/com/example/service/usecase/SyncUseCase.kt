package com.example.service.usecase

import com.example.data.LotteryData
import com.example.service.service.ParseService
import timber.log.Timber

class SyncUseCase(val service: ParseService) {

    fun parseLto(taskId: String = ""): Result<LotteryData> {
        return service.parseLto(taskId).also {
            Timber.i("parseLto lotteryData size: ${it.getOrNull()?.dataList?.size}")
        }
    }

    fun parseLtoBig(taskId: String = ""): Result<LotteryData> {
        return service.parseLtoBig(taskId).also {
            Timber.i("parseLtoBig lotteryData size: ${it.getOrNull()?.dataList?.size}")
        }
    }

    fun parseLtoHk(taskId: String = ""): Result<LotteryData> {
        return service.parseLtoHk(taskId).also {
            Timber.i("parseLtoHk lotteryData size: ${it.getOrNull()?.dataList?.size}")
        }
    }

    fun parseLtoList3(taskId: String = ""): Result<LotteryData> {
        return service.parseLtoList3(taskId).also {
            Timber.i("parseLtoList3 lotteryData size: ${it.getOrNull()?.dataList?.size}")
        }
    }

    fun parseLtoList4(taskId: String = ""): Result<LotteryData> {
        return service.parseLtoList4(taskId).also {
            Timber.i("parseLtoList4 lotteryData size: ${it.getOrNull()?.dataList?.size}")
        }
    }

    fun clearDatabase() {
        Timber.w("clearDatabase")
        service.clearDatabase()
    }
}