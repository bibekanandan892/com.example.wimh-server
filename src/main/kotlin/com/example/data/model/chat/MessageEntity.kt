package com.example.data.model.chat

import kotlinx.serialization.Serializable

@Serializable
data class MessageEntity(
    val id : Int = 0,
    val isMine: Boolean = false,
    val fromUserHeartId: String,
    val toUserHeartId: String,
    val message: String,
    val currentTime: String,
    val image: String? = null,
)
