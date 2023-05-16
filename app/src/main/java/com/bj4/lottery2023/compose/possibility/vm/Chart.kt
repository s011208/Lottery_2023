package com.bj4.lottery2023.compose.possibility.vm

import com.bj4.lottery2023.compose.general.Row

sealed class Chart {

    sealed class TableChart(
        open val indexRow: Row? = null,
        open val countRow: Row? = null,
    ) : Chart() {
        fun isValid() =
            !indexRow?.dataList.isNullOrEmpty() && !countRow?.dataList.isNullOrEmpty()

        data class PossibilityList(
            override val indexRow: Row? = null, override val countRow: Row? = null
        ) : TableChart(indexRow, countRow)

        data class PossibilityListOrderByDescent(
            override val indexRow: Row? = null, override val countRow: Row? = null
        ) : TableChart(indexRow, countRow)

        data class PossibilityListOrderByAscent(
            override val indexRow: Row? = null, override val countRow: Row? = null
        ) : TableChart(indexRow, countRow)

        data class NoShowUntilToday(
            override val indexRow: Row? = null, override val countRow: Row? = null
        ) : TableChart(indexRow, countRow)

        data class NoShowUntilTodayOrderByDescent(
            override val indexRow: Row? = null, override val countRow: Row? = null
        ) : TableChart(indexRow, countRow)

        data class NoShowUntilTodayOrderByAscent(
            override val indexRow: Row? = null, override val countRow: Row? = null
        ) : TableChart(indexRow, countRow)
    }
}