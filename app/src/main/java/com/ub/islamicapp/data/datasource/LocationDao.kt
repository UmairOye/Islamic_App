package com.ub.islamicapp.data.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ub.islamicapp.data.models.LocationEntity

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: LocationEntity)

    @Query("SELECT * FROM location_cache WHERE id = 1")
    suspend fun getLastLocation(): LocationEntity?
}
