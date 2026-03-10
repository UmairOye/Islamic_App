package com.ub.islamicapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location_cache")
data class LocationEntity(
    @PrimaryKey val id: Int = 1, // Only 1 row needed
    val latitude: Double,
    val longitude: Double
)
