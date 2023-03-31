package com.example.service.cache

import android.content.Context
import com.example.data.LotteryType

class Preferences(context: Context) {

    companion object {
        private const val PREFERENCE_NAME = "my_app_pref"

        private const val KEY_LOTTERY_TYPE = "lto_type"

        private const val KEY_FONT_SIZE = "font_size"

        private const val KEY_SORT_TYPE = "sort_type"

        private const val KEY_DISPLAY_ORDER = "display_order"

        private const val KEY_DAY_NIGHT = "day_night"
    }

    private val sharedPreferences =
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun getLotteryType(): LotteryType = LotteryType.valueOf(
        sharedPreferences.getString(KEY_LOTTERY_TYPE, LotteryType.Lto.name) ?: LotteryType.Lto.name
    )

    fun setLotteryType(type: LotteryType) =
        sharedPreferences.edit().putString(KEY_LOTTERY_TYPE, type.name).apply()

    fun getFontSize(): FontSize = FontSize.valueOf(
        sharedPreferences.getString(KEY_FONT_SIZE, FontSize.NORMAL.name) ?: FontSize.NORMAL.name
    )

    fun setFontSize(fontSize: FontSize) =
        sharedPreferences.edit().putString(KEY_FONT_SIZE, fontSize.name).apply()

    fun getSortType(): SortType = SortType.valueOf(
        sharedPreferences.getString(KEY_SORT_TYPE, SortType.NormalOrder.name)
            ?: SortType.NormalOrder.name
    )

    fun setSortType(sortType: SortType) =
        sharedPreferences.edit().putString(KEY_SORT_TYPE, sortType.name).apply()

    fun getDisplayOrder(): DisplayOrder = DisplayOrder.valueOf(
        sharedPreferences.getString(KEY_DISPLAY_ORDER, DisplayOrder.DESCEND.name)
            ?: DisplayOrder.DESCEND.name
    )

    fun setDisplayOrder(order: DisplayOrder) =
        sharedPreferences.edit().putString(KEY_DISPLAY_ORDER, order.name).apply()

    fun getDayNight(): DayNightMode = DayNightMode.valueOf(
        sharedPreferences.getString(KEY_DAY_NIGHT, DayNightMode.AUTO.name) ?: DayNightMode.AUTO.name
    )

    fun setDayNight(dayNightMode: DayNightMode) =
        sharedPreferences.edit().putString(KEY_DAY_NIGHT, dayNightMode.name).apply()
}

enum class FontSize {
    EXTRA_SMALL, SMALL, NORMAL, LARGE, EXTRA_LARGE
}

enum class DisplayOrder {
    ASCEND, DESCEND
}

enum class DayNightMode {
    DAY, NIGHT, AUTO
}