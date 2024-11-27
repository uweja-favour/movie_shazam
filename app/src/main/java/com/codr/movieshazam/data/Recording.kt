package com.codr.movieshazam.data

import java.util.UUID

data class Recording(
    var filePath: String,
    var fileName: String = "",
    val id: String = UUID.randomUUID().toString(),
    var duration: String = "",
    var dateAdded: String = ""
)