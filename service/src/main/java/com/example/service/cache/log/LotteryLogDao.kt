package com.example.service.cache.log

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.LotteryLog

@Dao
interface LotteryLogDao {
    @Query("SELECT * FROM LotteryLog")
    fun getAll(): List<LotteryLog>

    @Query("DELETE FROM LotteryLog")
    fun delete()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg log: LotteryLog)
}