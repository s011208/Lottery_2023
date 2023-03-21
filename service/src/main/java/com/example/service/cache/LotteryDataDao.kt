package com.example.service.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.LotteryData

@Dao
interface LotteryDataDao {
    @Query("SELECT * FROM lotterydata")
    fun getAll(): List<LotteryData>

    @Query("SELECT * FROM lotterydata WHERE type=:id")
    fun getLottery(id: String): LotteryData?

    @Query("DELETE FROM lotterydata")
    fun delete()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg users: LotteryData)
}