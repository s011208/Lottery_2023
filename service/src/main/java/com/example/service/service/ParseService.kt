package com.example.service.service

import com.example.data.LotteryData
import com.example.data.LotteryType
import com.example.service.cache.LotteryDataDatabase
import com.example.service.parser.LtoBigParser
import com.example.service.parser.LtoHkParser
import com.example.service.parser.LtoParser
import org.koin.java.KoinJavaComponent.inject
import timber.log.Timber

class ParseService {

    private val database: LotteryDataDatabase by inject(LotteryDataDatabase::class.java)

    fun parseLto(): Result<LotteryData> {
        val result = LtoParser(database.userDao().getLottery(LotteryType.Lto.toString())).parse()
        if (result.isSuccess) {
            result.onSuccess {
                database.userDao().insertAll(it)
            }
        } else {
            Timber.w(result.exceptionOrNull(), "parseLto failed")
        }
        return result
    }

    fun parseLtoBig(): Result<LotteryData> {
        val result =
            LtoBigParser(database.userDao().getLottery(LotteryType.LtoBig.toString())).parse()
        if (result.isSuccess) {
            result.onSuccess {
                database.userDao().insertAll(it)
            }
        }
        return result
    }

    fun parseLtoHk(): Result<LotteryData> {
        val result =
            LtoHkParser(database.userDao().getLottery(LotteryType.LtoHK.toString())).parse()
        if (result.isSuccess) {
            result.onSuccess {
                database.userDao().insertAll(it)
            }
        }
        return result
    }

    fun clearDatabase() {
        database.userDao().delete()
    }
}