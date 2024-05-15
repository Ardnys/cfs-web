package com.example

import com.example.exceptions.SamuraiUnavailableException
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
import kotlinx.coroutines.isActive
import kotlinx.coroutines.runBlocking
import java.net.ConnectException
import java.time.OffsetDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

internal val LOGGER = KtorSimpleLogger("Tiger Logger")
private val client = HttpClient(CIO)

fun startBackgroundProcess() = runBlocking {
    MailService.mailListener()
    val dur: Duration = 30.seconds
    LOGGER.info("Starting background process...")

    while (isActive) {
        val feedback = async {
            checkFeedbackDecay()
        }.await()
        if (feedback.isNotEmpty()) {
            for (f in feedback) {
                summarize(f)
            }
            setExpiredUrlToNull()
        }
        delay(dur)
    }
}

// shut down samurai as well
fun tigerCleanup() {
    // just block it I guess why not
    runBlocking {
        LOGGER.info("Samurai shutting down...")
        shutdownSamurai()
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
    for (f in feedback) {
        LOGGER.info("these are expired: ${f.id} -> ${f.courseTopic}")
    }
    return feedback
}

private suspend fun setExpiredUrlToNull() {
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
}

private suspend fun summarize(f: Feedback) {
    LOGGER.info("Samurai is called for ${f.courseTopic} with id ${f.id}")

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
    val response = callSamurai(feedbackText)

    when (response.status.value) {
        200 -> {
            LOGGER.info("200: Feedback for topic ${f.courseTopic} is summarized successfully.")
            val summarizedText = response.body<String>()
            LOGGER.info("Summary: $summarizedText")

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
            // BAD REQUEST
            val message = "400 BAD REQUEST: Samurai did not like your feedback."
            LOGGER.error(message)
            throw SamuraiUnavailableException(message)
        }

        500 -> {
            // INTERNAL SERVER ERROR
            val message = "500 INTERNAL SERVER ERROR: Samurai down! Samurai down!"
            LOGGER.error(message)
            throw SamuraiUnavailableException(message)
        }
    }
}

private suspend fun callSamurai(feedbackText: String): HttpResponse {
    return try {
        client.request("http://localhost:7878/summarize") {
            method = HttpMethod.Post
            setBody(feedbackText)
        }
    } catch (e: ConnectException) {
        val message = "Samurai is not available at the moment: ${e.message ?: e.toString()}"
        LOGGER.error(message)
        throw SamuraiUnavailableException(message)
    }
}


private suspend fun shutdownSamurai() {
    try {
        client.request("http://localhost:7878/shutdown") {
            method = HttpMethod.Get
        }
    } catch (e: ConnectException) {
        val message = "Tried to shutdown Samurai, but it was already down: ${e.message ?: e.toString()}"
        LOGGER.error(message)
        throw SamuraiUnavailableException(message)
    }
}
