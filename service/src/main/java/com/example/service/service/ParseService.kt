package com.example.service.service

import com.example.data.LoadingState
import com.example.data.LotteryData
import com.example.data.LotteryLog
import com.example.data.LotteryType
import com.example.service.cache.log.LotteryLogDatabase
import com.example.service.cache.lto.LotteryDataDatabase
import com.example.service.parser.*
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

    fun parseLto(): Result<LotteryData> {
        val result = LtoParser(database.userDao().getLottery(LotteryType.Lto.toString())).parse()
        if (result.isSuccess) {
            result.onSuccess {
                CoroutineScope(dispatcher).launch {
                    database.userDao().insertAll(it)
                    logDatabase.userDao().insertAll(
                        LotteryLog(
                            System.currentTimeMillis(),
                            LotteryType.Lto,
                            LoadingState.SUCCESS
                        )
                    )
                }
            }
        } else {
            Timber.w(result.exceptionOrNull(), "parseLto failed")
            CoroutineScope(dispatcher).launch {
                logDatabase.userDao().insertAll(
                    LotteryLog(
                        System.currentTimeMillis(),
                        LotteryType.Lto,
                        LoadingState.ERROR,
                        result.exceptionOrNull()?.message ?: ""
                    )
                )
            }
        }
        return result
    }

    fun parseLtoBig(): Result<LotteryData> {
        val result =
            LtoBigParser(database.userDao().getLottery(LotteryType.LtoBig.toString())).parse()
        if (result.isSuccess) {
            result.onSuccess {
                CoroutineScope(dispatcher).launch {
                    database.userDao().insertAll(it)
                    logDatabase.userDao().insertAll(
                        LotteryLog(
                            System.currentTimeMillis(),
                            LotteryType.LtoBig,
                            LoadingState.SUCCESS
                        )
                    )
                }
            }
        } else {
            Timber.w(result.exceptionOrNull(), "parseLto failed")
            CoroutineScope(dispatcher).launch {
                logDatabase.userDao().insertAll(
                    LotteryLog(
                        System.currentTimeMillis(),
                        LotteryType.LtoBig,
                        LoadingState.ERROR,
                        result.exceptionOrNull()?.message ?: ""
                    )
                )
            }
        }
        return result
    }

    fun parseLtoHk(): Result<LotteryData> {
        val result =
            LtoHkParser(database.userDao().getLottery(LotteryType.LtoHK.toString())).parse()
        if (result.isSuccess) {
            result.onSuccess {
                CoroutineScope(dispatcher).launch {
                    database.userDao().insertAll(it)
                    logDatabase.userDao().insertAll(
                        LotteryLog(
                            System.currentTimeMillis(),
                            LotteryType.LtoHK,
                            LoadingState.SUCCESS
                        )
                    )
                }
            }
        } else {
            Timber.w(result.exceptionOrNull(), "parseLto failed")
            CoroutineScope(dispatcher).launch {
                logDatabase.userDao().insertAll(
                    LotteryLog(
                        System.currentTimeMillis(),
                        LotteryType.LtoHK,
                        LoadingState.ERROR,
                        result.exceptionOrNull()?.message ?: ""
                    )
                )
            }
        }
        return result
    }

    fun parseLtoList3(): Result<LotteryData> {
        val result =
            LtoList3Parser(database.userDao().getLottery(LotteryType.LtoList3.toString())).parse()
        if (result.isSuccess) {
            result.onSuccess {
                CoroutineScope(dispatcher).launch {
                    database.userDao().insertAll(it)
                    logDatabase.userDao().insertAll(
                        LotteryLog(
                            System.currentTimeMillis(),
                            LotteryType.LtoList3,
                            LoadingState.SUCCESS
                        )
                    )
                }
            }
        } else {
            Timber.w(result.exceptionOrNull(), "parseLtoList3 failed")
            CoroutineScope(dispatcher).launch {
                logDatabase.userDao().insertAll(
                    LotteryLog(
                        System.currentTimeMillis(),
                        LotteryType.LtoList3,
                        LoadingState.ERROR,
                        result.exceptionOrNull()?.message ?: ""
                    )
                )
            }
        }
        return result
    }

    fun parseLtoList4(): Result<LotteryData> {
        val result =
            LtoList4Parser(database.userDao().getLottery(LotteryType.LtoList4.toString())).parse()
        if (result.isSuccess) {
            result.onSuccess {
                CoroutineScope(dispatcher).launch {
                    database.userDao().insertAll(it)
                    logDatabase.userDao().insertAll(
                        LotteryLog(
                            System.currentTimeMillis(),
                            LotteryType.LtoList4,
                            LoadingState.SUCCESS
                        )
                    )
                }
            }
        } else {
            Timber.w(result.exceptionOrNull(), "parseLtoList4 failed")
            CoroutineScope(dispatcher).launch {
                logDatabase.userDao().insertAll(
                    LotteryLog(
                        System.currentTimeMillis(),
                        LotteryType.LtoList4,
                        LoadingState.ERROR,
                        result.exceptionOrNull()?.message ?: ""
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