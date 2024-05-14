package com.example

import com.example.plugins.configureRouting
import com.example.plugins.configureSerialization
import com.example.plugins.configureStatusPages
import com.example.plugins.configureTemplating
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.util.concurrent.TimeUnit


fun main() {
    // if true, then the start call blocks a current thread until it finishes its execution.
    // If you run start from the main thread with wait = false and nothing else blocking this thread,
    // then your application will be terminated without handling any requests.
    val server = embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = false)

    // the background process (summary stuff) blocks the thread and server responds to requests
    startBackgroundProcess()

    Runtime.getRuntime().addShutdownHook(Thread {
        server.stop(20, 20, TimeUnit.SECONDS)
    })
    Thread.currentThread().join()
}


fun Application.module() {
    // !! supabase config is in SupaHotFire.kt !!
    configureTemplating()
    configureSerialization()
    configureRouting()
    configureStatusPages()

    environment.monitor.subscribe(ApplicationStopping) {
        // clean the tiger
        fallFromGrace()

        println("Application stopped")
    }
}

