package com.codr.movieshazam.ui.presentation.recording

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codr.movieshazam.AnEvent
import com.codr.movieshazam.AudioRecordingService
import com.codr.movieshazam.EventController
import com.codr.movieshazam.RsDataSource
import com.codr.movieshazam.SnackBarEvent
import com.codr.movieshazam.data.Recording
import com.codr.movieshazam.playback.AndroidAudioPlayer
import com.codr.movieshazam.ui.util.Constants.GET_POST_NOTIFICATIONS_EVENT
import com.codr.movieshazam.ui.util.Constants.KEY_POST_NOTIFICATIONS_GRANTED
import com.codr.movieshazam.ui.util.Constants.PREFS_NAME
import com.codr.movieshazam.ui.util.Constants.SNACK_BAR_EVENT
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RSViewModel @Inject constructor(
    private val rsDataSource: RsDataSource,
    @ApplicationContext private val context: Context
) : ViewModel() {

//  private val recorder by lazy { AndroidAudioRecorder(context.applicationContext) }
    private val player by lazy { AndroidAudioPlayer(context.applicationContext) }

    val listOfRecordings = MSHelperObject.listOfRecordings
    val isPlaying = MSHelperObject.isPlaying
    val isRecording = MSHelperObject.isRecording
    val noOfCheckedItems = MutableStateFlow(0)

    private var audioFile: File? = null

    init {
        retrieveAllRecordings()
    }

    override fun onCleared() {
        super.onCleared()
        clear()
    }

    private fun retrieveAllRecordings() = viewModelScope.launch(Dispatchers.IO) {
        MSHelperObject.listOfRecordings.value = rsDataSource.retrieveRecordings()
        Log.d("DATA LOG", "VIEWMODEL retrieved data is ${rsDataSource.retrieveRecordings()}")
    }

    // Start recording function
    fun startRecording(cacheDir: File, fileName: String) {
       if (!hasPermissionBeenGranted()) {
           viewModelScope.launch {
               EventController.sendEvent(
                   event = AnEvent(
                       type = GET_POST_NOTIFICATIONS_EVENT
                   )
               )
           }
           return
       }
       val intent = Intent(context, AudioRecordingService::class.java).apply {
           putExtra("START_RECORDING", true)
       }
       context.startForegroundService(intent) // Starts the foreground service
       Toast.makeText(context, "Recording started", Toast.LENGTH_SHORT).show()
    }

    private fun hasPermissionBeenGranted(): Boolean {
        val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_POST_NOTIFICATIONS_GRANTED, false)
    }


    // Stop recording function
    fun stopRecording() {
        val intent = Intent(context, AudioRecordingService::class.java).apply {
            putExtra("SAVE_RECORDING", true)
        }
        context.startForegroundService(intent) // Stops the foreground service
        MSHelperObject.isRecording.value = false
        Toast.makeText(context, "Recording stopped", Toast.LENGTH_SHORT).show()
    }

    // Play the recorded audio file
    fun play() {
        if (audioFile?.exists() == true && (audioFile?.length() ?: 0) > 0) {
            player.playFile(audioFile!!)
            MSHelperObject.isPlaying.value = true
        } else {
            Log.e("Playback", "File does not exist or is empty.")
        }
    }

    // Stop playing the audio file
    fun stopPlaying() {
        if (isPlaying.value) {
            player.stop()
            MSHelperObject.isPlaying.value = false
        }
    }

    // Pause playing the audio file
    fun pausePlaying() {
        if (isPlaying.value) {
            player.pause()
            MSHelperObject.isPlaying.value = false
        }
    }

    private fun clear() {
        audioFile = null
        player.stop()
    }

    // Toggle play back completed event
    fun onTogglePlayBackCompleted() {
        isPlaying.value = false
    }

    // Handle back press from the recording screen (stop recording)
    fun onBackPressFromRecordingScreen() {
        stopRecording()
    }

    /** HANDLE CHECKBOX FUNCTIONALITIES **/

    // Toggle checked state of individual items
    fun toggleItemChecked(item: Recording, isChecked: Boolean) {
        Log.d("THE LOG", "boolean value is $isChecked")
        MSHelperObject.listOfRecordings.value = listOfRecordings.value.map {
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

    // Toggle checked state for all items
    fun toggleAllItemsAsChecked(isChecked: Boolean) {
        MSHelperObject.listOfRecordings.value = listOfRecordings.value.map {
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

    // Compute number of checked items
    private fun computeNoOfCheckedItems() {
        noOfCheckedItems.value = listOfRecordings.value.count { it.isChecked }
    }

    // Delete checked items
    fun deleteCheckedItems() {
        val formerState = listOfRecordings.value
        MSHelperObject.listOfRecordings.value = listOfRecordings.value.filter {
            !it.isChecked
        }
        computeNoOfCheckedItems()
        showSnackBar(formerState)
    }

    // Show SnackBar with the undo option
    private fun showSnackBar(formerState: List<Recording>) = viewModelScope.launch(Dispatchers.Main) {
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
