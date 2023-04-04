package com.example.data.model.auth
import kotlinx.serialization.Serializable

@Serializable

data class LoginTokenResponse (
    val token: String? = null,
)

