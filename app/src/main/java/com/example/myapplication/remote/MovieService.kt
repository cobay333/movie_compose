package com.example.myapplication.remote

import com.example.myapplication.model.MovieModel
import com.example.myapplication.model.ResponseModel
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface MovieService {

    @GET
    suspend fun fetchMovies (@Url url: String, @Query("apikey") key: String, @Query("s") search: String,
                              @Query("type") type: String, @Query("page") page: Int
    ): ResponseModel<MovieModel>
}