package com.example.data.model.fcm.req


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FcmRequest(
    @SerialName("data")
    val `data`: Data? = null,
    @SerialName("notification")
    val notification: Notification? = null,
    @SerialName("to")
    val to: String? = "",
    val priority: String =  "high"
)