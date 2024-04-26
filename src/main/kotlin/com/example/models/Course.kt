package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Course(
    val id: Int? = null,
    val courseName: String,
    val courseCode: String
)
