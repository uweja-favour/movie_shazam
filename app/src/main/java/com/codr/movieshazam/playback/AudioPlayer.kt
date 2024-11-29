package com.codr.movieshazam.playback

import java.io.File

interface AudioPlayer {
    fun playFile(file: File)
    fun stop()
    fun pause()
}