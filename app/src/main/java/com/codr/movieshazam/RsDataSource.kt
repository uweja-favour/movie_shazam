package com.codr.movieshazam

import com.codr.movieshazam.data.Recording

// RsRepository -> Recording Screen Repository
interface RsDataSource {
    suspend fun saveRecordings(listOfRecording: List<Recording>)
    suspend fun retrieveRecordings(): List<Recording>
}