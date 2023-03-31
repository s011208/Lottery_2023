package com.example.service.usecase

import com.example.data.LotteryType
import com.example.service.cache.*

class SettingsUseCase(private val preferences: Preferences) {

    fun getLotteryType(): LotteryType = preferences.getLotteryType()

    fun setLotteryType(type: LotteryType) = preferences.setLotteryType(type)

    fun getFontSize(): FontSize = preferences.getFontSize()

    fun setFontSize(size: FontSize) = preferences.setFontSize(size)

    fun getSortType(): SortType = preferences.getSortType()

    fun setSortType(type: SortType) = preferences.setSortType(type)

    fun getDisplayOrder(): DisplayOrder = preferences.getDisplayOrder()

    fun setDisplayOrder(order: DisplayOrder) = preferences.setDisplayOrder(order)

    fun setDayNightSettings(settings: DayNightMode) = preferences.setDayNight(settings)

    fun getDayNight(): DayNightMode = preferences.getDayNight()
}

