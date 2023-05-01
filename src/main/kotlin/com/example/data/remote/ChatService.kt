package com.example.data.remote

import com.example.domain.UserRepository
import io.ktor.websocket.*
import java.util.concurrent.ConcurrentHashMap

class ChatService(private val userRepository: UserRepository) {
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



    suspend fun sendMessage(toUserId: String, messageEntityString: String, fromUserHeartId: String) {
        // Find the user to send the message to
        // Some logic to determine the recipient
        val toUserSession = users[toUserId] // If the user is not online, do nothing

        if(toUserSession == null){
            if(userRepository.getUserByHeartId(fromUserHeartId)?.connectedHeardId != null){
                userRepository.sendMessageNotification(toHeartId = toUserId,messageEntityString = messageEntityString,fromUserHeartId = fromUserHeartId)
            }
        }else{
            toUserSession.send(messageEntityString)
        }
    }
}