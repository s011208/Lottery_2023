package com.example.service.usecase

import com.example.data.LotteryType
import com.example.service.cache.DisplayOrder
import com.example.service.cache.Preferences
import com.example.service.cache.SortType

class SettingsUseCase(private val preferences: Preferences) {

    fun getLotteryType(): LotteryType = preferences.getLotteryType()

    fun setLotteryType(type: LotteryType) = preferences.setLotteryType(type)

    fun getSortType(): SortType = preferences.getSortType()

    fun setSortType(type: SortType) = preferences.setSortType(type)

    fun getDisplayOrder(): DisplayOrder = preferences.getDisplayOrder()

    fun setDisplayOrder(order: DisplayOrder) = preferences.setDisplayOrder(order)
}

