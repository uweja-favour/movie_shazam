package com.codr.movieshazam.data

import java.util.UUID

data class Recording(
    val filePath: String,
    val fileName: String = "",
    val id: String = UUID.randomUUID().toString(),
    val duration: String = "",
    val dateAdded: String = "",
    val isChecked: Boolean = false
)