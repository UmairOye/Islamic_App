package com.ub.islamicapp.di

import android.app.Application
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.FusedLocationProviderClient
import androidx.room.Room
import com.ub.islamicapp.data.datasource.AppDatabase
import com.ub.islamicapp.data.datasource.LocationDao
import com.ub.islamicapp.data.datasource.RecentCityDao
import com.ub.islamicapp.data.location.DefaultLocationTracker
import com.ub.islamicapp.data.repository.PrayerRepositoryImpl
import com.ub.islamicapp.domain.location.LocationTracker
import com.ub.islamicapp.domain.repository.PrayerRepository
import dagger.Binds
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
    fun provideAppDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            "islamic_app_db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    @Singleton
    fun provideLocationDao(db: AppDatabase): LocationDao {
        return db.locationDao()
    }

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(app: Application): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(app)
    }

    @Provides
    @Singleton
    fun provideRecentCityDao(db: AppDatabase): RecentCityDao {
        return db.recentCityDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindLocationTracker(
        defaultLocationTracker: DefaultLocationTracker
    ): LocationTracker

    @Binds
    @Singleton
    abstract fun bindPrayerRepository(
        prayerRepositoryImpl: PrayerRepositoryImpl
    ): PrayerRepository
}
