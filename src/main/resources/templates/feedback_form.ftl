<#-- @ftlvariable name="course" type="com.example.models.Course" -->
<#-- @ftlvariable name="feedback" type="com.example.models.Feedback" -->
<#-- @ftlvariable name="courseDateString" type="String" -->
<#-- @ftlvariable name="startMillis" type="Long" -->
<#import "_layout.ftl" as layout />
<@layout.header>
    <div>
        <h3 class="feedback-header">Give feedback</h3>
        <div class="stupid-grid">
            <label class="div-label"> Course Code</label>
            <span class="div-field">${course.courseCode}</span>

            <label class="div-label"> Course Name</label>
            <span class="div-field">${course.courseName}</span>

            <label class="div-label"> Course Date</label>
            <span class="div-field">${courseDateString}</span>

            <label class="div-label"> Course Topic</label>
            <span class="div-field">${feedback.courseTopic}</span>

            <label class="div-label"> Feedback Expiration Countdown</label>
            <span class="div-field" id="feedback_countdown"></span>
        </div>
        <form class="feedback-form" action="${course.courseCode}" method="post">
            <label class="form-label" for="student_feedback">Enter your feedback</label>
            <textarea name="student_feedback" id="student_feedback" placeholder="no more than 500 words"
                      oninput="countWords()"></textarea>
            <input type="hidden" name="feedback_id" value=${feedback.id}>
            <input class="submit_button" id="submit_button" type="submit">
            <span class="word_count_label">words:</span>
            <span class="word_count" id="wordCount"></span>
        </form>
        <p hidden="hidden" id="startMillis">${startMillis}</p>

    </div>
</@layout.header>

<script>
    function countWords() {
        let textArea = document.getElementById("student_feedback")
        let wordCountSpan = document.getElementById("wordCount")
        let words = textArea.value.trim().split(/\s+/).length

        wordCountSpan.textContent = words.toString()

        if (words > 500) {
            wordCountSpan.style.color = "#C94F4F"
            document.getElementById("submit_button").disabled = true
        } else {
            document.getElementById("submit_button").disabled = false
            wordCountSpan.style.color = "#b2b8a1"
        }
    }

    let longText = document.getElementById("startMillis").innerText;

    let feedbackStartMillis = Number(longText.replace(/,/g, ""));
    let fortyEightHours = 48 * 60 * 60 * 1000

    let countdown = setInterval(() => {
        let now = new Date().getTime()

        let distance = fortyEightHours - (now - feedbackStartMillis)

        let days = Math.floor(distance / (1000 * 60 * 60 * 24));
        let hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        let minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
        let seconds = Math.floor((distance % (1000 * 60)) / 1000);

        document.getElementById("feedback_countdown").innerHTML = days + "d " + hours + "h "
            + minutes + "m " + seconds + "s ";

        if (distance < 0) {
            clearInterval(countdown)
            document.getElementById("feedback_countdown").innerHTML = "expired"
        }
    }, 1000)
</script>
