package com.example.data.model.chat

import io.ktor.websocket.*

data class Member(
    val userName: String,
    val socket: WebSocketSession
)