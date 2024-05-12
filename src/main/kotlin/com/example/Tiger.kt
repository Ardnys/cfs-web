package com.example

import com.example.models.Feedback
import com.example.models.StudentFeedback
import com.example.plugins.supabase
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.logging.*
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.time.OffsetDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

internal val LOGGER = KtorSimpleLogger("Tiger Logger")

fun startBackgroundProcess() = runBlocking {
    val dur: Duration = 30.seconds
    LOGGER.info("Starting background process...")
    while (true) {
        val feedback = async {
            checkFeedbackDecay()
        }.await()
        if (feedback.isNotEmpty()) {
            for (f in feedback) {
                callSamurai(f)
            }
        }
        delay(dur)
    }
}

private suspend fun checkFeedbackDecay(): List<Feedback> {
    LOGGER.info("Checking feedback...")
    val feedback = supabase
        .from("feedbacks")
        .select(columns = Columns.list("id, feedback_start_date, course_topic, course_date")) {
            filter {
                // get the feedback older than 48 hours that doesn't have a summary
                and {
                    Feedback::feedbackStartDate lt OffsetDateTime.now().minusHours(48)
                    Feedback::summary isExact null
                }
            }
        }
        .decodeList<Feedback>()
    if (feedback.isNotEmpty()) {
        LOGGER.info("Summarizing expired feedback...")
    } else {
        LOGGER.info("No expired feedback found.")
        return emptyList()
    }
    // set them url to null, they aren't active anymore
    supabase
        .from("feedbacks")
        .update(
            {
                Feedback::url setTo null
            }
        ) {
            filter {
                Feedback::feedbackStartDate lt OffsetDateTime.now().minusHours(48)
            }
        }

    LOGGER.info("Expired feedback urls set to null.")
    return feedback
}

private val client = HttpClient(CIO)

private suspend fun callSamurai(f: Feedback) {
    LOGGER.info("Samurai is called for ${f.courseTopic}")

    val studentFeedback = supabase
        .from("student_feedbacks")
        .select(columns = Columns.list("feedback, feedback_id")) {
            filter {
                if (f.id != null)
                    eq("feedback_id", f.id)
            }
        }
        .decodeList<StudentFeedback>()

    val feedbackText = studentFeedback.joinToString(separator = ". ") { it.feedback.trim() }
    // TODO handle this nicely
    return

    val response: HttpResponse = client.request("http://localhost:7878/summarize") {
        method = HttpMethod.Post
        setBody(feedbackText)
    }
    when (response.status.value) {
        200 -> {
            LOGGER.info("200: Feedback for topic ${f.courseTopic} is summarized successfully.")
            val summarizedText = response.body<String>()

            supabase
                .from("feedbacks")
                .update(
                    {
                        Feedback::summary setTo summarizedText
                    }
                ) {
                    filter {
                        Feedback::id eq f.id
                    }
                }
        }

        400 -> {
            // TODO what should we do here
            // BAD REQUEST
            LOGGER.error("400 BAD REQUEST: Samurai did not like your feedback.")
        }

        500 -> {
            // INTERNAL SERVER ERROR
            LOGGER.error("500 INTERNAL SERVER ERROR: Samurai down! Samurai down!")
        }
    }
}
