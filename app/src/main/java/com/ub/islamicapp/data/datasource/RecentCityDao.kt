package com.ub.islamicapp.data.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ub.islamicapp.data.models.RecentCityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentCityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentCity(city: RecentCityEntity)

    @Query("SELECT * FROM recent_cities ORDER BY timestamp DESC LIMIT 3")
    fun getRecentCities(): Flow<List<RecentCityEntity>>
}
