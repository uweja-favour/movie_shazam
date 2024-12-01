package com.codr.movieshazam

import android.content.Context
import android.util.Log
import com.codr.movieshazam.data.Recording
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

const val RS_PREF_KEY = "rs_key"
const val RS_PREF_NAME = "rs_pref_name"
class RsDataSourceImpl(
    private val context: Context
) : RsDataSource {

    private val gson = Gson()
    override suspend fun saveRecordings(listOfRecording: List<Recording>) {
        withContext(Dispatchers.IO) {
            val data = gson.toJson(listOfRecording)
            val prefs = context.applicationContext.getSharedPreferences(RS_PREF_NAME, Context.MODE_PRIVATE)
            val success = prefs.edit().putString(RS_PREF_KEY, data).commit()
            Log.d("DATA LOG", "The save SUCCESS in RsDataSourceImpl is: $success, value: $data")
        }
    }

    override suspend fun retrieveRecordings(): List<Recording> {
        val prefs = context.applicationContext.getSharedPreferences(RS_PREF_NAME, Context.MODE_PRIVATE)
        val data = prefs.getString(RS_PREF_KEY, null)
        return try {
            val jsonString = data ?: gson.toJson(emptyList<Recording>())
            val listOfRecordings = gson.fromJson(jsonString, Array<Recording>::class.java).toList()
            Log.d("DATA LOG", "SUCCESS retrieved data is $listOfRecordings")
            listOfRecordings
        } catch (e: Exception) {
            Log.d("DATA LOG", "an EXCEPTION occurred while retrieving data from RsDataSourceImpl")
            emptyList()
        }
    }
}