package com.example.service.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.data.LotteryData

@Dao
interface LotteryDataDao {
    @Query("SELECT * FROM lotterydata")
    fun getAll(): List<LotteryData>

    @Query("DELETE FROM lotterydata")
    fun delete()

    @Insert
    fun insertAll(vararg users: LotteryData)
}