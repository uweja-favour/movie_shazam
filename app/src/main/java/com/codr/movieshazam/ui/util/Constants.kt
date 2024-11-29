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

    val screenRoutes = listOf(
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


    const val RECORD = "record"
    const val STOP_RECORD = "stop_record"
    const val PLAY = "play"
    const val STOP_PLAYING = "stop_playing"
    const val PLAYBACK_COMPLETE = "playback_completed"
    const val RECORDING = "Recording"
    const val SNACK_BAR_EVENT = "snack_bar_event"
}