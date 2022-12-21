package dev.ky3he4ik.composeweather.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Query("SELECT loc FROM weather_database")
    fun getLocationsFlow(): Flow<List<String>>

    @Query("SELECT * FROM weather_database")
    fun getAllWeatherEntities(): List<WeatherEntity>

    @Query("SELECT * FROM weather_database WHERE id = :id")
    fun getWeatherById(id: Long): Flow<WeatherEntity>

    @Query("SELECT * FROM weather_database WHERE loc = :zipcode")
    fun getWeatherByName(zipcode: String): Flow<WeatherEntity>

    @Query("SELECT * FROM weather_database WHERE loc = :location")
    fun getWeatherByLocation(location: String): Flow<WeatherEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weatherEntity: WeatherEntity)

    @Update
    suspend fun update(weatherEntity: WeatherEntity)

    @Query("DELETE FROM weather_database WHERE loc = :loc")
    suspend fun delete(loc: String)

    @Query("SELECT * FROM weather_database ORDER BY ID DESC LIMIT 1")
    fun selectLastEntry(): WeatherEntity

    @Query("SELECT (SELECT COUNT(*) FROM weather_database) == 0")
    fun isEmpty(): Boolean
}