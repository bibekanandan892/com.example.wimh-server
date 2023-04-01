package com.example.domain

import com.example.data.model.user_details.ConnectionRequest
import com.example.data.model.user_details.User

interface UserRepository {
    suspend fun saveUser(user : User): Boolean
    suspend fun sendConnectionRequest(heartId: String,senderUser: ConnectionRequest):Boolean
    suspend fun exceptConnectionRequest(senderHeartId : String, acceptorHeartId: String): Boolean
    suspend fun disconnectHeart(user: User): Boolean
    suspend fun getUserHeartId(subId: String): String?
    suspend fun getUserByHeartId(heartId: String): User?

}