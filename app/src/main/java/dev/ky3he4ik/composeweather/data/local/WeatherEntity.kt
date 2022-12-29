package dev.ky3he4ik.composeweather.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_database")
data class WeatherEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cityName: String,
    val loc: String,
)
