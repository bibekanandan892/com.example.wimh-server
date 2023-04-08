package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Status(
    val success: Boolean,
    val message: String? = null
)
