package com.codr.movieshazam.ui.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.HistoryToggleOff
import androidx.compose.material3.NavigationRailItem
import com.codr.movieshazam.data.NavigationItem

object Constants {

    val navigationItems = listOf(
        NavigationItem(
            title = "Record",
            filledIcon = Icons.Filled.Mic,
            outlinedIcon = Icons.Outlined.Mic
        ),
        NavigationItem(
            title = "History",
            filledIcon = Icons.Filled.History,
            outlinedIcon = Icons.Rounded.HistoryToggleOff
        )
    )
    const val RECORD = "record"
    const val STOP_RECORD = "stop_record"
    const val PLAY = "play"
    const val STOP_PLAYING = "stop_playing"
}