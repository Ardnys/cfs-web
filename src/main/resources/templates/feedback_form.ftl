<#-- @ftlvariable name="course" type="com.example.models.Course" -->
<#-- @ftlvariable name="feedback" type="com.example.models.Feedback" -->
<#import "_layout.ftl" as layout />
<@layout.header>
    <div class="feedback-div">
        <h3 class="feedback-header">Give feedback</h3>
        <form class="feedback-form" action="${course.courseCode}" method="post">
            <div class="stupid-grid">
                <label class="form-label"> Course Code</label>
                <span class="form-field">${course.courseCode}</span>

                <label class="form-label"> Course Name</label>
                <span class="form-field">${course.courseName}</span>

                <label class="form-label"> Course Date</label>
                <span class="form-field">${feedback.courseDate}</span>

                <label class="form-label"> Course Topic</label>
                <span class="form-field">${feedback.courseTopic}</span>

                <label class="form-label"> Feedback Start Date</label>
                <span class="form-field">${feedback.feedbackStartDate}</span>
            </div>
            <p class="form-label-container" >
                <label class="form-label" for="student_feedback">Enter your feedback</label>
                <textarea  name="student_feedback" id="student_feedback" placeholder="no more than 500 words"></textarea>
            <p>
                <input class="submit_button" type="submit">
            </p>
            <p>
                <input type="hidden" name="feedback_id" value=${feedback.id}>
            </p>
        </form>
    </div>
</@layout.header>
