package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val response: T? = null,
    val message: String? = null
)