package com.ub.islamicapp.data.datasource

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ub.islamicapp.data.models.LocationEntity
import com.ub.islamicapp.data.models.RecentCityEntity

@Database(entities = [LocationEntity::class, RecentCityEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
    abstract fun recentCityDao(): RecentCityDao
}
