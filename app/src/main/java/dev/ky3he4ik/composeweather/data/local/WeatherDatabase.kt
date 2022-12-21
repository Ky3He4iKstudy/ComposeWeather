package dev.ky3he4ik.composeweather.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room database to persist data for the Weather app.
 * This database stores a [WeatherEntity] entity
 */
// create the database with all necessary annotations, methods, variables, etc.
@Database(entities = [WeatherEntity::class], version = 5, exportSchema = false)
abstract class WeatherDatabase : RoomDatabase() {

    // Abstract function to provide instance of the database access object
    abstract fun getWeatherDao(): WeatherDao

    companion object {
        @Volatile
        private var INSTANCE: WeatherDatabase? = null

        fun getDatabase(context: Context): WeatherDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDatabase::class.java,
                    "weather_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}