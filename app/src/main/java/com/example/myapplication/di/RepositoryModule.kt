package com.example.myapplication.di

import com.example.myapplication.remote.MovieRepository
import com.example.myapplication.remote.MovieService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    /**
     * Provides RemoteDataRepository for access api service method
     */
    @Singleton
    @Provides
    fun provideMovieRepository(
        apiService: MovieService,
    ): MovieRepository {
        return MovieRepository(
            apiService
        )
    }

}