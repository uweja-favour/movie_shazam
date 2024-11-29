package com.codr.movieshazam.playback

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.core.net.toUri
import com.codr.movieshazam.AnEvent
import com.codr.movieshazam.EventController
import com.codr.movieshazam.ui.util.Constants.PLAYBACK_COMPLETE
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

class AndroidAudioPlayer(
    private val context: Context
): AudioPlayer {

    private var player: MediaPlayer? = null
    private var isPaused: Boolean = false

    override fun playFile(file: File) {
        try {
            if (isPaused) {
                player?.apply {
                    start()
                    isPaused = false
                    setOnCompletionListener {
                        Log.d("Playback", "Playback completed for file: ${file.absolutePath}")
                        onPlaybackCompleted()
                    }
                }

            } else {
                // Stop and release any existing player instance
                player?.stop()
                player?.reset()
                player?.release()

                // Initialize and start a new MediaPlayer
                player = MediaPlayer().apply {
                    setDataSource(context, file.toUri())
                    prepare()
                    start()

                    setOnCompletionListener {
                        Log.d("Playback", "Playback completed for file: ${file.absolutePath}")
                        onPlaybackCompleted()
                    }
                }
            }

            Log.d("Playback", "Playing file: ${file.absolutePath}")
        } catch (e: Exception) {
            Log.e("Playback", "Error playing file: ${e.message}", e)
        }
    }

    override fun stop() {
        try {
            // Stop and release the player if it exists
            player?.let {
                if (it.isPlaying) {
                    Log.d("Playback", "Playback stopped successfully.")
                    it.stop()
                } else {
                    Log.d("Playback", "Playback didnt stop!.")
                }
                it.reset()
                it.release()
            }
            player = null
            isPaused = false
            Log.d("Playback", "AT THE END.")
        } catch (e: Exception) {
            Log.e("Playback", "Error stopping playback: ${e.message}", e)
        }
    }

    override fun pause() {
        try {
            player?.let {
                if (it.isPlaying) {
                    Log.d("Playback", "Paused successfully.")
                    it.pause()
                    isPaused = true
                } else {
                    Log.d("Playback", "DIDN'T PAUSE!.")
                }
            }
        } catch (e: Exception) {
            Log.e("Playback", "Error PAUSING playback: ${e.message}", e)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun onPlaybackCompleted() {
        isPaused = false
        GlobalScope.launch(Dispatchers.Main) {
            EventController.sendEvent(
                AnEvent(
                    type = PLAYBACK_COMPLETE,
                    snackBarEvent = null
                )
            )
        }
    }
}
