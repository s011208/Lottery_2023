package com.example.service.service

import android.annotation.SuppressLint
import android.content.Context
import com.example.data.LoadingState
import com.example.data.LotteryData
import com.example.data.LotteryLog
import com.example.data.LotteryType
import com.example.service.cache.log.LotteryLogDatabase
import com.example.service.cache.lto.LotteryDataDatabase
import com.example.service.parser.Lto539Parser
import com.example.service.parser.LtoBigParser
import com.example.service.parser.LtoCF5Parser
import com.example.service.parser.LtoHkParser
import com.example.service.parser.LtoList3Parser
import com.example.service.parser.LtoList4Parser
import com.example.service.parser.LtoParser
import com.example.service.parser.Parser
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import timber.log.Timber
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.Locale


class ParseService(private val context: Context) {

    private val database: LotteryDataDatabase by inject(LotteryDataDatabase::class.java)
    private val logDatabase: LotteryLogDatabase by inject(LotteryLogDatabase::class.java)

    private suspend fun getLotteryParser(lotteryType: LotteryType): Parser {
        var lotteryData: LotteryData? = database.userDao().getLottery(lotteryType.toString())
        if (lotteryData == null ) {
            Timber.d("init data from file")
            lotteryData =
                Gson().fromJson(readFromFile(lotteryType), LotteryData::class.java)
            Timber.d("init data from file done: ${lotteryData?.dataList?.size}")
        }
        return when (lotteryType) {
            LotteryType.Lto -> LtoParser(lotteryData)
            LotteryType.LtoBig -> LtoBigParser(lotteryData)
            LotteryType.LtoHK -> LtoHkParser(lotteryData)
            LotteryType.LtoList3 -> LtoList3Parser(lotteryData)
            LotteryType.LtoList4 -> LtoList4Parser(lotteryData)
            LotteryType.Lto539 -> Lto539Parser(lotteryData)
            LotteryType.LtoCF5 -> LtoCF5Parser(lotteryData)
        }
    }

    private fun LotteryType.getFileName(): String = toString().lowercase(Locale.getDefault())

    @SuppressLint("DiscouragedApi")
    private fun readFromFile(type: LotteryType): String {
        val resource = try {
            context.resources.getIdentifier(type.getFileName(), "raw", context.packageName)
        } catch (e: Exception) {
            Timber.w(e, "failed to read resource")
            return ""
        }
        val inputStream: InputStream = context.resources.openRawResource(resource)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        var eachline = bufferedReader.readLine()
        var result = ""
        while (eachline != null) {
            result += eachline
            eachline = bufferedReader.readLine()
        }
        bufferedReader.close()
        return result
    }

    private fun writeToFile(data: String, type: LotteryType) {
        try {
            val outputStreamWriter =
                OutputStreamWriter(context.openFileOutput(type.getFileName(), Context.MODE_PRIVATE))
            outputStreamWriter.write(data)
            outputStreamWriter.close()
        } catch (e: IOException) {
            Timber.w(e, "Exception", "File write failed")
        }
    }

    suspend fun parse(
        taskId: String = "",
        syncSource: String = "",
        lotteryType: LotteryType,
        scope: CoroutineScope
    ): Result<LotteryData> {
        val result = getLotteryParser(lotteryType).parse()
        if (result.isSuccess) {
            result.onSuccess {
                scope.launch {
//                    if (BuildConfig.DEBUG) {
//                        writeToFile(Gson().toJson(it), lotteryType)
//                    }
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
            scope.launch {
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

    fun deleteLottery(lotteryType: LotteryType) {
        database.userDao().deleteLottery(lotteryType.toString())
    }
}