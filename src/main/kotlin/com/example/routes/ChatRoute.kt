package com.example.routes

import com.example.data.model.chat.MessageEntity
import com.example.data.remote.ChatService
import com.example.util.Constants
import com.example.util.MemberAlreadyExistsException
import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

fun Route.chatRoute(chatService: ChatService) {
    authenticate("jwt-auth") {
        webSocket("/chat") {
            val principal = call.authentication.principal<JWTPrincipal>()
            try {
                val senderHeartId = principal?.payload?.getClaim(Constants.HEART_ID_KEY)?.asString()
                if (senderHeartId == null) {
                    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Not authenticated"))
                    return@webSocket
                }
                // Register the user with the chat service
                chatService.register(senderHeartId, this)
                try {
                    // Handle incoming messages
                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            val messageEntityString = frame.readText()
                            try {
//                                 Gson().fromJson(messageEntityString, MessageEntity::class.java)
                                val messageEntity =Json.decodeFromString<MessageEntity>(messageEntityString)
                                chatService.sendMessage(toUserId = messageEntity.toUserHeartId, messageEntityString = messageEntityString)
                            }catch (e: Exception){
                                e.printStackTrace()
                            }

                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Handle exceptions and disconnections
                }
//                finally {
//                    // Unregister the user from the chat service
//                    chatService.unregister(senderHeartId)
//                }
            }catch (e : MemberAlreadyExistsException){
                call.respond(HttpStatusCode.Conflict)
            }catch (e : Exception){
                e.printStackTrace()
            }




        }
    }
}

data class MySession(
    val userId:String
)
