package com.codr.movieshazam

import com.codr.movieshazam.data.Recording

// RsRepository -> Recording Screen Repository
interface RsRepository {
    suspend fun saveRecordings(listOfRecording: List<Recording>)
    suspend fun retrieveRecordings(): List<Recording>
}