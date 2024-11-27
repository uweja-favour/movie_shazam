package com.codr.movieshazam

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

data class AnEvent(
    val name: String,
    val fileName: String,
)

object EventController {
    private val _emit = Channel<AnEvent>()
    val emit = _emit.receiveAsFlow()


    suspend fun sendEvent(event: AnEvent) {
        _emit.send(event)
    }
}