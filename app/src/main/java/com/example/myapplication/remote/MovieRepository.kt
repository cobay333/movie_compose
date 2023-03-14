package com.example.myapplication.remote

import com.example.myapplication.BuildConfig
import com.example.myapplication.model.MovieUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val apiService: MovieService
) {
    suspend fun movieDetail(search : String, page : Int): Flow<MovieUiState> = flow {
        emit(MovieUiState.Loading)
        try {
            val response = apiService.fetchMovies(BuildConfig.BASE_URL, BuildConfig.API_KEY, search, "movie", page)
            when (response.response) {
                "False" -> {
                    emit(MovieUiState.Error(response.error ?: ""))
                }
                "True" -> {
                    emit(MovieUiState.Success(response))
                }
                else -> {
                    emit(MovieUiState.Error(""))
                }
            }
        } catch (e: Exception) {
            emit(MovieUiState.Error(e.message ?: ""))
        }
    }
}