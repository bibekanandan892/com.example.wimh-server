package com.example.routes

import com.example.data.model.chat.MessageEntity
import com.example.data.remote.ChatService
import com.example.domain.UserRepository
import com.example.util.Constants
import com.example.util.MemberAlreadyExistsException
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

fun Route.chatRoute(chatService: ChatService,userRepository: UserRepository) {
    authenticate("jwt-auth") {
        webSocket("/chat") {
            val principal = call.authentication.principal<JWTPrincipal>()
            try {
                val senderHeartId = principal?.payload?.getClaim(Constants.HEART_ID_KEY)?.asString()

                if (senderHeartId == null) {
                    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Not authenticated"))
                    return@webSocket
                }
                val isNotConnect = userRepository.getUserByHeartId(heartId = senderHeartId)?.connectedHeardId == null
                // Register the user with the chat service
                chatService.register(senderHeartId, this)
                try {
                    // Handle incoming messages
                     incoming.consumeEach { frame->
                        if (frame is Frame.Text) {
                            val messageEntityString = frame.readText()
                            try {
                                val messageEntity = Json.decodeFromString<MessageEntity>(messageEntityString)
                                chatService.sendMessage(
                                    toUserId = messageEntity.toUserHeartId,
                                    fromUserHeartId = messageEntity.fromUserHeartId,
                                    messageEntityString = messageEntityString
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    chatService.unregister(senderHeartId)
                }
            } catch (e: MemberAlreadyExistsException) {
                call.respond(HttpStatusCode.Conflict)
            } catch (e: Exception) {
                e.printStackTrace()
            }


        }
    }
}

data class MySession(
    val userId: String
)
