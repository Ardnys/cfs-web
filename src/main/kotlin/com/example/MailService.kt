package com.example

import com.example.utils.MailSender
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import jdk.internal.net.http.common.Log.channel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

class MailService(private val supabaseClient : SupabaseClient) {
    init {
//        val listener = supabaseClient
//            .from("feedbacks")
//            .on("*") { payload ->
//                if (payload.eventType == "INSERT") {
//                    GlobalScope.launch(Dispatchers.IO) {
//                        sendURLToStudents()
//                    }
//                } else if (payload.eventType == "UPDATE" && payload.newRecord["summary"] != "null") {
//                    GlobalScope.launch(Dispatchers.IO) {
//                        sendSummaryToTeacher()
//                    }
//                } else { }
//            }
//            .subscribe()
    }

    private suspend fun sendURLToStudents() {
        val subject = "Feedback Form URL"
        val message = createMessageForStudent()
        val enrolledStudentsMails = getEnrolledStudentsMails()
        for (mail in enrolledStudentsMails) {
            MailSender().sendMail(subject, message, mail)
        }
    }

    private suspend fun createMessageForStudent(): String {
        val courseName = supabaseClient
            .from("feedbacks")
            .select(columns = Columns.list("course_name")) {
                order(column = "id", order = Order.ASCENDING)
                limit(count = 1)
            }  .decodeSingle<String>()
        val url = supabaseClient
            .from("feedbacks")
            .select(columns = Columns.list("url")) {
                order(column = "id", order = Order.ASCENDING)
                limit(count = 1)
            }  .decodeSingle<String>()
        val message = """
            Dear student,
                            
            Dear student you can access the feedback form created for the last class about our course $courseName here $url. Your input is valued.
                            
            Best regards,
            """
        return message
    }

    private suspend fun getEnrolledStudentsMails(): List<String> {
        val courseId = getLastFeedbacksCourseId()

        val studentMailsResponse = supabaseClient
            .from("students")
            .select(columns = Columns.list("mail")) {
                filter {
                    "student_id" in supabaseClient
                        .from("student_courses")
                        .select(columns = Columns.list("student_id")) {
                            filter {
                                eq("course_id", courseId)
                            }

                        }
                        .decodeList<String>()
                }
            }
            .decodeList<String>()
        return studentMailsResponse
    }

    private suspend fun getLastFeedbacksCourseId(): Int {
        val courseId = supabaseClient
            .from("feedbacks")
            .select(columns = Columns.list("course_id")) {
                order(column = "id", order = Order.ASCENDING)
                limit(count = 1)

            }  .decodeSingle<Int>()

        return  courseId
    }

    private suspend fun sendSummaryToTeacher() {
        val subject = "Feedbacks are summarized"
        val authenticatedTeacher = supabaseClient.
                from("teachers")
                .select(columns = Columns.list("mail")) {
                order(column = "id", order = Order.ASCENDING)
                limit(count = 1)
            }  .decodeSingle<String>()

        val summary =  supabaseClient
            .from("feedbacks")
            .select(columns = Columns.list("summary")) {
                order(column = "id", order = Order.ASCENDING)
                limit(count = 1)
            }  .decodeSingle<String>()
        MailSender().sendMail(subject, summary, authenticatedTeacher)
    }
}