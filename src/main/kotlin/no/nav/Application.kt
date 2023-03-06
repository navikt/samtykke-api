package no.nav

import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import no.nav.plugins.*
import java.util.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    if (isNais()) {
        install(Authentication) {
            jwt("citizen") {
                // TODO: add propper checking
                validate { cred ->
                    JWTPrincipal(cred.payload)
                }
            }
            jwt("employee") {
                // TODO: add propper checking
                validate { cred ->
                    JWTPrincipal(cred.payload)
                }
            }
        }
    }
    configureRouting()

}
