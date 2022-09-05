package com.posco.posco_store.di

import com.example.giahn.giahnxois
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
class LogModule {

    @Provides
    fun provideAccessCheck(): giahnxois {
        return giahnxois()
    }

}