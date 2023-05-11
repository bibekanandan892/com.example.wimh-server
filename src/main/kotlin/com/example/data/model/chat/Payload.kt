package com.example.data.model.chat

import com.example.data.model.read_receipt.MessageIdResponse
import kotlinx.serialization.Serializable

@Serializable
data class Payload(
    val messageEntity: MessageEntity? = null,
    val messageIdResponse: MessageIdResponse? = null
)
