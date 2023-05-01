package com.example.data.model.fcm.req


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Body(
    @SerialName("currentTime")
    val currentTime: String?,
    @SerialName("fromUserHeartId")
    val fromUserHeartId: String?,
    @SerialName("id")
    val id: Int?,
    @SerialName("image")
    val image: String? = null,
    @SerialName("isMine")
    val isMine: Boolean?,
    @SerialName("message")
    val message: String?,
    @SerialName("toUserHeartId")
    val toUserHeartId: String?
)