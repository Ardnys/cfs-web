<#-- @ftlvariable name="course" type="com.example.models.Course" -->
<#-- @ftlvariable name="feedback" type="com.example.models.Feedback" -->
<#import "_layout.ftl" as layout />
<@layout.header>
    <div class="feedback-div">
        <h3 class="feedback-header">Give feedback</h3>
        <div class="stupid-grid">
            <label class="div-label"> Course Code</label>
            <span class="div-field">${course.courseCode}</span>

            <label class="div-label"> Course Name</label>
            <span class="div-field">${course.courseName}</span>

            <label class="div-label"> Course Date</label>
            <span class="div-field">${feedback.courseDate}</span>

            <label class="div-label"> Course Topic</label>
            <span class="div-field">${feedback.courseTopic}</span>

            <label class="div-label"> Feedback Start Date</label>
            <span class="div-field">${feedback.feedbackStartDate}</span>
        </div>
        <form class="feedback-form" action="${course.courseCode}" method="post">
            <label class="form-label" for="student_feedback">Enter your feedback</label>
            <textarea  name="student_feedback" id="student_feedback" placeholder="no more than 500 words"></textarea>
            <input type="hidden" name="feedback_id" value=${feedback.id}>
            <input class="submit_button" type="submit">
        </form>
    </div>
</@layout.header>
