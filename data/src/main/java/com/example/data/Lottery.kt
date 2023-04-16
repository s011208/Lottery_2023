package com.example.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


data class LotteryRowData(
    val date: Long,
    val normalNumberList: List<Int>,
    val specialNumberList: List<Int>
)

@Entity
data class LotteryData(
    @PrimaryKey val type: LotteryType,
    @ColumnInfo val dataList: List<LotteryRowData> = listOf(),
    @ColumnInfo val normalNumberCount: Int,
    @ColumnInfo val specialNumberCount: Int,
    @ColumnInfo val isSpecialNumberSeparate: Boolean,
)

enum class LotteryType {
    Lto, LtoBig, LtoHK, LtoList3, LtoList4
}

class LotteryDataConverter {
    private val gson = Gson()

    @TypeConverter
    fun lotteryRowDataListToString(list: List<LotteryRowData>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun stringToLotteryRowDataList(stringList: String): List<LotteryRowData> {
        return getList(stringList, LotteryRowData::class.java)
    }

    @TypeConverter
    fun listIntToString(list: List<Int>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun stringToListInt(stringList: String): List<Int> {
        return getList(stringList, Int::class.java)
    }

    private fun <T> getList(jsonArray: String?, clazz: Class<T>?): List<T> {
        val typeOfT: Type = TypeToken.getParameterized(MutableList::class.java, clazz).type
        return gson.fromJson(jsonArray, typeOfT)
    }
}