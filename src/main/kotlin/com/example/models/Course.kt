package com.example.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Course(
    val id: Int? = null,
    @SerialName("course_name")
    val courseName: String,
    @SerialName("course_code")
    val courseCode: String
)
