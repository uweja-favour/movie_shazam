package com.codr.movieshazam.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse(
    val text: String
)