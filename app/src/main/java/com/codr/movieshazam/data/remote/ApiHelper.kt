package com.codr.movieshazam.data.remote

import java.io.File

interface ApiHelper {
    suspend fun uploadFile(file: File): String
}