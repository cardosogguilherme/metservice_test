package com.cardosogui.citybikesexplorer.di

import com.cardosogui.citybikesexplorer.favorites.FavoritesRepository
import com.cardosogui.citybikesexplorer.favorites.InMemoryFavoritesRepository
import com.cardosogui.citybikesexplorer.ride.RideRepository
import com.cardosogui.citybikesexplorer.ride.RideRepositoryImpl
import com.cardosogui.citybikesexplorer.stations.StationRepository
import com.cardosogui.citybikesexplorer.stations.StationRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindStationRepository(impl: StationRepositoryImpl): StationRepository

    @Binds
    abstract fun bindRideRepository(impl: RideRepositoryImpl): RideRepository

    @Binds
    abstract fun bindFavoritesRepository(impl: InMemoryFavoritesRepository): FavoritesRepository
}
