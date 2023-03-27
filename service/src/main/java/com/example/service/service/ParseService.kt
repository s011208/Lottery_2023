package com.example.service.service

import com.example.data.LotteryData
import com.example.data.LotteryType
import com.example.service.cache.LotteryDataDatabase
import com.example.service.parser.LtoBigParser
import com.example.service.parser.LtoHkParser
import com.example.service.parser.LtoParser
import org.koin.java.KoinJavaComponent.inject

class ParseService {

    private val database: LotteryDataDatabase by inject(LotteryDataDatabase::class.java)

    fun parseLto(): Result<LotteryData> {
        val result = LtoParser(database.userDao().getLottery(LotteryType.Lto.toString())).parse()
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

    fun parseLtoBig(): Result<LotteryData> {
        val result = LtoBigParser(database.userDao().getLottery(LotteryType.LtoBig.toString())).parse()
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

    fun parseLtoHk(): Result<LotteryData> {
        val result = LtoHkParser(database.userDao().getLottery(LotteryType.LtoHK.toString())).parse()
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

    fun clearDatabase() {
        database.userDao().delete()
    }
}