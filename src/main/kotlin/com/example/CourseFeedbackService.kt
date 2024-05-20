package com.example

import com.example.exceptions.CourseNotFoundException
import com.example.exceptions.FeedbackNotFoundException
import com.example.models.Course
import com.example.models.Feedback
import com.example.plugins.supabase
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import java.time.OffsetDateTime

suspend fun getCourseByCode(code: String): Course {
    return try {
        supabase.from("courses").select {
            filter {
                eq("course_code", code)
            }
        }.decodeSingle<Course>()
    } catch (e: RuntimeException) {
        val message = "Course not found: ${e.message ?: e.toString()}"
        throw CourseNotFoundException(message)
    }
}

suspend fun getFeedbackByCourseId(id: Int?): Feedback {
    return try {
        supabase
            .from("feedbacks")
            .select(columns = Columns.list("id, url, course_date, course_topic, course_id, feedback_start_date")) {
                filter {
                    if (id != null) {
                        and {
                            eq("course_id", id)
                            gt("feedback_start_date", OffsetDateTime.now().minusHours(48))
                        }
                    }
                }
            }
            .decodeSingle<Feedback>()
    } catch (e: RuntimeException) {
        val message = "Feedback form is either expired or doesn't exist: ${e.message ?: e.toString()}"
        throw FeedbackNotFoundException(message)
    }
}
