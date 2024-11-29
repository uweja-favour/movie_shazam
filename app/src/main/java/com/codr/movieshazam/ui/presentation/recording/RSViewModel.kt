package com.codr.movieshazam.ui.presentation.recording

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codr.movieshazam.AnEvent
import com.codr.movieshazam.EventController
import com.codr.movieshazam.RsRepository
import com.codr.movieshazam.SnackBarEvent
import com.codr.movieshazam.data.Recording
import com.codr.movieshazam.playback.AndroidAudioPlayer
import com.codr.movieshazam.record.AndroidAudioRecorder
import com.codr.movieshazam.ui.util.Constants.SNACK_BAR_EVENT
import com.codr.movieshazam.ui.util.getCurrentDate
import com.codr.movieshazam.ui.util.getFormattedPeriod
import com.codr.movieshazam.ui.util.getFormattedTime
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RSViewModel @Inject constructor(
    private val repository: RsRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val recorder by lazy { AndroidAudioRecorder(context.applicationContext) }
    private val player by lazy { AndroidAudioPlayer(context.applicationContext) }

    val listOfRecordings = MutableStateFlow<List<Recording>>(emptyList())
    val isPlaying = MutableStateFlow(false)
    val isRecording = MutableStateFlow(false)
    val noOfCheckedItems = MutableStateFlow(0)

    init { retrieveAllRecordings() }

    override fun onCleared() {
        super.onCleared()
        clear()
    }

    private fun retrieveAllRecordings() = viewModelScope.launch(Dispatchers.IO) {
        listOfRecordings.value = repository.retrieveRecordings()
        Log.d("DATA LOG", "VIEWMODEL retrieved data is ${repository.retrieveRecordings()}")
    }

    private var audioFile: File? = null

    fun startRecording(cacheDir: File, fileName: String) {
        val file = File(cacheDir, fileName)
        try {
            recorder.start(file)
            audioFile = file
            isRecording.value = true
        } catch (e: Exception) {
            Log.e("Playback", "Error starting recording: ${e.message}", e)
        }
    }

    fun stopRecording() {
        try {
            recorder.stop()
            isRecording.value = false
            audioFile?.let {
                val newRecording = Recording(
                    fileName = it.name,
                    filePath = it.absolutePath,
                    dateAdded = "${getCurrentDate()} ${getFormattedTime()} ${getFormattedPeriod()}",
                )
                listOfRecordings.value += newRecording
                saveRecording()
            }
        } catch (e: Exception) {
            Log.e("Playback", "Error stopping recording: ${e.message}", e)
        }
    }

    fun play() {
        if (audioFile?.exists() == true && (audioFile?.length() ?: 0) > 0) {
            player.playFile(audioFile!!)
            isPlaying.value = true
        } else {
            Log.e("Playback", "File does not exist or is empty.")
        }
    }

    fun stopPlaying() {
        if (isPlaying.value) {
            player.stop()
            isPlaying.value = false
        }
    }

    fun pausePlaying() {
        if (isPlaying.value) {
            player.pause()
            isPlaying.value = false
        }
    }

    private fun saveRecording() = viewModelScope.launch(Dispatchers.IO) {
        repository.saveRecordings(listOfRecordings.value)
    }

    private fun clear() {
        audioFile = null
        player.stop()
        recorder.stop()
    }

    fun onTogglePlayBackCompleted() {
        isPlaying.value = false
    }

    fun onBackPressFromRecordingScreen() {
        stopRecording()
    }


    /** HANDLE CHECKBOX FUNCTIONALITIES **/

    fun toggleItemChecked(item: Recording, isChecked: Boolean) {
        Log.d("THE LOG", "boolean value is $isChecked")
        listOfRecordings.value = listOfRecordings.value.map {
            if (it == item) {
                val editedItem = Recording(
                    filePath = it.filePath,
                    fileName = it.fileName,
                    duration = it.duration,
                    dateAdded = it.dateAdded,
                    isChecked = isChecked
                )
                editedItem
            } else it
        }
        computeNoOfCheckedItems()
        Log.d("THE LOG", "no of checked items is now ${noOfCheckedItems.value}")
    }

    fun toggleAllItemsAsChecked(isChecked: Boolean) {
        listOfRecordings.value = listOfRecordings.value.map {
            val editedItem = Recording(
                filePath = it.filePath,
                fileName = it.fileName,
                duration = it.duration,
                dateAdded = it.dateAdded,
                isChecked = isChecked
            )
            editedItem
        }
        computeNoOfCheckedItems()
    }

    private fun computeNoOfCheckedItems() {
        noOfCheckedItems.value = listOfRecordings.value.count { it.isChecked }
    }

    fun deleteCheckedItems() {
        val formerState = listOfRecordings.value
        listOfRecordings.value = listOfRecordings.value.filter {
            !it.isChecked
        }
        computeNoOfCheckedItems()
        showSnackBar(formerState)
    }

    private fun showSnackBar(formerState: List<Recording>)
     = viewModelScope.launch(Dispatchers.Main) {
        EventController.sendEvent(
            AnEvent(
                type = SNACK_BAR_EVENT,
                message = "Recording(s) deleted",
                snackBarEvent = SnackBarEvent(
                    actionTitle = "UNDO",
                    action = {
                        // undo delete action
                        listOfRecordings.value = formerState
                        computeNoOfCheckedItems()
                    }
                )
            )
        )
    }
}