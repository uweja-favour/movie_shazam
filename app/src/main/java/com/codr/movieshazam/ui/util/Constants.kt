package com.codr.movieshazam.ui.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.rounded.HistoryToggleOff
import com.codr.movieshazam.data.NavigationItem
import com.codr.movieshazam.ui.presentation.recording.DropDownItem

object Constants {

    private val screenRoutes = listOf(
        "record_screen",
        "history_screen"
    )

    val navigationItems = listOf(
        NavigationItem(
            title = "Record",
            route = screenRoutes[0],
            filledIcon = Icons.Filled.Mic,
            outlinedIcon = Icons.Outlined.Mic
        ),
        NavigationItem(
            title = "History",
            route = screenRoutes[1],
            filledIcon = Icons.Filled.History,
            outlinedIcon = Icons.Rounded.HistoryToggleOff
        )
    )

    val searchAndFind = DropDownItem(
        title = "Search and find",
        icon = Icons.AutoMirrored.Filled.Send
    )

    val recordAndSave = DropDownItem(
        title = "Record and save",
        icon = Icons.Default.Save,
    )



    const val PLAYBACK_COMPLETE = "playback_completed"
    const val RECORDING = "Recording"
    const val SNACK_BAR_EVENT = "snack_bar_event"
    const val BASE_URL = "https://movie-shazam.onrender.com/"
    const val PREFS_NAME = "app_prefs"
    const val KEY_POST_NOTIFICATIONS_GRANTED = "post_notifications_granted"
    const val GET_POST_NOTIFICATIONS_EVENT = "get_post_notifications_event"
    const val START_RECORDING = "start_recording"
    const val SAVE_RECORDING = "save_recording"
    const val CHANNEL_ID = "the_channel_id"
    const val NOTIFICATION_ID = 1
    const val RECORD_AUDIO = "record_audio"
}