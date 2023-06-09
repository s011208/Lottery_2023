package com.example.service.parser

import com.example.data.LotteryData
import com.example.data.LotteryRowData
import com.example.data.LotteryType
import org.jsoup.Jsoup

class LtoList3Parser(cacheLotteryData: LotteryData?) : Parser(cacheLotteryData) {
    companion object {
        private const val URL = "https://www.pilio.idv.tw/lto/list3.asp?orderby=new&indexpage="

        private const val IGNORE_COLUMN = 2

        private const val LAST_DATA_DATE = 1134950400000
    }

    override fun getBaseUrl(): String = URL

    override fun parseInternal(url: String): List<LotteryRowData> {
        val doc = Jsoup.connect(url).get()
        val elementTable = doc.select("table.auto-style1")
        val tds = elementTable.select("td")

        val rtn = ArrayList<LotteryRowData>()

        var date: Long
        var numberList: List<Int>
        for (i in IGNORE_COLUMN until tds.size step 3) {
            try {
                date = dateConverter(tds[i].text())
                numberList = numberConverter(tds[i + 1].text())
                rtn.add(LotteryRowData(date, numberList, listOf()))
            } catch (throwable: Throwable) {
                analytics.recordException(throwable)
            }
        }

        return rtn
    }

    override fun getLotteryLastDataDate(): Long = LAST_DATA_DATE

    override fun getType(): LotteryType = LotteryType.LtoList3

    override fun getNormalCount(): Int = 3

    override fun getSpecialCount(): Int = 0
    override fun isSpecialNumberSeparate(): Boolean {
        return false
    }

    private fun numberConverter(numbers: String): List<Int> {
        return numbers.split(" ").subList(0, 3).map { it.trim().toInt() }.toList()
    }
}