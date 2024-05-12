package com.example.plugins

import com.example.models.*
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

fun Application.configureRouting() {
    // TODO a different architecture for the routes perhaps
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get("/supa") {
            val students = supabase.from("students").select().decodeList<Student>()
//            val course = supabase.from("courses").select().decodeList<Course>()[0].toString()
            call.respondText(students.joinToString())
//            supabase
//                .from("courses")
//                .insert(listOf(
//                    Course("Intro to Java", "CS100"),
//                    Course("Calculus 1", "MATH 151"),
//                    Course("Physics 1", "PHYS 131")
//                )
//                )

        }
        route("feedback") {
            get("{course_id}") {
                // TODO handle invalid course ids
                val code = call.parameters.getOrFail<String>("course_id")
                val course = supabase.from("courses").select {
                    filter {
                        eq("course_code", code)
                    }
                }.decodeSingle<Course>()

                val feedback = supabase
                    .from("feedbacks")
                    .select(columns = Columns.list("id, url, course_date, course_topic, course_id, feedback_start_date")) {
                        filter {
                            if (course.id != null) {
                                and {
                                    eq("course_id", course.id)
                                    gt("feedback_start_date", OffsetDateTime.now().minusHours(48))
                                }
                            }
                        }
                    }
                    .decodeSingle<Feedback>()

                call.respond(FreeMarkerContent("feedback_form.ftl", mapOf("course" to course, "feedback" to feedback)))

            }
            post("{course_id}") {
                val formParameters = call.receiveParameters()
                val studentFeedbackText = formParameters.getOrFail("student_feedback")
                val feedbackId = formParameters.getOrFail<Int>("feedback_id").toInt()
                // TODO check if the feedback is 500 chars or words whatever

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
