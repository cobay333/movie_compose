package com.example.myapplication.model

sealed interface  MovieUiState {
    data class Success(val movies: ResponseModel<MovieModel>) : MovieUiState
    data class Error(val message: String) : MovieUiState
    object Loading : MovieUiState
}