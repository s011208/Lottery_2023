package com.example.service.usecase

import com.example.data.LotteryData
import com.example.data.LotteryType
import com.example.service.cache.LotteryDataDatabase
import org.koin.java.KoinJavaComponent

class DisplayUseCase {
    private val database: LotteryDataDatabase by KoinJavaComponent.inject(LotteryDataDatabase::class.java)

    fun getLotteryData(lotteryType: LotteryType): LotteryData? {
        return database.userDao().getLottery(lotteryType.toString())
    }
}