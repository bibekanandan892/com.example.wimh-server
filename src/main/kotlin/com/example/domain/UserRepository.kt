package com.example.domain

import com.example.data.model.Status
import com.example.data.model.user_details.User

interface UserRepository {
    suspend fun saveUser(user : User): Status

    suspend fun sendConnectionRequest(fromHeartId: String,toHeartId : String): Status
    suspend fun exceptConnectionRequest(senderHeartId : String, acceptorHeartId: String): Status
    suspend fun disconnectHeart(userHeartId: String, connectedHeardId: String): Status
    suspend fun getHeartIdBySubId(subId: String): String?
    suspend fun getUserByHeartId(heartId: String): User?
    suspend fun updateFcmToken(fcmToken : String?,heartId: String): Status
    suspend fun sendMessageNotification(toHeartId: String, messageEntityString: String, fromUserHeartId: String) : Status
    suspend fun sendDisconnectNotification(connectedHeardId: String):Status

}