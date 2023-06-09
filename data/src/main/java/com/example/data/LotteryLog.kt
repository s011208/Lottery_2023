package com.example.data

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["timeStamp", "type"])
data class LotteryLog(
    val timeStamp: Long,
    val type: LotteryType,
    @ColumnInfo val state: LoadingState,
    @ColumnInfo val errorMessage: String = "",
    @ColumnInfo val taskId: String = "",
    @ColumnInfo val source: String = "",
)

enum class LoadingState {
    ERROR, SUCCESS
}