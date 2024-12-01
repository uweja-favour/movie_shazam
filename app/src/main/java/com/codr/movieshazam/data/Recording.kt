package com.codr.movieshazam.data

import com.codr.movieshazam.ui.util.getCurrentMillis
import java.util.UUID

data class Recording(
    val filePath: String,
    val fileName: String = "",
    val id: String = "${UUID.randomUUID()}${getCurrentMillis()}",
    val duration: String = "",
    val dateAdded: String = "",
    val isChecked: Boolean = false
)