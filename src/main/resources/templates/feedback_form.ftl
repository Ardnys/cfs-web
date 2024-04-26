<#-- @ftlvariable name="course" type="com.example.models.Course" -->
<#-- @ftlvariable name="feedback" type="com.example.models.Feedback" -->
<#import "_layout.ftl" as layout />
<@layout.header>
    <div>
        <h3>Give feedback</h3>
        <form action="/${course.courseCode}" method="post">
            <p>Course Code: ${course.courseCode}</p>
            <p>Course Name: ${course.courseName}</p>
            <p>Course Date: ${feedback.courseDate}</p>
            <p>Course Topic: ${feedback.courseTopic}</p>
            <p>Course Topic: ${feedback.url}</p>
            <p>
                <input type="hidden" name="feedbackId" value=${feedback.id}>
            </p>
            <p>
                <label for="studentFeedback">Enter your feedback</label>
                <textarea name="studentFeedback" id="studentFeedback" placeholder="no more than 500 words"></textarea>
            <p>
                <input type="submit">
            </p>
        </form>
    </div>
</@layout.header>
