package com.example.data.model.fcm.req


import com.example.data.model.read_receipt.MessageIdResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Data(
    @SerialName("body")
    val body: Body? = null,
    @SerialName("mutable_content")
    val mutableContent: Boolean? = true,
    @SerialName("sound")
    val sound: String? = "Tri-tone",
    @SerialName("title")
    val title: String? = null,
    @SerialName("isDisconnectRequest")
    val isDisconnectRequest : String? = null,
    @SerialName("receiptMessageIdResponse")
    val receiptMessageIdResponse: String? = null
)