package com.example.plugins

import com.example.models.*
import com.example.supabase
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

fun Application.configureRouting() {
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
                val course = supabase.from("courses").select() {
                    filter {
                        eq("courseCode", code)
                    }
                }.decodeSingle<Course>()

                val feedback = supabase
                    .from("feedbacks")
                    .select(columns = Columns.list("id, courseDate, courseTopic, courseId")) {
                        filter {
                            if (course.id != null) {
                                eq("courseId", course.id)
                            }
                        }
                    }
                    .decodeSingle<Feedback>()


                // TODO if feedback.url == null then no longer active perhaps ?

                call.respond(FreeMarkerContent("feedback_form.ftl", mapOf("course" to course, "feedback" to feedback)))
            }
            post("{course_id}") {
                val formParameters = call.receiveParameters()
                val studentFeedbackText = formParameters.getOrFail("studentFeedback")
                val feedbackId = formParameters.getOrFail<Int>("feedbackId").toInt()

                supabase
                    .from("studentfeedbacks")
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
                val courseId = supabase
                    .from("courses")
                    .select() {
                        filter {
                            eq("courseCode", request.course)
                        }
                    }
                    .decodeSingle<Course>()
                    .id

                // TODO add a timestamp or something for the duration
                println("nice request: $request")
                val feedback = Feedback(
                    courseDate = request.date,
                    courseTopic = request.topic,
                    courseId = courseId
                )

                supabase
                    .from("feedbacks")
                    .insert(feedback)

                call.respond(HttpStatusCode.Created)
            }

        }
    }
}
