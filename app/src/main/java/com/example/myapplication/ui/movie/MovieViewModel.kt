package com.example.myapplication.ui.movie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.MovieModel
import com.example.myapplication.model.MovieUiState
import com.example.myapplication.remote.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(private val repo: MovieRepository) : ViewModel(){
    private val _movieState = MutableStateFlow(emptyList<MovieModel>())
    private val _loadingState = MutableStateFlow(false)
    private val _firstPageState = MutableStateFlow(false)
    private val _errorMessageState = MutableStateFlow("")

    val firstPageStateFlow: StateFlow<Boolean> = _firstPageState.asStateFlow()
    val loadingStateFlow: StateFlow<Boolean> = _loadingState.asStateFlow()
    val movieStateFlow: StateFlow<List<MovieModel>> = _movieState.asStateFlow()
    val errorMessageStateFlow: StateFlow<String> = _errorMessageState.asStateFlow()
    private var totalMovies = 0
    private var page = 1
    private var search = "Marvel"
    init {
        loadMovie()
    }

    fun loadMore(){
        if (!loadingStateFlow.value && !_firstPageState.value
            && movieStateFlow.value.size < totalMovies) {
            loadMovie()
        }
    }

    fun searchFilm(text : String) {
        search = text
        _movieState.value = listOf()
        page = 1
        loadMovie()
    }

    private fun loadMovie() {
        viewModelScope.launch {
            repo.movieDetail(search, page).collectLatest {
                when (it) {
                    is MovieUiState.Loading -> {
                        if (page == 1) {
                            _firstPageState.value = true
                        } else {
                            _loadingState.value = true
                        }
                    }
                    is MovieUiState.Error -> {
                        _errorMessageState.value = it.message
                        _firstPageState.value = false
                        _loadingState.value = false
                    }
                    is MovieUiState.Success -> {
                        val temp = _movieState.value.toMutableList()
                        temp.addAll(it.movies.data?: listOf())
                        _movieState.value = temp
                        page ++
                        totalMovies = it.movies.totalResults?.toInt()?: 0
                        _firstPageState.value = false
                        _loadingState.value = false
                        _errorMessageState.value = ""
                    }
                }
            }}
    }

}