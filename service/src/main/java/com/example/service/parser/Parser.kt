package com.example.service.parser

import com.example.data.LotteryData
import com.example.data.LotteryRowData
import com.example.data.LotteryType
import java.text.SimpleDateFormat
import java.util.*

abstract class Parser(private val cacheLotteryData: LotteryData? = null) {
    companion object {
        private const val DATE_FORMATTER = "yyyy/MM/dd"
    }

    private var currentPage: Int = 1

    internal val dateFormat = SimpleDateFormat(DATE_FORMATTER, Locale.getDefault())

    fun parse(): Result<LotteryData> {
        val lotteryDataList = mutableSetOf<LotteryRowData>()

        try {
            do {
                android.util.Log.v(
                    "QQQQ",
                    "type: ${getType()}, url: ${getUrl()}"
                )
                lotteryDataList.addAll(parseInternal(getUrl()))
                if (cacheLotteryData != null) {
                    val cacheLotteryDataSet = cacheLotteryData.dataList.toMutableSet()
                    if (!cacheLotteryDataSet.addAll(lotteryDataList)) {
                        // merge and finish
                        lotteryDataList.clear()
                        lotteryDataList.addAll(cacheLotteryDataSet)
                        break
                    }
                }
                ++currentPage
                val minDate = lotteryDataList.toList().minOf { it.date }
                android.util.Log.i("QQQQ", "minDate: $minDate, getLastDataDate(): ${getLastDataDate()}")
            } while (lotteryDataList.isNotEmpty() && minDate > getLastDataDate())
        } catch (exception: Throwable) {
            return Result.failure(exception)
        }

        return Result.success(
            LotteryData(
                dataList = lotteryDataList.toMutableList()
                    .also { it.sortByDescending { lotteryRowData -> lotteryRowData.date } },
                type = getType(),
                normalNumberCount = getNormalCount(),
                specialNumberCount = getSpecialCount(),
            )
        )
    }

    private fun getUrl(): String = "${getBaseUrl()}$currentPage"

    internal abstract fun getBaseUrl(): String

    internal abstract fun parseInternal(url: String): List<LotteryRowData>

    internal abstract fun getLastDataDate(): Long

    internal abstract fun getType(): LotteryType

    internal abstract fun getNormalCount(): Int

    internal abstract fun getSpecialCount(): Int
}