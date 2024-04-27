package com.example

import com.example.plugins.configureRouting
import freemarker.cache.ClassTemplateLoader
import freemarker.core.HTMLOutputFormat
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.freemarker.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
//    val SUPABASE_URL = environment.config.propertyOrNull("supabase.url")?.getString() ?: "hot"
//    val SUPABASE_KEY = environment.config.propertyOrNull("supabase.key")?.getString() ?: "fire"

    // configure freemarker
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
        outputFormat = HTMLOutputFormat.INSTANCE
    }
    // configure serialization
    install(ContentNegotiation) {
        json()
    }
    // TODO put configs on separate files
    configureRouting()
}
