package com.example.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudentFeedback(
    val feedback: String,
    @SerialName("feedback_id")
    val feedbackId: Int,
)