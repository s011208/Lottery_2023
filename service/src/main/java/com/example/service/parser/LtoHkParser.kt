package com.example.service.parser

import com.example.data.LotteryData
import com.example.data.LotteryRowData
import com.example.data.LotteryType
import org.jsoup.Jsoup

class LtoHkParser(cacheLotteryData: LotteryData?) : Parser(cacheLotteryData)  {
    companion object {
        private const val URL = "https://www.pilio.idv.tw/ltohk/list.asp?indexpage="

        private const val COLUMN_COUNT = 3

        private const val LAST_DATA_DATE = 1025740800000
    }

    override fun getBaseUrl(): String = URL

    override fun parseInternal(url: String): List<LotteryRowData> {
        val doc = Jsoup.connect(url).get()
        val elementTable = doc.select("table.auto-style1")
        val tds = elementTable.select("td")

        val rtn = ArrayList<LotteryRowData>()

        var date: Long = 0
        var numberList: List<Int> = ArrayList()
        var specialNumberList: List<Int>
        for (i in COLUMN_COUNT until tds.size) {
            val value = tds[i].text()
            when {
                i % COLUMN_COUNT == 0 -> date = dateConverter(value)
                i % COLUMN_COUNT == 1 -> numberList = numberConverter(value)
                i % COLUMN_COUNT == 2 -> {
                    specialNumberList = specialNumberConverter(value)
                    rtn.add(LotteryRowData(date, numberList, specialNumberList))
                }
            }
        }

        return rtn
    }

    override fun getLastDataDate(): Long = LAST_DATA_DATE

    override fun getType(): LotteryType = LotteryType.LtoHK

    override fun getNormalCount(): Int = 49

    override fun getSpecialCount(): Int = 1

    private fun dateConverter(date: String): Long {
        return dateFormat.parse(date.substring(0, date.length - 3).trim())?.time ?: 0L
    }

    private fun numberConverter(numbers: String): List<Int> {
        return numbers.split(",").map { it.trim().toInt() }.toList()
    }

    private fun specialNumberConverter(number: String): List<Int> {
        return ArrayList<Int>().apply { add(number.toInt()) }
    }

    override fun isSpecialNumberSeparate(): Boolean {
        return false
    }
}