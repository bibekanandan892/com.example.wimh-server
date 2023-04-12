package com.example.data.model.chat

import kotlinx.serialization.Serializable

@Serializable
data class MessageEntity(
    val fromUserHeartId : String,
    val toUserHeartId : String,
    val message: String,
    val currentTime : String
)
