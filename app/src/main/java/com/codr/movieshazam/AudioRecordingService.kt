package com.codr.movieshazam

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.codr.movieshazam.data.Recording
import com.codr.movieshazam.data.remote.ApiHelper
import com.codr.movieshazam.record.AndroidAudioRecorder
import com.codr.movieshazam.ui.presentation.recording.MSHelperObject
import com.codr.movieshazam.ui.util.Constants
import com.codr.movieshazam.ui.util.getCurrentDate
import com.codr.movieshazam.ui.util.getCurrentMillis
import com.codr.movieshazam.ui.util.getFormattedPeriod
import com.codr.movieshazam.ui.util.getFormattedTime
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class AudioRecordingService : Service() {

    private var outputFile: File? = null
    private var isRecording = false
    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())
    private val dataSource = RsDataSourceImpl(this)

    @Inject
    lateinit var apiHelper: ApiHelper

    private val recorder by lazy { AndroidAudioRecorder(this) }
    private lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val startRecording = intent?.getBooleanExtra(Constants.START_RECORDING, false) ?: false
        val saveRecording = intent?.getBooleanExtra(Constants.SAVE_RECORDING, false) ?: false

        startForegroundService()

        when {
            saveRecording -> {
                stopRecordingAndFindMovieName()
                return START_NOT_STICKY
            }
            startRecording && !isRecording -> {
                startTimedRecording(durationMillis = 5000)
            }
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        if (isRecording) cancelRecording()
    }

    private fun cancelRecording() {
        recorder.stop()
        isRecording = false
        MSHelperObject.isRecording.value = false
        outputFile = null
        notificationManager.cancelAll()
        notificationManager.deleteNotificationChannel(Constants.CHANNEL_ID)
    }

    private fun startTimedRecording(durationMillis: Long) = serviceScope.launch {
        try {
            val file = File(cacheDir, "Recording_${getCurrentMillis()}.mp3")
            recorder.start(file)
            outputFile = file
            isRecording = true
            MSHelperObject.isRecording.value = true

            // Update notification while recording
            updateRecordingNotification()

            delay(durationMillis)
            if (isRecording) stopRecordingAndFindMovieName()

        } catch (e: Exception) {
            Log.e("Recording", "Error starting recording: ${e.message}", e)
        }
    }

    private fun stopRecordingAndFindMovieName() = serviceScope.launch {
        try {
            recorder.stop()
            outputFile?.let {
                val newRecording = Recording(
                    fileName = it.name,
                    filePath = it.absolutePath,
                    dateAdded = "${getCurrentDate()} ${getFormattedTime()} ${getFormattedPeriod()}"
                )
                MSHelperObject.listOfRecordings.value += newRecording
                saveRecordingToDatabase()
            }

            isRecording = false
            MSHelperObject.isRecording.value = false
            updateRecordingNotification("Recording Saved", "Your audio recording has been saved successfully.")

            outputFile?.let { uploadAudioFile(it) }

        } catch (e: Exception) {
            Log.e("Recording", "Error stopping recording: ${e.message}", e)
        }
    }

    private fun saveRecordingToDatabase() = serviceScope.launch {
        try {
            dataSource.saveRecordings(MSHelperObject.listOfRecordings.value)
        } catch (e: Exception) {
            Log.e("Database", "Error saving recordings: ${e.message}", e)
        }
    }

    private fun uploadAudioFile(file: File) = serviceScope.launch {
        try {
            val movieName = apiHelper.uploadFile(file)
            Log.d("API LOG", "Movie name: $movieName")
        } catch (e: Exception) {
            Log.e("API LOG", "Error uploading file: ${e.message}", e)
        }
    }

    private fun startForegroundService() {
        val notification = createRecordingNotification()
        try {
            startForeground(Constants.NOTIFICATION_ID, notification)
        } catch (e: Exception) {
            Log.e("THE LOG", "Error calling startForeground: ${e.message}")
        }
    }

    private fun updateRecordingNotification(contentTitle: String = "Recording Audio...", contentText: String = "Listening...") {
        val notification = NotificationCompat.Builder(this, Constants.CHANNEL_ID)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .setContentIntent(createMainActivityPendingIntent())
            .build()

        notificationManager.notify(Constants.NOTIFICATION_ID, notification)
    }

    private fun createRecordingNotification(): Notification {
        return NotificationCompat.Builder(this, Constants.CHANNEL_ID)
            .setContentTitle("Recording Audio...")
            .setContentText("Listening...")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .setContentIntent(createMainActivityPendingIntent())
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.CHANNEL_ID,
                "Movie Shazam",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Handles all notifications for Movie Shazam app"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createMainActivityPendingIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java)
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    companion object {
        // Constants moved to a separate file for better organization and maintainability
    }
}
