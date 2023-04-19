package com.example.data.model.fcm

import kotlinx.serialization.Serializable

@Serializable
data class Result(
    val error: String?,
    val message_id: String?
)