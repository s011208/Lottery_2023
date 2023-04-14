package com.example.myapplication.compose.lotterytable.vm

import com.example.data.LotteryData
import com.example.data.LotteryRowData
import com.example.service.cache.SortType
import java.text.SimpleDateFormat
import java.util.*

object LotteryDataMapper {

    private val ADD_TO_TEN_ORDER = listOf(
        1, 10, 29, 38, 47,
        2, 11, 20, 39, 48,
        3, 12, 21, 30, 49,
        4, 13, 22, 31, 40,
        5, 14, 23, 32, 41, 50,
        6, 15, 24, 33, 42, 51,
        7, 16, 25, 34, 43, 52,
        8, 17, 26, 35, 44, 53,
        9, 18, 27, 36, 45,
        19, 28, 37, 46
    )

    private val LAST_DIGIT_ORDER = listOf(
        1, 11, 21, 31, 41, 51,
        2, 12, 22, 32, 42, 52,
        3, 13, 23, 33, 43, 53,
        4, 14, 24, 34, 44, 54,
        5, 15, 25, 35, 45, 55,
        6, 16, 26, 36, 46, 56,
        7, 17, 27, 37, 47, 57,
        8, 18, 28, 38, 48, 58,
        9, 19, 29, 39, 49, 59,
        10, 20, 30, 40, 50, 60
    )

    fun map(lotteryData: LotteryData, sortType: SortType): List<Row> {
        val rtn = when (sortType) {
            SortType.NormalOrder -> {
                makeLotteryData(lotteryData)
            }
            SortType.AddToTen -> {
                val makeLotteryData = makeLotteryData(lotteryData)
                val comparator = GridComparator(ADD_TO_TEN_ORDER, lotteryData.isSpecialNumberSeparate)
                makeLotteryData.map { row -> row.copy(dataList = row.dataList.sortedWith(comparator)) }
            }
            SortType.LastDigit -> {
                val makeLotteryData = makeLotteryData(lotteryData)
                val comparator = GridComparator(LAST_DIGIT_ORDER, lotteryData.isSpecialNumberSeparate)
                makeLotteryData.map { row -> row.copy(dataList = row.dataList.sortedWith(comparator)) }
            }
        }

        return rtn
    }

    private fun makeLotteryData(lotteryData: LotteryData): MutableList<Row> {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val monthlyTotalDateFormat = SimpleDateFormat("MM", Locale.getDefault())
        val rtn = mutableListOf<Row>()

        val headerRowList = mutableListOf<Grid>()

        val normalNumberCount = lotteryData.normalNumberCount
        val specialNumberCount = lotteryData.specialNumberCount

        // header
        // TODO resource
        headerRowList.add(
            Grid(
                text = "",
                type = Grid.Type.Date,
                visible = false
            )
        )
        for (number in 1..normalNumberCount) {
            headerRowList.add(
                Grid(
                    index = number,
                    text = number.toString(),
                    type = Grid.Type.Normal
                )
            )
        }

        if (lotteryData.isSpecialNumberSeparate) {
            for (number in 1..specialNumberCount) {
                headerRowList.add(
                    Grid(
                        index = number,
                        text = number.toString(),
                        type = Grid.Type.Special
                    )
                )
            }
        }

        rtn.add(Row(headerRowList, Row.Type.Header))

        var previousMonth =
            if (lotteryData.dataList.isEmpty()) {
                Calendar.JANUARY
            } else {
                lotteryData.dataList.first().date.getMonth()
            }
        val monthlyRowData = mutableListOf<LotteryRowData>()
        var monthlyTotalNormalMap = mutableMapOf<Int, Int>()
        var monthlyTotalSpecialMap = mutableMapOf<Int, Int>()

        // data
        lotteryData.dataList.forEach { lotteryRowData ->
            // check monthly total
            val currentMonth = lotteryRowData.date.getMonth()
            if (previousMonth == lotteryRowData.date.getMonth()) {
                monthlyRowData.add(lotteryRowData)
            } else {
                previousMonth = currentMonth
                if (monthlyRowData.isNotEmpty()) {
                    rtn.add(
                        makeMonthlyTotalRow(
                            monthlyRowData,
                            monthlyTotalNormalMap,
                            monthlyTotalSpecialMap,
                            lotteryData,
                            monthlyTotalDateFormat,
                            normalNumberCount,
                            specialNumberCount
                        )
                    )

                    monthlyTotalNormalMap.clear()
                    monthlyTotalSpecialMap.clear()
                    monthlyRowData.clear()
                    monthlyRowData.add(lotteryRowData)
                }
            }


            // lottery data list
            val dailyLotteryRowList = mutableListOf<Grid>()
            dailyLotteryRowList.add(
                Grid(
                    text = dateFormat.format(lotteryRowData.date),
                    type = Grid.Type.Date
                )
            )

            for (number in 1..normalNumberCount) {
                val isNormalNumber = number in lotteryRowData.normalNumberList
                val isSpecialNumber =
                    number in lotteryRowData.specialNumberList && !lotteryData.isSpecialNumberSeparate
                dailyLotteryRowList.add(
                    Grid(
                        index = number,
                        text = number.toString(),
                        visible = isNormalNumber || isSpecialNumber,
                        type = if (isSpecialNumber) Grid.Type.Special else Grid.Type.Normal
                    )
                )
            }

            if (lotteryData.isSpecialNumberSeparate) {
                for (number in 1..specialNumberCount) {
                    dailyLotteryRowList.add(
                        Grid(
                            index = number,
                            text = number.toString(),
                            visible = number in lotteryRowData.specialNumberList,
                            type = Grid.Type.Special
                        )
                    )
                }
            }

            rtn.add(Row(dailyLotteryRowList, Row.Type.LotteryData))
        }

        if (monthlyRowData.isNotEmpty()) {
            rtn.add(
                makeMonthlyTotalRow(
                    monthlyRowData,
                    monthlyTotalNormalMap,
                    monthlyTotalSpecialMap,
                    lotteryData,
                    monthlyTotalDateFormat,
                    normalNumberCount,
                    specialNumberCount
                )
            )
        }

        return rtn
    }

    private fun Long.getMonth(): Int {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = this
        return calendar.get(Calendar.MONTH)
    }

    private fun makeMonthlyTotalRow(
        monthlyRowData: MutableList<LotteryRowData>,
        monthlyTotalNormalMap: MutableMap<Int, Int>,
        monthlyTotalSpecialMap: MutableMap<Int, Int>,
        lotteryData: LotteryData,
        monthlyTotalDateFormat: SimpleDateFormat,
        normalNumberCount: Int,
        specialNumberCount: Int,
    ): Row {
        monthlyRowData.flatMap { it.normalNumberList }.forEach { normalNumber ->
            monthlyTotalNormalMap.put(
                normalNumber,
                monthlyTotalNormalMap.getOrDefault(normalNumber, 0) + 1
            )
        }
        monthlyRowData.flatMap { it.specialNumberList }.forEach { specialNumber ->
            if (lotteryData.isSpecialNumberSeparate) {
                monthlyTotalSpecialMap.put(
                    specialNumber,
                    monthlyTotalSpecialMap.getOrDefault(specialNumber, 0) + 1
                )
            } else {
                monthlyTotalNormalMap.put(
                    specialNumber,
                    monthlyTotalNormalMap.getOrDefault(specialNumber, 0) + 1
                )
            }
        }

        val monthlyTotalRowList = mutableListOf<Grid>()

        monthlyTotalRowList.add(
            Grid(
                text = monthlyTotalDateFormat.format(monthlyRowData.first().date),
                type = Grid.Type.Date
            )
        )

        for (number in 1..normalNumberCount) {
            monthlyTotalRowList.add(
                Grid(
                    index = number,
                    text = monthlyTotalNormalMap.getOrDefault(number, 0).toString(),
                    visible = true,
                    type = Grid.Type.Normal
                )
            )
        }

        if (lotteryData.isSpecialNumberSeparate) {
            for (number in 1..specialNumberCount) {
                monthlyTotalRowList.add(
                    Grid(
                        index = number,
                        text = monthlyTotalSpecialMap.getOrDefault(number, 0).toString(),
                        visible = true,
                        type = Grid.Type.Special
                    )
                )
            }
        }

        return Row(monthlyTotalRowList, Row.Type.MonthlyTotal)
    }
}

private class GridComparator(
    private val orderList: List<Int>,
    private val isSpecialNumberSeparate: Boolean,
) : Comparator<Grid> {
    override fun compare(p0: Grid?, p1: Grid?): Int {
        if (p0 == p1) return 0
        if (p0 == null) return 1
        if (p1 == null) return -1
        if (p0.type == p1.type ||
            (!isSpecialNumberSeparate && p0.type != Grid.Type.Date && p1.type != Grid.Type.Date)
        ) {
            return if (p0.type == Grid.Type.Normal || p0.type == Grid.Type.Special) {
                orderList.indexOf(p0.index).compareTo(orderList.indexOf(p1.index))
            } else {
                0
            }
        }
        if (p0.type == Grid.Type.Date) return -1
        if (p1.type == Grid.Type.Date) return 1
        if (p0.type == Grid.Type.Normal) return -1
        if (p1.type == Grid.Type.Normal) return 1
        if (p0.type == Grid.Type.Special) return 1
        if (p1.type == Grid.Type.Special) return -1

        return 0
    }
}