package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class StudentFeedback(
    val feedback: String,
    val feedbackId: Int,
)