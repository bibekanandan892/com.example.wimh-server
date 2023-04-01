package com.example.data.remote

import io.ktor.websocket.*
import java.util.concurrent.ConcurrentHashMap

class ChatService {
    private val users = ConcurrentHashMap<String, WebSocketSession>()

    fun register(userId: String, session: WebSocketSession) {
        users[userId] = session
    }

    fun unregister(userId: String) {
        users.remove(userId)
    }

    suspend fun sendMessage(fromUserId: String, message: String) {
        // Find the user to send the message to
        val toUserId ="" // Some logic to determine the recipient
        val toUserSession = users[toUserId] ?: return // If the user is not online, do nothing

        // Send the message to the recipient
        toUserSession.send(message)
    }
}