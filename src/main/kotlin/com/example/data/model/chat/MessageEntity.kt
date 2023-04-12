package com.example.data.model.chat

data class MessageEntity(
    val fromUserHeartId : String,
    val toUserHeartId : String,
    val message: String,
    val currentTime : String
)
