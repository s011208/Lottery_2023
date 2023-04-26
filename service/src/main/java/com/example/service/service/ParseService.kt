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

    fun parseLto(taskId: String = ""): Result<LotteryData> {
        val result = LtoParser(database.userDao().getLottery(LotteryType.Lto.toString())).parse()
        if (result.isSuccess) {
            result.onSuccess {
                CoroutineScope(dispatcher).launch {
                    database.userDao().insertAll(it)
                    logDatabase.userDao().insertAll(
                        LotteryLog(
                            timeStamp = System.currentTimeMillis(),
                            type = LotteryType.Lto,
                            state = LoadingState.SUCCESS,
                            taskId = taskId,
                        )
                    )
                }
            }
        } else {
            Timber.w(result.exceptionOrNull(), "parseLto failed")
            CoroutineScope(dispatcher).launch {
                logDatabase.userDao().insertAll(
                    LotteryLog(
                        timeStamp = System.currentTimeMillis(),
                        type = LotteryType.Lto,
                        state = LoadingState.ERROR,
                        errorMessage = result.exceptionOrNull()?.message ?: "",
                        taskId = taskId,
                    )
                )
            }
        }
        return result
    }

    fun parseLtoBig(taskId: String = ""): Result<LotteryData> {
        val result =
            LtoBigParser(database.userDao().getLottery(LotteryType.LtoBig.toString())).parse()
        if (result.isSuccess) {
            result.onSuccess {
                CoroutineScope(dispatcher).launch {
                    database.userDao().insertAll(it)
                    logDatabase.userDao().insertAll(
                        LotteryLog(
                            timeStamp = System.currentTimeMillis(),
                            type = LotteryType.LtoBig,
                            state = LoadingState.SUCCESS,
                            taskId = taskId,
                        )
                    )
                }
            }
        } else {
            Timber.w(result.exceptionOrNull(), "parseLto failed")
            CoroutineScope(dispatcher).launch {
                logDatabase.userDao().insertAll(
                    LotteryLog(
                        timeStamp = System.currentTimeMillis(),
                        type = LotteryType.LtoBig,
                        state = LoadingState.ERROR,
                        errorMessage = result.exceptionOrNull()?.message ?: "",
                        taskId = taskId,
                    )
                )
            }
        }
        return result
    }

    fun parseLtoHk(taskId: String = ""): Result<LotteryData> {
        val result =
            LtoHkParser(database.userDao().getLottery(LotteryType.LtoHK.toString())).parse()
        if (result.isSuccess) {
            result.onSuccess {
                CoroutineScope(dispatcher).launch {
                    database.userDao().insertAll(it)
                    logDatabase.userDao().insertAll(
                        LotteryLog(
                            timeStamp = System.currentTimeMillis(),
                            type = LotteryType.LtoHK,
                            state = LoadingState.SUCCESS,
                            taskId = taskId,
                        )
                    )
                }
            }
        } else {
            Timber.w(result.exceptionOrNull(), "parseLto failed")
            CoroutineScope(dispatcher).launch {
                logDatabase.userDao().insertAll(
                    LotteryLog(
                        timeStamp = System.currentTimeMillis(),
                        type = LotteryType.LtoHK,
                        state = LoadingState.ERROR,
                        errorMessage = result.exceptionOrNull()?.message ?: "",
                        taskId = taskId,
                    )
                )
            }
        }
        return result
    }

    fun parseLtoList3(taskId: String = ""): Result<LotteryData> {
        val result =
            LtoList3Parser(database.userDao().getLottery(LotteryType.LtoList3.toString())).parse()
        if (result.isSuccess) {
            result.onSuccess {
                CoroutineScope(dispatcher).launch {
                    database.userDao().insertAll(it)
                    logDatabase.userDao().insertAll(
                        LotteryLog(
                            timeStamp = System.currentTimeMillis(),
                            type = LotteryType.LtoList3,
                            state = LoadingState.SUCCESS,
                            taskId = taskId,
                        )
                    )
                }
            }
        } else {
            Timber.w(result.exceptionOrNull(), "parseLtoList3 failed")
            CoroutineScope(dispatcher).launch {
                logDatabase.userDao().insertAll(
                    LotteryLog(
                        timeStamp = System.currentTimeMillis(),
                        type = LotteryType.LtoList3,
                        state = LoadingState.ERROR,
                        errorMessage = result.exceptionOrNull()?.message ?: "",
                        taskId = taskId,
                    )
                )
            }
        }
        return result
    }

    fun parseLtoList4(taskId: String = ""): Result<LotteryData> {
        val result =
            LtoList4Parser(database.userDao().getLottery(LotteryType.LtoList4.toString())).parse()
        if (result.isSuccess) {
            result.onSuccess {
                CoroutineScope(dispatcher).launch {
                    database.userDao().insertAll(it)
                    logDatabase.userDao().insertAll(
                        LotteryLog(
                            timeStamp = System.currentTimeMillis(),
                            type = LotteryType.LtoList4,
                            state = LoadingState.SUCCESS,
                            taskId = taskId,
                        )
                    )
                }
            }
        } else {
            Timber.w(result.exceptionOrNull(), "parseLtoList4 failed")
            CoroutineScope(dispatcher).launch {
                logDatabase.userDao().insertAll(
                    LotteryLog(
                        timeStamp = System.currentTimeMillis(),
                        type = LotteryType.LtoList4,
                        state = LoadingState.ERROR,
                        errorMessage = result.exceptionOrNull()?.message ?: "",
                        taskId = taskId,
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