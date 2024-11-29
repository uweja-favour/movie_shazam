package com.codr.movieshazam.record

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.provider.MediaStore.Audio.Media
import java.io.File
import java.io.FileOutputStream

class AndroidAudioRecorder(
    private val context: Context
): AudioRecorder {

    private var recorder: MediaRecorder? = null

    private fun createRecorder(): MediaRecorder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else MediaRecorder()
    }

    override fun start(outputFile: File) {
        createRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
//            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4) // change ths to get a better audio recording
//            setAudioEncoder(MediaRecorder.AudioEncoder.AAC) // change ths to get a better audio recording

            // For higher quality, use AMR or PCM formats
            setOutputFormat(MediaRecorder.OutputFormat.AMR_NB)  // You can also try PCM for uncompressed audio

            // Use HE_AAC for better compression and quality
            setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC)  // AAC is fine too, but HE_AAC might give better results
            setOutputFile(FileOutputStream(outputFile).fd)

            prepare()
            start()

            recorder = this
        }
    }

    override fun stop() {
        recorder?.stop()
        recorder?.reset()
        recorder = null
    }
}