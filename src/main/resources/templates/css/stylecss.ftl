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
        padding-left: 40%;
        padding-right: 40%;
        padding-bottom: 20px;
        grid-template-columns: 0.5fr 0.5fr;
        grid-template-rows: repeat(5, 1fr);
        row-gap: 20px;
        justify-content: center;
    }

    .div-label {
        color: #d2b058;
        text-align: left;
    }

    label::after {
        content: ':';
    }

    .div-field {
        color: #b2b8a1;
        text-align: left;
    }

    .form-label-container {
    }

    .feedback-form {
        display: grid;
        padding-left: 40%;
        padding-right: 30%;
        grid-template-columns: repeat(4, 1fr);
        grid-template-rows: repeat(4, 1fr);
        gap: 10px;
        justify-content: center;
    }

    .form-label {
        grid-row: 2;
        grid-column: 1;
        color: #d2b058;
        text-align: left;
    }

    textarea {
        grid-row: 1 / 4;
        grid-column: 2 / 5;
        padding: 10px;
        margin-left: 10%;
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
        grid-row: 4;
        grid-column: 4;
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