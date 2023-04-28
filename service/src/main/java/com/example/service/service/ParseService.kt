package com.example.service.service

import com.example.data.LoadingState
import com.example.data.LotteryData
import com.example.data.LotteryLog
import com.example.data.LotteryType
import com.example.service.cache.log.LotteryLogDatabase
import com.example.service.cache.lto.LotteryDataDatabase
import com.example.service.parser.LtoBigParser
import com.example.service.parser.LtoHkParser
import com.example.service.parser.LtoList3Parser
import com.example.service.parser.LtoList4Parser
import com.example.service.parser.LtoParser
import com.example.service.parser.Parser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import timber.log.Timber
import java.util.concurrent.Executors

class ParseService {

    private val database: LotteryDataDatabase by inject(LotteryDataDatabase::class.java)
    private val logDatabase: LotteryLogDatabase by inject(LotteryLogDatabase::class.java)

    private val dispatcher = Executors.newFixedThreadPool(1).asCoroutineDispatcher()

    private fun getLotteryParser(lotteryType: LotteryType): Parser {
        val lotteryData = database.userDao().getLottery(lotteryType.toString())
        return when (lotteryType) {
            LotteryType.Lto -> LtoParser(lotteryData)
            LotteryType.LtoBig -> LtoBigParser(lotteryData)
            LotteryType.LtoHK -> LtoHkParser(lotteryData)
            LotteryType.LtoList3 -> LtoList3Parser(lotteryData)
            LotteryType.LtoList4 -> LtoList4Parser(lotteryData)
        }
    }

    fun parse(
        taskId: String = "",
        syncSource: String = "",
        lotteryType: LotteryType
    ): Result<LotteryData> {
        val result = getLotteryParser(lotteryType).parse()
        if (result.isSuccess) {
            result.onSuccess {
                CoroutineScope(dispatcher).launch {
                    database.userDao().insertAll(it)
                    logDatabase.userDao().insertAll(
                        LotteryLog(
                            timeStamp = System.currentTimeMillis(),
                            type = lotteryType,
                            state = LoadingState.SUCCESS,
                            taskId = taskId,
                            source = syncSource,
                        )
                    )
                }
            }
        } else {
            Timber.w(result.exceptionOrNull(), "parse${lotteryType.name} failed")
            CoroutineScope(dispatcher).launch {
                logDatabase.userDao().insertAll(
                    LotteryLog(
                        timeStamp = System.currentTimeMillis(),
                        type = lotteryType,
                        state = LoadingState.ERROR,
                        errorMessage = result.exceptionOrNull()?.message ?: "",
                        taskId = taskId,
                        source = syncSource,
                    )
                )
            }
        }
        return result
    }

    fun clearDatabase() {
        database.userDao().delete()
    }
}