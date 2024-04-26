package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Teacher(
    val name: String,
    val surname: String,
    val mail: String
)
