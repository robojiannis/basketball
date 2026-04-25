package com.robojiannis.basketball.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "matches")
data class Match(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: Long = System.currentTimeMillis(),
    val homeName: String,
    val awayName: String,
    val homeScore: Int,
    val awayScore: Int,
    val points: Int,
    val rebounds: Int,
    val assists: Int,
    val steals: Int,
    val blocks: Int,
    val turnovers: Int,
    val isWin: Boolean,
    val quarters: List<QuarterStats> = emptyList()
)

data class QuarterStats(
    val quarterNumber: Int,
    val isPlaying: Boolean = false,
    val homeScore: Int = 0,
    val awayScore: Int = 0,
    val points: Int = 0,
    val rebounds: Int = 0,
    val assists: Int = 0,
    val steals: Int = 0,
    val blocks: Int = 0,
    val turnovers: Int = 0
)

class Converters {
    @TypeConverter
    fun fromQuarterStatsList(value: List<QuarterStats>?): String? {
        val gson = Gson()
        val type = object : TypeToken<List<QuarterStats>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toQuarterStatsList(value: String?): List<QuarterStats>? {
        val gson = Gson()
        val type = object : TypeToken<List<QuarterStats>>() {}.type
        return gson.fromJson(value, type)
    }
}
