<style>
    body {
        background-color: #1e1f22;
        color: #b2b8a1;
        font-family: Arial, sans-serif;
    }

    *,
    ::before,
    ::after {
        /* selecting all elements on the page, along with the ::before
           and ::after pseudo-elements; resetting their margin and
           padding to zero and forcing all elements to calculate their
           box-sizing the same way, 'border-box' includes the border-widths,
           and padding, in the stated width: */
        box-sizing: border-box;
        margin: 0;
        padding: 0;
    }

    .cfs-header {
        color: #57965c;
        margin: 30px;
    }
    .feedback-header {
        margin: 20px;
    }

    .stupid-grid {
        display: grid;
        grid-template-columns: 10% 10%;
        grid-template-rows: repeat(5, 40px);
        gap: 10px;
        justify-content: center;
    }

    /* Form label */
    .form-label {
        color: #d2b058;
        text-align: left;
    }

    label::after {
        content: ':';
    }

    /* Form field */
    .form-field {
        color: #b2b8a1;
        text-align: left;
    }

    textarea {
        width: 40%;
        height: 20%;
        padding: 10px;
        border: 1px solid #777;
        border-radius: 5px;
        background-color: #555;
        color: #fff;
        resize: vertical;
    }

    textarea:focus {
        outline: none;
        border-color: #fff;
    }

    .submit_button {
        padding: 10px 20px;
        background-color: #007bff;
        color: #fff;
        border: none;
        border-radius: 5px;
        cursor: pointer;
        transition: background-color 0.3s ease;
    }

    .submit_button:hover {
        background-color: #0056b3;
    }
</style>