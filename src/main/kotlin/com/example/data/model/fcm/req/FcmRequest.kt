package com.example.data.model.fcm.req


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FcmRequest(
    @SerialName("data")
    val `data`: Data? = Data(),
    @SerialName("notification")
    val notification: Notification? = Notification(),
    @SerialName("to")
    val to: String? = ""
)