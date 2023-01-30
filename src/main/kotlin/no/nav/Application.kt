package no.nav

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import no.nav.plugins.*

fun main() {
    if (System.getenv("NAIS_CLUSTER_NAME") != "labs-gcp") {
        val context = ApplicationContext(System.getenv())
    }
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureRouting()
}
