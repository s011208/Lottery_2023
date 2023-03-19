package com.example.service.service

import com.example.service.cache.LotteryDataDatabase
import org.koin.java.KoinJavaComponent.inject

class ParseService {
    val myPresenter : LotteryDataDatabase by inject(LotteryDataDatabase::class.java)
}