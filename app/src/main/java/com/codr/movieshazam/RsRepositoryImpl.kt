package com.codr.movieshazam

import android.content.Context
import android.util.Log
import com.codr.movieshazam.data.Recording
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

const val RS_PREF_KEY = "rs_key"
const val RS_PREF_NAME = "rs_pref_name"
class RsRepositoryImpl(
    private val context: Context
) : RsRepository {
    private val gson = Gson()
    override suspend fun saveRecordings(listOfRecording: List<Recording>) {
        withContext(Dispatchers.IO) {
            val data = gson.toJson(listOfRecording)
            val prefs = context.applicationContext.getSharedPreferences(RS_PREF_NAME, Context.MODE_PRIVATE)
            val success = prefs.edit().putString(RS_PREF_KEY, data).commit()
            Log.d("DATA LOG", "Save success in RsRepositoryImpl: $success, value: $data")
        }
    }

    override suspend fun retrieveRecordings(): List<Recording> {
        val prefs = context.applicationContext.getSharedPreferences(RS_PREF_KEY, Context.MODE_PRIVATE)
        val data = prefs.getString(RS_PREF_KEY, null)
        return try {
            val jsonString = data ?: gson.toJson(emptyList<Recording>())
            val dataToBeReturned = gson.fromJson(jsonString, Array<Recording>::class.java).toList()
            dataToBeReturned
        } catch (e: Exception) {
            emptyList()
        }
    }
}