package no.nav

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import no.nav.plugins.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureRouting()
    // TODO: if not in "dev-gcp" or "prod-gcp", in application context override "Here is application header with token, get user in database based on oid in token"
    //  with "place dummy id to user".
    //  Do normal check with database if id exists, if not, create user with id.
}
