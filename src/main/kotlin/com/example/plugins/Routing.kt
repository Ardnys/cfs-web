package com.example.plugins

import com.example.getCourseByCode
import com.example.getFeedbackByCourseId
import com.example.models.Course
import com.example.models.Feedback
import com.example.models.StudentFeedback
import com.example.models.UrlActivationRequest
import com.example.utils.OffsetDateTimeFormatter
import io.github.jan.supabase.postgrest.from
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.application
import io.ktor.server.application.call
import io.ktor.server.application.log
import io.ktor.server.freemarker.FreeMarkerContent
import io.ktor.server.request.receive
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.util.getOrFail
import java.time.OffsetDateTime
import java.time.ZoneId


fun Application.configureRouting() {
    routing {
        route("feedback") {
            get("{course_id}") {
                val code = call.parameters.getOrFail<String>("course_id")

                val course = getCourseByCode(code)
                val feedback = getFeedbackByCourseId(course.id)

                val courseDateString = OffsetDateTimeFormatter.parse(feedback.courseDate)
                val millis = feedback.feedbackStartDate?.toInstant()?.toEpochMilli()

                application.log.info("feedback served: $feedback")

                call.respond(
                    FreeMarkerContent(
                        "feedback_form.ftl", mapOf(
                            "startMillis" to millis,
                            "courseDateString" to courseDateString,
                            "course" to course,
                            "feedback" to feedback
                        )
                    )
                )
            }
            post("{course_id}") {
                val formParameters = call.receiveParameters()
                val studentFeedbackText = formParameters.getOrFail("student_feedback")
                val feedbackId = formParameters.getOrFail<Int>("feedback_id").toInt()

                supabase
                    .from("student_feedbacks")
                    .insert(StudentFeedback(studentFeedbackText, feedbackId))

                application.log.info("feedback taken for: $feedbackId")

                call.respond(FreeMarkerContent("thankyou.ftl", model = null))
            }
            // TODO delete this when mobile is ready
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
                    courseId = course.id!!,
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
