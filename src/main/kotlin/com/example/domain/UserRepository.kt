package com.example.domain

import com.example.data.model.user_details.User

interface UserRepository {
    suspend fun saveUser(user : User): Boolean
    suspend fun sendConnectionRequest(fromHeartId: String,toHeartId : String):Boolean
    suspend fun exceptConnectionRequest(senderHeartId : String, acceptorHeartId: String): Boolean
    suspend fun disconnectHeart(userHeartId: String, connectedHeardId: String): Boolean
    suspend fun getHeartIdBySubId(subId: String): String?
    suspend fun getUserByHeartId(heartId: String): User?

}