package com.codr.movieshazam

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.codr.movieshazam.ui.presentation.recording.MSHelperObject
import com.codr.movieshazam.ui.presentation.recording.RSViewModel
class AppLifecycleObserver(
    private val context: Context,
    private val viewModel: RSViewModel
) : DefaultLifecycleObserver {

//    private var isServiceRunning = false

    override fun onStop(owner: LifecycleOwner) {
//        // App has moved to the background
//        Log.d("THE LOG", "App is going to the background.")
//
//        val isRecordingOrPlaying = MSHelperObject.isRecording.value || MSHelperObject.isPlaying.value
//        Log.d("THE LOG", "Recording or playing: $isRecordingOrPlaying")
//
//        if (isRecordingOrPlaying && !isServiceRunning) {
//            // Start the foreground service only if it's not already running
//            Log.d("THE LOG", "Starting foreground service because recording or playing is active.")
//            val intent = Intent(context, AudioRecordingService::class.java).apply {
//                putExtra("START_RECORDING", true)
//            }
//            try {
//                context.startForegroundService(intent)
//                isServiceRunning = true // Track service status
//            } catch (e: Exception) {
//                Log.e("THE LOG", "Error starting foreground service: ${e.message}")
//            }
//        }
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
//        isServiceRunning = false // Reset the flag when the app comes to the foreground
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackground() {
//        viewModel.onAppInBackground()  // Call ViewModel method to run code in the background
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForeground() {
        Log.d("THE LOG", "App is in the foreground.")
//        viewModel.onAppForeground()  // Call ViewModel method to run code when the app is in the foreground
    }
}
