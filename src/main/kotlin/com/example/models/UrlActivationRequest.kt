package com.example.models

import com.example.utils.OffsetDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Serializable
data class UrlActivationRequest(
    @Serializable(with = OffsetDateTimeSerializer::class)
    val date: OffsetDateTime,
    val topic: String,
    val course: String
)
