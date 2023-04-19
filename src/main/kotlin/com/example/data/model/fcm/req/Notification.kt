package com.example.data.model.fcm.req


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    @SerialName("body")
    val body: String? = "NA",
    @SerialName("mutable_content")
    val mutableContent: Boolean? = true,
    @SerialName("sound")
    val sound: String? = "Tri-tone",
    @SerialName("title")
    val title: String? = "NA"
)