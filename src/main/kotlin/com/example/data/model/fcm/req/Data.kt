package com.example.data.model.fcm.req


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Data(
    @SerialName("dl")
    val dl: String? = "NA",
    @SerialName("url")
    val url: String? = "NA"
)