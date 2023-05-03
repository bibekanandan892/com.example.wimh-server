package com.example.data.model.read_receipt


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageIdResponse(
    @SerialName("messageIdList")
    val messageIdList: List<Long?>?
)