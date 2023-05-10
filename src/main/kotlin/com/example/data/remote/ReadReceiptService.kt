package com.example.data.remote

import com.example.domain.UserRepository
import io.ktor.websocket.*
import java.util.concurrent.ConcurrentHashMap

class ReadReceiptService (private val userRepository: UserRepository) {

    private val users = ConcurrentHashMap<String, WebSocketSession>()

    fun register(userId: String, session: WebSocketSession) {
        if(!users.containsKey(userId)){
            users[userId] = session
        }
    }


    suspend fun unregister(userId: String) {
        users[userId]?.close()
        if(users.containsKey(userId)){
            users.remove(userId)
        }
    }



    suspend fun sendReceipt(messageIdResponseString: String, recipientHeartId: String) {
        val recipientUser = userRepository.getUserByHeartId(recipientHeartId)
        val toUserSession = users[recipientUser?.connectedHeardId] // If the user is not online, do nothing
        if(toUserSession == null){
            if(recipientUser?.connectedHeardId != null){
                userRepository.sendReceiptNotification(recipientUser = recipientUser,messageIdResponseString = messageIdResponseString)
            }
        }else{
            toUserSession.send(messageIdResponseString)
        }
    }
}