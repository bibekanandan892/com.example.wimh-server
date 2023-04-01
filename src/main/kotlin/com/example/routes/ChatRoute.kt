package com.example.routes

import com.example.data.remote.ChatService
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*

fun Route.chatRoute(chatService: ChatService) {
    authenticate("jwt-auth") {
        webSocket("/chat") {
            val session = call.sessions.get<MySession>()
            if (session == null) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Not authenticated"))
                return@webSocket
            }

            val userId = session.userId // Get the user ID from the session

            // Register the user with the chat service
            chatService.register(userId, this)

            try {
                // Handle incoming messages
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val message = frame.readText()
                        chatService.sendMessage(userId, message)
                    }
                }
            } catch (e: Exception) {
                // Handle exceptions and disconnections
            } finally {
                // Unregister the user from the chat service
                chatService.unregister(userId)
            }
        }
    }
}

data class MySession(
    val userId:String
)
