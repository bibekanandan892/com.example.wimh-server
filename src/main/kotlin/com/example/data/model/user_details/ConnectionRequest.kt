package com.example.data.model.user_details

import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
@Serializable
data class ConnectionRequest(
    val id: String = ObjectId().toString(),
    val subId : String?,
    val name: String?,
    val emailAddress: String?,
    val userHeartId: String?,
    val profilePhoto: String?,
)
