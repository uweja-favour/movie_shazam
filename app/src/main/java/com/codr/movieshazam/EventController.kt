package com.codr.movieshazam

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

data class AnEvent(
    val type: String,
    val snackBarEvent: SnackBarEvent? = null,
    val message: String = "",
)

data class SnackBarEvent(
    val actionTitle: String?,
    val action: (() -> Unit)?
)

object EventController {
    private val _emit = Channel<AnEvent>()
    val emit = _emit.receiveAsFlow()


    suspend fun sendEvent(event: AnEvent) {
        _emit.send(event)
    }
}