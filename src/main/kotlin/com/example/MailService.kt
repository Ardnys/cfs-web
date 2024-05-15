package com.example

import com.example.models.*
import com.example.plugins.supabase
import com.example.utils.MailSender
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import io.ktor.util.logging.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.json.Json

object MailService {
    private val json = Json
    private var lastFeedback: Feedback? = null
    private val logger = KtorSimpleLogger("mail service logger")

    suspend fun mailListener() {
        val channel = supabase.channel("mailer")
        val changeFlow = channel.postgresChangeFlow<PostgresAction>(schema = "public") {
            table = "feedbacks"
        }
        changeFlow.onEach { action ->
            if (action is PostgresAction.Insert) {
                lastFeedback = json.decodeFromString<Feedback>(action.record.toString())
                sendURLToStudents()
            } else if (action is PostgresAction.Update) {
                lastFeedback = json.decodeFromString<Feedback>(action.record.toString())
                sendSummaryToTeacher()
            }
        }.launchIn(CoroutineScope(Dispatchers.IO))
        supabase.realtime.connect()
        channel.subscribe()
    }

    private suspend fun sendURLToStudents() {
        val subject = "No feedback?(,,>﹏<,,)"
        val message = createMessageForStudent()
        val enrolledStudentsMails = getEnrolledStudentsMails()
        logger.info("sending summary to students")
        for (mail in enrolledStudentsMails) {
            MailSender.sendMail(subject, message, mail, true)
        }
    }

    private suspend fun createMessageForStudent(): String {
        val response = supabase
            .from("courses")
            .select(columns = Columns.list("id", "course_name", "course_code", "teacher_id")) {
                filter {
                    eq("id", lastFeedback?.courseId!!)
                }
            }.decodeSingle<Course>()

        val message = """
            <html>
            <body style="font-family: Verdana; color: #777777;">
                <p>Our precious student♡,</p>
                <p>Yor feedback regarding our last class of ${response.courseCode} (${response.courseName}) is required for you 🫵🏻 to pass your class.</p>
                <p>You can access the form from here ---> ${lastFeedback?.url}.<p>
                <p>XOX,<p>
                <br><br>
                <img src="https://content.imageresizer.com/images/memes/Megamind-no-bitches-meme-65939r.jpg" alt="Feedback Form">
            </body>
            </html>
        """
        return message
    }

    private suspend fun getEnrolledStudentsMails(): List<String> {
        val studentMails = mutableListOf<String>()
        val studentIds = getEnrolledStudentIds()

        val response = supabase
            .from("students")
            .select(columns = Columns.list("id", "name", "surname", "mail")) {
                filter {
                    isIn("id", studentIds)
                }
            }.decodeList<Student>()

        for (student in response) {
            studentMails.add(student.mail)
        }

        return studentMails
    }

    private suspend fun getEnrolledStudentIds(): List<Int> {
        val idList = mutableListOf<Int>()
        val response = supabase
            .from("student_courses")
            .select(Columns.raw("student_id, course_id")) {
                filter {
                    eq("course_id", lastFeedback?.courseId!!)
                }
            }.decodeList<StudentCourses>()

        for (id in response) {
            idList.add(id.studentId!!)
        }
        return idList
    }

    private suspend fun sendSummaryToTeacher() {
        val subject = "Feedbacks are summarized"
        if(lastFeedback?.summary == null){
            logger.info("Feedback summary is not created yet!")
            return
        } else if (lastFeedback?.url == null){
            logger.info("Feedback summary is already sent!")
            return
        }
        val response = supabase
            .from("teachers")
            .select(Columns.raw("id,name,surname,mail")) {
                filter {
                    eq("id", getTeacherId())
                }
            }.decodeSingle<Teacher>()
        logger.info("sending summary to teacher: ${response.mail}")
        MailSender.sendMail(subject, lastFeedback?.summary!!, response.mail)
    }

    private suspend fun getTeacherId(): Int {
        val response = supabase
            .from("courses")
            .select(columns = Columns.list("id", "course_name", "course_code", "teacher_id")) {
                filter {
                    eq("id", lastFeedback?.courseId!!)
                }
            }.decodeSingle<Course>()
        return response.teacherId!!
    }
}