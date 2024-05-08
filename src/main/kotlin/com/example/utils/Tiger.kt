package com.example.utils

import com.example.models.Feedback
import com.example.models.StudentFeedback
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.*
import java.time.OffsetDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@OptIn(DelicateCoroutinesApi::class)
fun startBackgroundProcess() = runBlocking {
    val dur: Duration = 2.minutes
    println("Starting background process...")
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

suspend fun checkFeedbackDecay(): List<Feedback> {
    println("Let's check them feedbacks")
    // TODO there's bug where it gets ALL the older feedbacks. We need to filter them
    // TODO error handling too
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
    println("uu nice feedbacks if i say myself")
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
    println("oh so you updated them nicee")
    return feedback
}

private val client = HttpClient(CIO)

suspend fun callSamurai(f: Feedback) {
    println("Samurai is called for ${f.courseTopic}")

    val studentFeedback = supabase
        .from("student_feedbacks")
        .select(columns = Columns.list("feedback, feedback_id")) {
            filter {
                if (f.id != null)
                    eq("feedback_id", f.id)
            }
        }
        .decodeList<StudentFeedback>()
        .joinToString(".\n")
    println("nice feedback: $studentFeedback")
    return
    // TODO early return because summary server isn't fully ready

    val response: HttpResponse = client.request("http://localhost:3000/summarize") {
        method = HttpMethod.Post
        setBody(studentFeedback)
    }
    // TODO handle other return status conditions as well
    if (response.status.value == 200) {
        // yay
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
    } else {
        println("oopsie: " + response.status.value)
    }
}
