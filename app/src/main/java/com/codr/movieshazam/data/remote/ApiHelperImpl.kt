package com.codr.movieshazam.data.remote

import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(
    private val movieShazamApi: MovieShazamApi
) : ApiHelper {

    override suspend fun uploadFile(file: File): String {
        return try {
            // Prepare file as multipart
            val requestBody = file.asRequestBody("audio/mpeg".toMediaTypeOrNull())
            val multipartFile = MultipartBody.Part.createFormData("file", file.name, requestBody)

            // Make the API call
            val response = movieShazamApi.getMovieName(multipartFile)
            Log.d("API LOG", "API SUCCESS")
            response.text
        } catch (e: Exception) {
            Log.d("API LOG", "API FAILED")
            "Error: ${e.message}"
        }
    }
}