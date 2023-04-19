package com.example.data.model.user_details

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class User(
    @BsonId
    val id: String = ObjectId().toString(),
    val subId : String? = null,
    val name: String?= null,
    val emailAddress: String?= null,
    val userHeartId: String?= null,
    val profilePhoto: String?= null,
    val connectedHeardId: String?= null,
    val connectedUserName: String? = null,
    val connectedUserEmail: String? = null,
    val connectUserPhoto: String? = null,
    val fcmToken : String? = null,
    val listOfConnectRequest : List<ConnectionRequest?> = listOf()
)