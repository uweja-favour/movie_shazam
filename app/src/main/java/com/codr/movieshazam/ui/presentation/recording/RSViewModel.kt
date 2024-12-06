package com.codr.movieshazam.ui.presentation.recording

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codr.movieshazam.AnEvent
import com.codr.movieshazam.AudioRecordingService
import com.codr.movieshazam.EventController
import com.codr.movieshazam.RsDataSource
import com.codr.movieshazam.SnackBarEvent
import com.codr.movieshazam.data.Recording
import com.codr.movieshazam.playback.AndroidAudioPlayer
import com.codr.movieshazam.ui.util.Constants.KEY_POST_NOTIFICATIONS_GRANTED
import com.codr.movieshazam.ui.util.Constants.PREFS_NAME
import com.codr.movieshazam.ui.util.Constants.SAVE_RECORDING
import com.codr.movieshazam.ui.util.Constants.SNACK_BAR_EVENT
import com.codr.movieshazam.ui.util.Constants.START_RECORDING
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RSViewModel @Inject constructor(
    private val rsDataSource: RsDataSource,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val player by lazy { AndroidAudioPlayer(context.applicationContext) }

    val listOfRecordings = MSHelperObject.listOfRecordings.asStateFlow()
    val isPlaying = MSHelperObject.isPlaying.asStateFlow()
    val isRecording = MSHelperObject.isRecording.asStateFlow()
    private var _noOfCheckedItems = MutableStateFlow(0)
    val noOfCheckedItems = _noOfCheckedItems.asStateFlow()
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

    fun startRecording() = viewModelScope.launch {
       val intent = AudioIntentFactory.createAudioServiceIntent(context, START_RECORDING)
       context.startForegroundService(intent) // Starts the foreground service
       Toast.makeText(context, "Recording started", Toast.LENGTH_SHORT).show()
    }

    // Stop recording function
    fun stopRecording() = viewModelScope.launch {
        if (isRecording.value) {
            val intent = AudioIntentFactory.createAudioServiceIntent(context, SAVE_RECORDING)
            context.startForegroundService(intent)
            StateManager.setRecordingState(false)
            Toast.makeText(context, "Recording stopped", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clear() {
        audioFile = null
        player.stop()
    }

    // Toggle play back completed event
    fun onTogglePlayBackCompleted() {
        StateManager.setPlayingState(false)
    }


    /**
     *  HANDLE CHECKBOX FUNCTIONALITIES
     *
     * **/

    fun toggleItemChecked(isChecked: Boolean, item: Recording, ) {
        Log.d("THE LOG", "boolean value is $isChecked")
        // Update the checked state of the item directly in the list
        MSHelperObject.listOfRecordings.value = listOfRecordings.value.map { recording ->
            if (recording == item) recording.withCheckedState(isChecked) else recording
        }
        updateCheckedItemCount()
    }

    // Toggle checked state for all items
    fun toggleAllItemsAsChecked(isChecked: Boolean = true) {
        // Map all items to the new checked state
        MSHelperObject.listOfRecordings.value = listOfRecordings.value.map { it.withCheckedState(isChecked) }
        updateCheckedItemCount()
    }

    // Compute the number of checked items
    private fun updateCheckedItemCount() {
        // Use `count` directly to compute the value
        _noOfCheckedItems.value = listOfRecordings.value.count { it.isChecked }
    }

    // Delete checked items
    fun deleteCheckedItems() {
        val previousState = listOfRecordings.value
        // Filter out checked items
        MSHelperObject.listOfRecordings.value = listOfRecordings.value.filterNot { it.isChecked }
        updateCheckedItemCount()
        showSnackBar(previousState)
    }

    // Show SnackBar with the undo option
    private fun showSnackBar(previousState: List<Recording>) = viewModelScope.launch {
        EventController.sendEvent(
            AnEvent(
                type = SNACK_BAR_EVENT,
                message = "Recording(s) deleted",
                snackBarEvent = SnackBarEvent(
                    actionTitle = "UNDO",
                    action = {
                        // Undo the delete action
                        MSHelperObject.listOfRecordings.value = previousState
                        updateCheckedItemCount()
                    }
                )
            )
        )
    }


    object AudioIntentFactory {
        fun createAudioServiceIntent(context: Context, action: String): Intent {
            return Intent(context, AudioRecordingService::class.java).apply {
                putExtra(action, true)
            }
        }
    }

    object StateManager {
        fun setRecordingState(isRecording: Boolean) {
            MSHelperObject.isRecording.value = isRecording
        }

        fun setPlayingState(isPlaying: Boolean) {
            MSHelperObject.isPlaying.value = isPlaying
        }
    }

    private fun Recording.withCheckedState(isChecked: Boolean): Recording {
        return Recording(
            filePath = this.filePath,
            fileName = this.fileName,
            duration = this.duration,
            dateAdded = this.dateAdded,
            isChecked = isChecked
        )
    }


    /**
     *
     *
     * HANDLES PLAYBACK
     *
     *
     */

    // Play the recorded audio file
    fun play() {
        if (audioFile?.exists() == true && (audioFile?.length() ?: 0) > 0) {
            player.playFile(audioFile!!)
            StateManager.setPlayingState(true)
        } else {
            Log.e("Playback", "File does not exist or is empty.")
        }
    }

    // Stop playing the audio file
    fun stopPlaying() {
        if (isPlaying.value) {
            player.stop()
            StateManager.setPlayingState(false)
        }
    }

    // Pause playing the audio file
    fun pausePlaying() {
        if (isPlaying.value) {
            player.pause()
            StateManager.setPlayingState(false)
        }
    }
}
