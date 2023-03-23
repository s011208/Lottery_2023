package com.example.myapplication.vm

import com.example.data.LotteryData
import com.example.service.SortType
import java.text.SimpleDateFormat
import java.util.*

object LotteryDataMapper {

    fun map(lotteryData: LotteryData, sortType: SortType): List<Row> {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val rtn = mutableListOf<Row>()

        when (sortType) {
            SortType.Normal -> {
                val headerRowList = mutableListOf<Grid>()

                val normalNumberCount = lotteryData.normalNumberCount
                val specialNumberCount = lotteryData.specialNumberCount

                // header
                // TODO resource
                headerRowList.add(
                    Grid(
                        text = "0000/00/00",
                        type = Grid.Type.Date,
                        visible = false
                    )
                )
                for (number in 1..normalNumberCount) {
                    headerRowList.add(
                        Grid(
                            index = number,
                            text = number.toStringNumberWithLeadingZero(),
                            type = Grid.Type.ColumnTitle
                        )
                    )
                }
                for (number in 1..specialNumberCount) {
                    headerRowList.add(
                        Grid(
                            index = number,
                            text = number.toStringNumberWithLeadingZero(),
                            type = Grid.Type.SpecialColumnTitle
                        )
                    )
                }

                rtn.add(Row(headerRowList))

                // data
                lotteryData.dataList.forEach { lotteryRowData ->
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
                                text = number.toStringNumberWithLeadingZero(),
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
                                    text = number.toStringNumberWithLeadingZero(),
                                    visible = number in lotteryRowData.specialNumberList,
                                    type = Grid.Type.Special
                                )
                            )
                        }
                    }

                    rtn.add(Row(dailyLotteryRowList))
                }
            }
            SortType.AddToTen -> {}
            SortType.LastDigit -> {}
        }

        return rtn
    }

    private fun Int.toStringNumberWithLeadingZero(): String {
        return if (this < 10) return "0" + toString() else toString()
    }
}