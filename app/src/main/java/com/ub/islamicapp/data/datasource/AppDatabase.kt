package com.ub.islamicapp.data.datasource

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ub.islamicapp.data.models.LocationEntity

@Database(entities = [LocationEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
}
