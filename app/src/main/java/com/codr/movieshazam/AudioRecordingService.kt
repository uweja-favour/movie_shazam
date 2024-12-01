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
import com.codr.movieshazam.record.AndroidAudioRecorder
import com.codr.movieshazam.ui.presentation.recording.MSHelperObject
import com.codr.movieshazam.ui.presentation.recording.MSHelperObject.listOfRecordings
import com.codr.movieshazam.ui.util.Constants.NOTIFICATION_CHANNEL_ID
import com.codr.movieshazam.ui.util.getCurrentDate
import com.codr.movieshazam.ui.util.getCurrentMillis
import com.codr.movieshazam.ui.util.getFormattedPeriod
import com.codr.movieshazam.ui.util.getFormattedTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class AudioRecordingService : Service() {

    private var outputFile: File? = null
    private var isRecording = false // Track if recording is in progress
    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())
    private val recorder by lazy { AndroidAudioRecorder(this) }
    private val repository = RsDataSourceImpl(this)
    private lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val startRecording = intent?.getBooleanExtra("START_RECORDING", false) ?: false
        val saveRecording = intent?.getBooleanExtra("SAVE_RECORDING", false) ?: false

        val notification = createNotification()
        try {
            startForeground(2, notification)
            Log.d("THE LOG", "startForeground called, service in foreground.")
        } catch (e: Exception) {
            Log.e("THE LOG", "Error calling startForeground: ${e.message}")
        }
        Log.d("THE LOG", "Service is in the foreground now.")

        if (startRecording && !isRecording) {
            startRecordingWithTimer(10000)
            notificationManager.notify(NOTIFICATION_ID, notification)
        }

        // If we need to save the recording when the app comes to the foreground
        if (saveRecording) {
            stopRecording() // Trigger saving the recording
            return START_NOT_STICKY
        }

        Log.d("THE LOG", "SERVICE SURELY STARTED")
        return START_NOT_STICKY
    }

    private fun startRecordingWithTimer(durationMillis: Long) {
        try {
            val file = File(cacheDir, "$RECORDING ${getCurrentMillis()}.m4a")
            recorder.start(file)
            outputFile = file
            isRecording = true
            MSHelperObject.isRecording.value = true
            Log.d("THE LOG", "Recording started!")

            // Automatically stop recording after the specified duration
            serviceScope.launch {
                delay(durationMillis)
                if (isRecording) {
                    stopRecording()
                }
            }
        } catch (e: Exception) {
            Log.e("Recording", "Error starting recording: ${e.message}", e)
        }
    }

    private fun stopRecording() {
        try {
            recorder.stop()
            Log.d("THE LOG", "Recording stopped. File: $outputFile")

            outputFile?.let {
                val newRecording = Recording(
                    fileName = it.name,
                    filePath = it.absolutePath,
                    dateAdded = "${getCurrentDate()} ${getFormattedTime()} ${getFormattedPeriod()}"
                )
                listOfRecordings.value += newRecording
                saveRecording()
            }

            isRecording = false

            // Clear all existing notifications
            notificationManager.cancelAll()

            // Display the final notification
            updateNotification(
                contentTitle = "Recording Saved",
                contentText = "Your audio recording has been saved successfully."
            )
            MSHelperObject.isRecording.value = false
        } catch (e: Exception) {
            Log.e("Recording", "Error stopping recording: ${e.message}", e)
        }
    }

    private fun updateNotification(contentTitle: String, contentText: String) {
        val notificationChannelId = NOTIFICATION_CHANNEL_ID
        val activityIntent = Intent(this, MainActivity::class.java)
        val activityPendingIntent = PendingIntent.getActivity(
            this,
            6,
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Clear all existing notifications
        notificationManager.cancelAll()

        val updatedNotification = NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(false) // Notification is no longer ongoing
            .setContentIntent(activityPendingIntent) // Optional: allow navigation back to the app
            .build()

        // Issue the new notification with a specific ID
        notificationManager.notify(NOTIFICATION_ID, updatedNotification)
    }



    private fun saveRecording() = serviceScope.launch(Dispatchers.IO) {
        repository.saveRecordings(listOfRecordings.value)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("THE LOG", "Service stopped")
        serviceScope.cancel()
        if (isRecording) stopRecording()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null // Not binding this service
    }

    private fun createNotification(): Notification {
        val notificationChannelId = NOTIFICATION_CHANNEL_ID
        val activityIntent = Intent(this, MainActivity::class.java)
        val activityPendingIntent = PendingIntent.getActivity(
            this,
            4,
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                notificationChannelId,
                "Audio Recording",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle("Recording Audio, please wait.")
            .setContentText("Listening...")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOngoing(true)
            .setContentIntent(activityPendingIntent) // Optional: allow navigation back to the app
            .build()
    }
//
//    private fun createFullScreenNotification(): Notification {
//        val notificationChannelId = "AudioRecordingChannel"
//        val activityIntent = Intent(this, MainActivity::class.java)
//        val fullScreenPendingIntent = PendingIntent.getActivity(
//            this,
//            5,
//            activityIntent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                notificationChannelId,
//                "Audio Recording Alerts",
//                NotificationManager.IMPORTANCE_HIGH // High importance for full-screen notifications
//            )
//            channel.description = "Notifications for ongoing or urgent audio recording events."
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        return NotificationCompat.Builder(this, notificationChannelId)
//            .setContentTitle("Recording Audio")
//            .setContentText("Your audio recording is in progress.")
//            .setSmallIcon(R.drawable.ic_launcher_background)
//            .setPriority(NotificationCompat.PRIORITY_HIGH) // Ensure high priority
//            .setCategory(NotificationCompat.CATEGORY_ALARM) // Categorize as an alarm
//            .setOngoing(true)
//            .setFullScreenIntent(fullScreenPendingIntent, true) // Full-screen intent
//            .build()
//    }

    companion object Constants {
        const val NOTIFICATION_ID = 100
        const val RECORDING = "Recording"
    }
}

//
//private fun updateNotification(contentText: String) {
//    val notificationChannelId = "AudioRecordingChannel"
//    val activityIntent = Intent(this, MainActivity::class.java)
//    val activityPendingIntent = PendingIntent.getActivity(
//        this,
//        4,
//        activityIntent,
//        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//    )
//
//    val updatedNotification = NotificationCompat.Builder(this, notificationChannelId)
//        .setContentTitle("Recording Audio")
//        .setContentText(contentText)
//        .setSmallIcon(R.drawable.ic_launcher_background)
//        .setOngoing(true)
//        .addAction(
//            R.drawable.ic_launcher_background,
//            "Reset",
//            activityPendingIntent
//        )
//        .build()
//
//    notificationManager.notify(NOTIFICATION_ID, updatedNotification) // Use the same notification ID
//}
