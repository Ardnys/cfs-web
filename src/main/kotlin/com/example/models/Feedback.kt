package com.example.models

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Feedback(
    val id: Int? = null, // TODO idk how to deal with these for now
    val courseDate: LocalDateTime,
    val courseTopic: String,
    val summary: String? = null,
    val url: String? = null,
    val courseId: Int? = null
)
