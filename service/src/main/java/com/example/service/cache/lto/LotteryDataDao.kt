package com.example.service.cache.lto

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

    @Query("DELETE FROM lotterydata WHERE type=:id")
    fun deleteLottery(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg data: LotteryData)
}