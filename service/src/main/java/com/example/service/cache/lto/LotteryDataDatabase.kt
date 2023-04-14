package com.example.service.cache.lto

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.LotteryData
import com.example.data.LotteryDataConverter

@TypeConverters(LotteryDataConverter::class)
@Database(entities = [LotteryData::class], version = 1, exportSchema = false)
abstract class LotteryDataDatabase : RoomDatabase() {
    abstract fun userDao(): LotteryDataDao
}