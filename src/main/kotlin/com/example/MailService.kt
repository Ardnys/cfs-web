package com.example

import com.example.models.*
import com.example.utils.MailSender
import com.example.plugins.supabase
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

object MailService {
    private var courseId: Int? = null
    private var courseDate: OffsetDateTime? = null
    private var summary: String? = null.toString()
    private var url: String? = null
    private val json = Json
    private var feedback: Feedback? = null

    suspend fun mailListener() {
        val channel = supabase.channel("mailer")
        val changeFlow = channel.postgresChangeFlow<PostgresAction>(schema = "public") {
            table = "feedbacks"
        }
        changeFlow.onEach { action ->
            if (action is PostgresAction.Insert) {
                feedback = json.decodeFromString<Feedback>(action.record.toString())
                courseId = feedback!!.courseId
                courseDate = feedback!!.courseDate
                url = feedback!!.url
                sendURLToStudents()
            } else if (action is PostgresAction.Update) {
                feedback = json.decodeFromString<Feedback>(action.record.toString())
                summary = feedback!!.summary
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
            MailSender.sendMail(subject, message, mail)
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
                            
            A feedback form regarding our last class of ${response.courseCode} (${response.courseName}) is created. You can access it from here $url. Your input is valued.
                            
            Best regards,
            """
        return message
    }

    private suspend fun getEnrolledStudentsMails(): List<String> {
        val studentMails = mutableListOf<String>()
        val studentIds =getEnrolledStudentIds()

        val response = supabase
            .from("students")
            .select(columns = Columns.list("id","name","surname","mail")) {
                filter {
                    isIn("id",studentIds)
                }
            }.decodeList<Student>()

        for (student in response) {
            studentMails.add(student.mail)
        }

        return studentMails
    }

    private suspend fun getEnrolledStudentIds(): List<Int>{
        val idList = mutableListOf<Int>()
        val response = supabase
            .from("student_courses")
            .select(Columns.raw("student_id, course_id")) {
                filter {
                    eq("course_id",courseId!!)
                }
            }.decodeList<StudentCourses>()

        for (id in response){
            idList.add(id.studentId!!)
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

        MailSender.sendMail(subject, summary!!, response.mail)
    }
}