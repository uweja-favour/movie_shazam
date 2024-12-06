package com.codr.movieshazam.data.remote

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface MovieShazamApi {

    @Multipart
    @POST("https://movie-shazam.onrender.com/upload")
    suspend fun getMovieName(@Part file: MultipartBody.Part): ApiResponse
}