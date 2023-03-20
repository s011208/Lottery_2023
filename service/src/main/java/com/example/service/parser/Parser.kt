package com.example.service.parser

import com.example.data.LotteryData
import com.example.data.LotteryRowData
import com.example.data.LotteryType
import java.text.SimpleDateFormat
import java.util.*

abstract class Parser {
    companion object {
        private const val DATE_FORMATTER = "yyyy/MM/dd"
    }

    private var currentPage: Int = 1

    internal val dateFormat = SimpleDateFormat(DATE_FORMATTER, Locale.getDefault())

    fun parse(): Result<LotteryData> {
        val lotteryDataList = mutableListOf<LotteryRowData>()

        try {
            do {
                android.util.Log.v("QQQQ", "lotteryDataList: ${lotteryDataList.size}")
                lotteryDataList.addAll(parseInternal(getUrl()))
                ++currentPage
            } while (lotteryDataList.isNotEmpty() && lotteryDataList.last().date != getLastDataDate())
        } catch (exception: Throwable) {
            return Result.failure(exception)
        }

        return Result.success(
            LotteryData(
                dataList = lotteryDataList,
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