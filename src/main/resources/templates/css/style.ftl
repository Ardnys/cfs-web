<style>
    body {
        background-color: rgb(17 20 15);
        color: rgb(225 228 218);
        font-family: Arial, sans-serif;
    }

    *,
    ::before,
    ::after {
        box-sizing: border-box;
        margin: 0;
        padding: 0;
    }

    .cfs-header {
        color: rgb(164 211 150);
        margin: 30px;
    }

    .feedback-header {
        margin: 20px;
    }

    .not-found-header {
        color: rgb(105 0 5);
        margin: 5%;
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
        color: rgb(160 207 210);
        text-align: left;
    }

    label::after {
        content: ':';
    }

    .div-field {
        color: rgb(225 228 218);
        text-align: left;
    }

    .feedback-form {
        display: grid;
        padding-left: 40%;
        padding-right: 30%;
        grid-template-columns: 1fr .8fr .8fr .8fr;
        grid-template-rows: repeat(4, 1fr);
        gap: 10px;
        justify-content: center;
    }

    .form-label {
        grid-row: 2;
        grid-column: 1;
        color: rgb(160 207 210);
        text-align: left;
    }

    textarea {
        grid-row: 1 / 4;
        grid-column: 2 / 5;
        padding: 10px;
        margin-left: 5%;
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

    .word_count_label {
        grid-row: 4;
        grid-column: 2;
        color: rgb(160 207 210);
        margin-left: 5%;
        text-align: left;
    }

    .word_count {
        grid-row: 4;
        grid-column: 3;
        color: rgb(192 239 176);
        text-align: left;
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
        background-color: rgb(164 211 150);
    }

    .submit_button:disabled {
        background-color: rgb(105 0 5);
    }
</style>
