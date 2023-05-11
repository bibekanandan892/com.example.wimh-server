package com.example.data.remote

import com.example.data.model.chat.MessageEntity
import com.example.data.model.chat.Payload
import com.example.data.model.read_receipt.MessageIdResponse
import com.example.domain.UserRepository
import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

class ChatService(private val userRepository: UserRepository) {
    private val users = ConcurrentHashMap<String, WebSocketSession>()

    fun register(userId: String, session: WebSocketSession) {
        if (!users.containsKey(userId)) {
            users[userId] = session
        }
    }


    suspend fun unregister(userId: String) {
        users[userId]?.close()
        if (users.containsKey(userId)) {
            users.remove(userId)
        }
    }


    suspend fun sendMessage(
        messageEntity: MessageEntity
    ) {
        // Find the user to send the message to
        // Some logic to determine the recipient
        val toUserSession = users[messageEntity.toUserHeartId] // If the user is not online, do nothing
        if (toUserSession == null) {
            if (userRepository.getUserByHeartId(messageEntity.fromUserHeartId)?.connectedHeardId != null) {
                userRepository.sendMessageNotification(
                    messageEntity = messageEntity
                )
            }
        } else {
            val messagePayload = Payload(messageEntity = messageEntity)
            val messagePayloadString = Json.encodeToString(messagePayload)
            toUserSession.send(messagePayloadString)
        }
    }

    suspend fun sendReceipt(
        recipientHeartId: String,
        messageIdResponse: MessageIdResponse
    ) {
        val messageIdResponseString = Json.encodeToString(messageIdResponse)
        val recipientUser = userRepository.getUserByHeartId(recipientHeartId)
        val toUserSession = users[recipientUser?.connectedHeardId] // If the user is not online, do nothing
        if (toUserSession == null) {
            if (recipientUser?.connectedHeardId != null) {
                userRepository.sendReceiptNotification(
                    recipientUser = recipientUser,
                    messageIdResponseString = messageIdResponseString
                )
            }
        } else {
            val receiptPayload = Payload(messageIdResponse = messageIdResponse)
            val receiptPayloadString = Json.encodeToString(receiptPayload)
            toUserSession.send(receiptPayloadString)
        }
    }
}