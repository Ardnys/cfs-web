package com.example.plugins

import com.example.models.Course
import com.example.models.Feedback
import com.example.models.Student
import com.example.models.StudentFeedback
import com.example.supabase
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
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
        get("/feedback/{course_id}") {
            val code = call.parameters.getOrFail<String>("course_id")
            val course = supabase.from("courses").select() {
                filter {
                    eq("courseCode", code)
                }
            }.decodeList<Course>()[0]

            val feedback = supabase
                .from("feedbacks")
                .select(Columns.raw("id, courseDate, courseTopic, url, courses(id)"))
                .decodeList<Feedback>()[0]

            // TODO if feedback.url == null then no longer active perhaps ?

            call.respond(FreeMarkerContent("feedback_form.ftl", mapOf("course" to course, "feedback" to feedback)))
        }
        post("/{course_id}") {
            val formParameters = call.receiveParameters()
            val studentFeedbackText = formParameters.getOrFail("studentFeedback")
            val feedbackId = formParameters.getOrFail<Int>("feedbackId").toInt()

            supabase
                .from("studentfeedbacks")
                .insert(StudentFeedback(studentFeedbackText, feedbackId))

            call.respond(FreeMarkerContent("thankyou.ftl", model = null))
        }
    }
}
