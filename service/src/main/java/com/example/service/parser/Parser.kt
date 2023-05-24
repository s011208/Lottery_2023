package com.example.service.parser

import com.example.analytics.Analytics
import com.example.data.LotteryData
import com.example.data.LotteryRowData
import com.example.data.LotteryType
import org.koin.java.KoinJavaComponent
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

abstract class Parser(private val cacheLotteryData: LotteryData? = null) {
    companion object {
        private const val DATE_FORMATTER = "yyyy/MM/dd"
    }

    private var currentPage: Int = 1

    private val dateFormat = SimpleDateFormat(DATE_FORMATTER, Locale.getDefault())

    val analytics: Analytics by KoinJavaComponent.inject(Analytics::class.java)

    fun parse(): Result<LotteryData> {
        val cacheLotteryDataSet = cacheLotteryData?.dataList?.toMutableSet() ?: mutableSetOf()
        var previousMinDate = Long.MAX_VALUE
        var currentMinDate: Long
        try {
            do {
                Timber.d("type: ${getType()}, url: ${getUrl()}")

                val newDataRows = parseInternal(getUrl())
                currentMinDate =
                    previousMinDate.coerceAtMost(newDataRows.minOfOrNull { it.date } ?: 0)
                previousMinDate = currentMinDate

                if (!cacheLotteryDataSet.addAll(newDataRows)) {
                    // merge and finish
                    Timber.w("${getType()}: break parse")
                    break
                }
                ++currentPage
                Timber.d("minDate: $currentMinDate, getLastDataDate(): ${getLotteryLastDataDate()}, data size: ${cacheLotteryDataSet.size}")
            } while (newDataRows.isNotEmpty() /*&& currentMinDate > getLotteryLastDataDate()*/) // TODO needed?
        } catch (exception: Throwable) {
            Timber.w(exception, "exception")
            return Result.failure(exception)
        }

        return Result.success(
            LotteryData(
                dataList = cacheLotteryDataSet.distinctBy { it.date }.toMutableList()
                    .also { it.sortByDescending { lotteryRowData -> lotteryRowData.date } },
                type = getType(),
                normalNumberCount = getNormalCount(),
                specialNumberCount = getSpecialCount(),
                isSpecialNumberSeparate = isSpecialNumberSeparate(),
            )
        )
    }

    private fun getUrl(): String = "${getBaseUrl()}$currentPage"

    internal abstract fun getBaseUrl(): String

    internal abstract fun parseInternal(url: String): List<LotteryRowData>

    internal abstract fun getLotteryLastDataDate(): Long

    internal abstract fun getType(): LotteryType

    internal abstract fun getNormalCount(): Int

    internal abstract fun getSpecialCount(): Int

    internal abstract fun isSpecialNumberSeparate(): Boolean

    internal open fun getDateFormat(): SimpleDateFormat {
        return dateFormat
    }

    internal open fun dateConverter(date: String): Long {
        val tempDate = getDateFormat().parse(date.substring(0, date.length - 3).trim()) ?: return 0L

        val calendar = Calendar.getInstance()
        calendar.time = tempDate
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.HOUR, 0)
        return calendar.time.time
    }
}