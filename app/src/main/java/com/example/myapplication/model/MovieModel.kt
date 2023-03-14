package com.example.myapplication.model

import com.google.gson.annotations.SerializedName

data class ResponseModel<T>(@SerializedName("Response") val response : String,
                              @SerializedName("totalResults") val totalResults : String?,
                            @SerializedName("Error") val error : String?,
                             @SerializedName("Search") val data : List<T>?)


data class MovieModel(@SerializedName("Title") val title : String,
                      @SerializedName("Year") val year:String,
                      @SerializedName("imdbID") val imdbID: String,
                      @SerializedName("Type") val type : String,
                      @SerializedName("Poster") val poster: String)
