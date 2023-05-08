package com.example.service.cache.log

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.LotteryLog

@Database(entities = [LotteryLog::class], version = 5, exportSchema = false)
abstract class LotteryLogDatabase : RoomDatabase() {
    abstract fun userDao(): LotteryLogDao
}