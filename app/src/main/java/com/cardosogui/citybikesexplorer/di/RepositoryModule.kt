package com.cardosogui.citybikesexplorer.di

import com.cardosogui.citybikesexplorer.data.repository.StationRepository
import com.cardosogui.citybikesexplorer.data.repository.StationRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindStationRepository(impl: StationRepositoryImpl): StationRepository
}
