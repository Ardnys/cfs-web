package com.example

import com.example.models.*
import com.example.utils.MailSender
import com.example.utils.supabase
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.json.Json
import java.time.OffsetDateTime

class MailService {
    private var courseId: Int? = null
    private var courseDate: OffsetDateTime? = null
    private var summary: String? = null.toString()
    private var url: String? = null
    private val json = Json

    suspend fun mailListener() {
        val channel = supabase.channel("mailer")
        val changeFlow = channel.postgresChangeFlow<PostgresAction>(schema = "public") {
            table = "feedbacks"
        }
        changeFlow.onEach { action ->
            if (action is PostgresAction.Insert) {
                val feedback = json.decodeFromString<Feedback>(action.record.toString())
                courseId = feedback.courseId
                courseDate = feedback.courseDate
                url = feedback.url
                sendURLToStudents()
            } else if (action is PostgresAction.Update) {
                val feedback = json.decodeFromString<Feedback>(action.record.toString())
                summary = feedback.summary
                sendSummaryToTeacher()
            }
        }.launchIn(CoroutineScope(Dispatchers.IO))
        supabase.realtime.connect()
        channel.subscribe()
    }

    private suspend fun sendURLToStudents() {
        val subject = "Feedback Form URL"
        val message = createMessageForStudent()
        val enrolledStudentsMails = getEnrolledStudentsMails()
        for (mail in enrolledStudentsMails) {
            MailSender().sendMail(subject, message, mail.toString())
        }
    }

    private suspend fun createMessageForStudent(): String {
        val response = supabase
            .from("courses")
            .select(columns = Columns.list("id","course_name","course_code")) {
                filter {
                    eq("id", courseId!!)
                }
            }  .decodeSingle<Course>()

        val message = """
            Dear student,
                            
            Dear student you can access the feedback form created for the last class about our course ${response.courseCode} here $url. Your input is valued.
                            
            Best regards,
            """
        return message
    }

    private suspend fun getEnrolledStudentsMails(): List<Any> {
        val studentMails = mutableListOf<Any>()
        val studentIds =getEnrolledStudentIds()

        val response = supabase
            .from("students")
            .select(columns = Columns.list("id","name","surname","mail")) {
                filter {
                    isIn("id",studentIds)
                }
            }.decodeList<Student>()

        response.forEach { students ->
            students.mail.let {
                studentMails.add(it)
            }
        }

        return studentMails
    }

    private suspend fun getEnrolledStudentIds(): List<Any>{
        val idList = mutableListOf<Any>()
        val response = supabase
            .from("student_courses")
            .select(Columns.raw("student_id, course_id")) {
                filter {
                    eq("course_id",courseId!!)
                }
            }.decodeList<StudentCourses>()

         response.forEach { studentCourses ->
            studentCourses.studentId.let {
                idList.add(it!!)
            }
         }

        return idList
    }

    private suspend fun sendSummaryToTeacher() {
        val subject = "Feedbacks are summarized"
        val response = supabase.
                from("teachers")
                .select(columns = Columns.list("mail","name","surname")) {
                order(column = "id", order = Order.DESCENDING)
                limit(count = 1)
            }  .decodeSingle<Teacher>()

        MailSender().sendMail(subject, summary!!, response.mail)
    }
}