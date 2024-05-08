package com.example.models

import com.example.utils.OffsetDateTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Serializable
data class Feedback(
    val id: Int? = null, // TODO idk how to deal with these for now

    @Serializable(with = OffsetDateTimeSerializer::class)
    @SerialName("course_date")
    val courseDate: OffsetDateTime,

    @SerialName("course_topic")
    val courseTopic: String,
    val summary: String? = null,
    val url: String? = null,

    @SerialName("course_id")
    val courseId: Int? = null,

    @Serializable(with = OffsetDateTimeSerializer::class)
    @SerialName("feedback_start_date")
    val feedbackStartDate: OffsetDateTime? = null, // assigned automagically by db
)
