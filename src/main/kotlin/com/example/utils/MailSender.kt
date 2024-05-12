package com.example.utils

import com.example.exceptions.ClientSecretsNotFoundException
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import java.util.Base64
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.GmailScopes.GMAIL_SEND
import com.google.api.services.gmail.model.Message
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.file.Paths
import java.util.*
import javax.mail.Message.RecipientType.TO
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import io.ktor.util.logging.*
object  MailSender {
    private val service: Gmail
    private val logger = KtorSimpleLogger("mail logger")
    init {
        val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
        val jsonFactory = GsonFactory.getDefaultInstance()
        service = Gmail.Builder(httpTransport, jsonFactory, getCredentials(httpTransport, jsonFactory))
            .setApplicationName("Mail Sender")
            .build()
    }

    @Throws(Exception::class)
    private fun getCredentials(httpTransport: NetHttpTransport, jsonFactory: GsonFactory): Credential {
        val clientSecretJson = System.getenv("CLIENT_SECRET_PATH")

        var clientSecrets: GoogleClientSecrets? = null
        try {
            clientSecrets = GoogleClientSecrets.load(
                jsonFactory,
                InputStreamReader(FileInputStream(clientSecretJson), Charsets.UTF_8)
            )
        } catch (e: Exception) {
            val message = "Client secrets file not found."
            logger.error(message)
            throw ClientSecretsNotFoundException(message)

        }

        val flow = GoogleAuthorizationCodeFlow.Builder(
            httpTransport, jsonFactory, clientSecrets, setOf(GMAIL_SEND))
            .setDataStoreFactory(FileDataStoreFactory(Paths.get("tokens").toFile()))
            .setAccessType("offline")
            .build()

        val receiver = LocalServerReceiver.Builder().setPort(8888).build()
        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
    }

    @Throws(Exception::class)
    fun sendMail(subject: String, message: String, receiver: String) {
        val props = Properties()
        val session = Session.getDefaultInstance(props, null)
        val email = MimeMessage(session)
        email.addRecipient(TO, InternetAddress(receiver))
        email.subject = subject
        email.setText(message)

        val buffer = ByteArrayOutputStream()
        email.writeTo(buffer)
        val rawMessageBytes = buffer.toByteArray()
        val encodedEmail = Base64.getUrlEncoder().encodeToString(rawMessageBytes)
        var msg = Message()
        msg.raw = encodedEmail

        try {
            logger.info("Message id: " + msg.id)
            logger.info(msg.toPrettyString())
            service.users().messages().send("me", msg).execute()
        } catch (e: GoogleJsonResponseException) {
            val error = e.details
            if (error.code == 403) {
                logger.error("Unable to send message: " + e.details)
            } else {
                throw e
            }
        }
    }
}
