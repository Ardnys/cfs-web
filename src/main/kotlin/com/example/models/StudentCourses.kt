package com.example.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudentCourses(
    @SerialName("student_id")
    val studentId: Int? = null,
    @SerialName("course_id")
    val courseId: Int? = null
)
