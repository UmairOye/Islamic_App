package com.ub.islamicapp.di

import android.app.Application
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.FusedLocationProviderClient
import com.ub.islamicapp.data.location.DefaultLocationTracker
import com.ub.islamicapp.data.repository.PrayerRepositoryImpl
import com.ub.islamicapp.domain.location.LocationTracker
import com.ub.islamicapp.domain.repository.PrayerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(app: Application): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(app)
    }

    @Provides
    @Singleton
    fun provideLocationTracker(
        fusedLocationProviderClient: FusedLocationProviderClient,
        application: Application
    ): LocationTracker {
        return DefaultLocationTracker(fusedLocationProviderClient, application)
    }

    @Provides
    @Singleton
    fun providePrayerRepository(): PrayerRepository {
        return PrayerRepositoryImpl()
    }
}
