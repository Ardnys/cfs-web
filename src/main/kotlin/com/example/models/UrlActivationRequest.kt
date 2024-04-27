package com.example.models

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class UrlActivationRequest(
    val date: LocalDateTime,
    val topic: String,
    val course: String
)

/* -- JSON counterpart --
{
    "date": 2024-04-01T09:58:19,
    "topic": "loops (for, while, do/while)",
    "course": "CS50"
}
 */
