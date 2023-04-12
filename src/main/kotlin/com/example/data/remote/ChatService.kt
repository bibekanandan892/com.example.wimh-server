package com.example.data.remote

import com.example.data.model.chat.MessageEntity
import com.example.util.MemberAlreadyExistsException
import io.ktor.websocket.*
import java.util.concurrent.ConcurrentHashMap

class ChatService {
    private val users = ConcurrentHashMap<String, WebSocketSession>()

    fun register(userId: String, session: WebSocketSession) {
        if(users.containsKey(userId)){
            throw MemberAlreadyExistsException()
        }
        users[userId] = session
    }


    suspend fun unregister(userId: String) {
        users[userId]?.close()
        if(users.containsKey(userId)){
            users.remove(userId)
        }
    }



    suspend fun sendMessage(toUserId : String,messageEntityString: String) {
        // Find the user to send the message to
         // Some logic to determine the recipient
        val toUserSession = users[toUserId] ?: return // If the user is not online, do nothing

        // Send the message to the recipient
        toUserSession.send(messageEntityString)
    }
}