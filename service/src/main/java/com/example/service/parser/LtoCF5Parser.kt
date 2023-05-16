package com.example.service.parser

import com.example.data.LotteryData
import com.example.data.LotteryRowData
import com.example.data.LotteryType
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.Locale

class LtoCF5Parser(cacheLotteryData: LotteryData?) : Parser(cacheLotteryData) {
    companion object {
        private const val URL = "https://www.lotto-8.com/USA/listltoFT5.asp?orderby=new&indexpage="

        private const val COLUMN_COUNT = 2

        private const val LAST_DATA_DATE = 697161600000L

        private const val DATE_FORMATTER = "MM/dd/yyyy"
    }

    private val dateFormat = SimpleDateFormat(DATE_FORMATTER, Locale.getDefault())

    override fun getBaseUrl(): String = URL

    override fun parseInternal(url: String): List<LotteryRowData> {
        val doc = Jsoup.connect(url).get()
        val elementTable = doc.select("table.auto-style1")
        val tds = elementTable.select("td")

        val rtn = ArrayList<LotteryRowData>()

        var date: Long = 0
        var numberList: List<Int>
        for (i in COLUMN_COUNT until tds.size) {
            val value = tds[i].text()
            try {
                when {
                    i % COLUMN_COUNT == 0 -> date = dateConverter(value)
                    i % COLUMN_COUNT == 1 -> {
                        numberList = numberConverter(value)
                        rtn.add(LotteryRowData(date, numberList, listOf()))
//                        Timber.e("date: $date, numberList: $numberList")
                    }
                }
            } catch (throwable: Throwable) {
//                Timber.w(throwable, "error")
                analytics.recordException(throwable)
            }
        }

        return rtn
    }

    override fun getLotteryLastDataDate(): Long = LAST_DATA_DATE

    override fun getType(): LotteryType = LotteryType.LtoCF5

    override fun getNormalCount(): Int = 39

    override fun getSpecialCount(): Int = 0
    override fun isSpecialNumberSeparate(): Boolean {
        return false
    }

    override fun getDateFormat(): SimpleDateFormat {
        return dateFormat
    }

    private fun numberConverter(numbers: String): List<Int> {
        return numbers.split(",").map { it.trim().toInt() }.toList()
    }
}