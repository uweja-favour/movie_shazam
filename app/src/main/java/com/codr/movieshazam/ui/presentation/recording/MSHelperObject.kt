package com.codr.movieshazam.ui.presentation.recording

import com.codr.movieshazam.data.Recording
import kotlinx.coroutines.flow.MutableStateFlow

object MSHelperObject {
    val listOfRecordings = MutableStateFlow<List<Recording>>(emptyList())
    val isPlaying = MutableStateFlow(false)
    val isRecording = MutableStateFlow(false)
}