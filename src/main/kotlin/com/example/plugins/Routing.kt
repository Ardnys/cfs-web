package com.example.plugins

import com.example.exceptions.CourseNotFoundException
import com.example.exceptions.FeedbackNotFoundException
import com.example.models.Course
import com.example.models.Feedback
import com.example.models.StudentFeedback
import com.example.models.UrlActivationRequest
import com.example.utils.supabase
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import java.time.OffsetDateTime
import java.time.ZoneId

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

fun Application.configureRouting() {
    // TODO a different architecture for the routes perhaps
    routing {
        route("feedback") {
            get("{course_id}") {
                val code = call.parameters.getOrFail<String>("course_id")

                val course = getCourseByCode(code)
                val feedback = getFeedbackByCourseId(course.id)

                call.respond(FreeMarkerContent("feedback_form.ftl", mapOf("course" to course, "feedback" to feedback)))
            }
            post("{course_id}") {
                val formParameters = call.receiveParameters()
                val studentFeedbackText = formParameters.getOrFail("student_feedback")
                val feedbackId = formParameters.getOrFail<Int>("feedback_id").toInt()

                val wordCount = studentFeedbackText
                    .split(" ")
                    .count()

                supabase
                    .from("student_feedbacks")
                    .insert(StudentFeedback(studentFeedbackText, feedbackId))

                call.respond(FreeMarkerContent("thankyou.ftl", model = null))
            }
            get("activate") {
                // creates a new feedback entry in the database
                // accepts some sort of json
                // date, topic, course code or name
                // i guess request should look like this
                /*
                {
                    "date": 2024-04-01T09:58:19,
                    "topic": "loops (for, while, do/while)",
                    "course": "CS50"
                }
                */
                val request = call.receive<UrlActivationRequest>()
                val course = supabase
                    .from("courses")
                    .select {
                        filter {
                            eq("course_code", request.course)
                        }
                    }
                    .decodeSingle<Course>()

                println("nice request: $request")
                val feedback = Feedback(
                    courseDate = request.date,
                    courseTopic = request.topic,
                    courseId = course.id,
                    url = "feedback/${course.courseCode}",
                    feedbackStartDate = OffsetDateTime.now(ZoneId.systemDefault())
                )

                supabase
                    .from("feedbacks")
                    .insert(feedback)

                call.respond(HttpStatusCode.Created)
            }
        }
    }
}
