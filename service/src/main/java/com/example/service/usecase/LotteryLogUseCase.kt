package com.example.service.usecase

import com.example.service.cache.log.LotteryLogDatabase
import org.koin.java.KoinJavaComponent

class LotteryLogUseCase {

    private val database: LotteryLogDatabase by KoinJavaComponent.inject(LotteryLogDatabase::class.java)

    fun getAll() = database.userDao().getAll()

    fun clear() = database.clearAllTables()
}