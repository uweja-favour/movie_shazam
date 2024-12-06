package com.codr.movieshazam.record

import android.content.Context
import android.media.AudioManager
import android.media.MediaRecorder
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream

class AndroidAudioRecorder(
    @ApplicationContext private val context: Context // if an issue consider using private val context: Context
): AudioRecorder {

    private var recorder: MediaRecorder? = null
    private var isRecording = false
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private fun createRecorder(): MediaRecorder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else MediaRecorder()
    }

    private fun getBestAudioSource(): Int {
        return try {
            MediaRecorder.AudioSource.VOICE_RECOGNITION
        } catch (e: Exception) {
            MediaRecorder.AudioSource.MIC
        }
    }

    override fun start(outputFile: File) {
        if (isRecording) {
            stop()
        }
        val audioSource = getBestAudioSource()
        createRecorder().apply {
            setAudioSource(audioSource) // or MIC as fallback
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioSamplingRate(44100) // Set sampling rate
            setAudioEncodingBitRate(192000) // Set higher bitrate for better quality

            setOutputFile(FileOutputStream(outputFile).fd)
            prepare()
            start()
            isRecording = true
            recorder = this
        }
    }

    override fun stop() {
        recorder?.stop()
        recorder?.reset()
        isRecording = false
        recorder = null
    }
}