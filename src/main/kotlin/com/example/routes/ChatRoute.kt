package com.example.routes

import com.example.data.model.chat.Payload
import com.example.data.remote.ChatService
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
import kotlinx.serialization.encodeToString
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
                chatService.register(senderHeartId, this)
                try {
                     incoming.consumeEach { frame->
                        if (frame is Frame.Text) {
                            val payloadString = frame.readText()
                            try {
                                val payload = Json.decodeFromString<Payload>(payloadString)
                                payload.messageEntity?.let { messageEntity ->
                                    val messageEntityString = Json.encodeToString(messageEntity)
                                    chatService.sendMessage(
                                        toUserId = messageEntity.toUserHeartId,
                                        fromUserHeartId = messageEntity.fromUserHeartId,
                                        messageEntityString = messageEntityString,
                                        messageEntity =messageEntity

                                    )
                                }
                                payload.messageIdResponse?.let {messageIdResponse->
                                    val messageIdResponseString = Json.encodeToString(messageIdResponse)
                                    chatService.sendReceipt(
                                        messageIdResponseString = messageIdResponseString,
                                        recipientHeartId = senderHeartId,
                                        messageIdResponse = messageIdResponse
                                    )
                                }
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

