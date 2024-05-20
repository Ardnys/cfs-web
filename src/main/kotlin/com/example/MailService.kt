package com.example

import com.example.models.Course
import com.example.models.Feedback
import com.example.models.Student
import com.example.models.StudentCourses
import com.example.models.Teacher
import com.example.plugins.supabase
import com.example.utils.MailSender
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import io.ktor.util.logging.KtorSimpleLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.json.Json

object MailService {
    private val json = Json
    private var lastFeedback: Feedback? = null
    private val logger = KtorSimpleLogger("mail service logger")
    private var lastCourse: Course? = null

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
        val subject = "Feedback Form"
        val message = createMessageForStudent()
        val enrolledStudentsMails = getEnrolledStudentsMails()
        logger.info("sending feedback form for the course ${lastCourse?.courseCode} (${lastCourse?.courseName}), to registered students!")
        for (mail in enrolledStudentsMails) {
            MailSender.sendMail(subject, message, mail, true)
        }
    }

    private suspend fun createMessageForStudent(): String {
        lastCourse = getLastFeedbacksCourseDetails()
        val message = """
            <html>
            <body style="font-family: Verdana; color: #777777;">
                <p>Dear student,</p>
                <p>A feedback form is created regarding our last class of ${lastCourse?.courseCode} (${lastCourse?.courseName}).</p>
                <p>You can access it from here ---> ${lastFeedback?.url}.<p>
                <p>Best Regards,<p>
                <br><br>
                <img src="https://imgflip.com/s/meme/Shrek-Cat.jpg" alt="cat">
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
        if (lastFeedback?.summary == null) {
            logger.info("Summary for feedback with id={$lastFeedback.id},\"topic={${lastFeedback?.courseTopic}}\",is not created yet!")
            return
        } else if (lastFeedback?.url == null) {
            logger.info("Summary for feedback with id={$lastFeedback.id},\"topic={${lastFeedback?.courseTopic}}\",is already sent!")
            return
        }
        val teacher = supabase
            .from("teachers")
            .select(Columns.raw("id,name,surname,mail")) {
                filter {
                    eq("id", getTeacherId())
                }
            }.decodeSingle<Teacher>()
        val message = createMessageForTeacher()
        logger.info("sending summary to teacher: ${teacher.mail}")
        MailSender.sendMail(subject, message, teacher.mail)
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

    private suspend fun createMessageForTeacher(): String {
        val response = supabase
            .from("courses")
            .select(columns = Columns.list("id", "course_name", "course_code", "teacher_id")) {
                filter {
                    eq("id", lastFeedback?.courseId!!)
                }
            }.decodeSingle<Course>()
        val message = """
            Summarized feedbacks for the course ${response.courseCode} (${response.courseName}) held on ${lastFeedback?.courseDate}:
            
            ${lastFeedback?.summary!!}
        """
        return message
    }
    private suspend fun getLastFeedbacksCourseDetails(): Course {
        val response = supabase
            .from("courses")
            .select(columns = Columns.list("id", "course_name", "course_code", "teacher_id")) {
                filter {
                    eq("id", lastFeedback?.courseId!!)
                }
            }.decodeSingle<Course>()
        return response
    }
}