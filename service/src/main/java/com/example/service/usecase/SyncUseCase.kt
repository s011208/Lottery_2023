package com.example.service.usecase

import com.example.data.LotteryData
import com.example.data.LotteryType
import com.example.service.service.ParseService
import timber.log.Timber

class SyncUseCase(val service: ParseService) {

    fun parse(taskId: String, source: String, type: LotteryType): Result<LotteryData> {
        return service.parse(taskId, source, type).also {
            Timber.i("parse${type.name} lotteryData size: ${it.getOrNull()?.dataList?.size}")
        }
    }

    fun clearDatabase() {
        Timber.w("clearDatabase")
        service.clearDatabase()
    }
}