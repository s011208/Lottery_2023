package com.example.service.service

import com.example.data.LotteryData
import com.example.service.cache.LotteryDataDatabase
import com.example.service.parser.LtoParser
import org.koin.java.KoinJavaComponent.inject

class ParseService {

    private val database: LotteryDataDatabase by inject(LotteryDataDatabase::class.java)

    fun parseLto(): Result<LotteryData> {
        val result =  LtoParser().parse()
        if (result.isSuccess) {
            result.onSuccess {
                database.userDao().insertAll(it)

                val all = database.userDao().getAll()
                android.util.Log.v("QQQQ", "all: ${all.size}")
                if (all.isNotEmpty()) {
                    android.util.Log.v("QQQQ", "first size: ${all.first().dataList.size}")
                }
            }
        }
        return result
    }
}