package com.codr.movieshazam.ui.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

private val timeFormatter = DateTimeFormatter.ofPattern("h:mm a z")

fun getCurrentDate(): String {
    val formatter = DateTimeFormatter.ofPattern("EEE, MMM d")
    return LocalDate.now().format(formatter)
}

fun getCurrentMillis(): String {
    val currentTimeInMillis = System.currentTimeMillis().toString()
    return currentTimeInMillis
}

fun getFormattedTime(): String {
    return ZonedDateTime.now(ZoneId.systemDefault())
        .format(timeFormatter)
        .split(" ")[0]
}

fun getFormattedPeriod(): String {
    return ZonedDateTime.now(ZoneId.systemDefault())
        .format(timeFormatter)
        .split(" ")[1]
}