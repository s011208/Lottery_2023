package com.example.service.usecase

import com.example.data.LotteryData
import com.example.data.LotteryType
import com.example.service.service.ParseService
import kotlinx.coroutines.CoroutineScope
import timber.log.Timber

class SyncUseCase(val service: ParseService) {

    suspend fun parse(taskId: String, source: String, type: LotteryType, scope: CoroutineScope): Result<LotteryData> {
        return service.parse(taskId, source, type, scope).also {
            Timber.i("parse${type.name} lotteryData size: ${it.getOrNull()?.dataList?.size}")
        }
    }

    fun clearDatabase() {
        Timber.w("clearDatabase")
        service.clearDatabase()
    }
}