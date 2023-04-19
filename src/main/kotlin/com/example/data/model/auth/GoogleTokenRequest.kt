package com.example.data.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class GoogleTokenRequest(
    val tokenId: String? = null ,
    val fcmToken: String? = null,
)