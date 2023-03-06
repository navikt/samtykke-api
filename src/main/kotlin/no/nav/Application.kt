package no.nav

import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import no.nav.plugins.*
import java.util.*
import java.util.concurrent.TimeUnit

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val azureADProvider: JwkProvider = JwkProviderBuilder(System.getenv("AZURE_OPENID_CONFIG_JWKS_URI"))
        .cached(10, 24, TimeUnit.HOURS)
        .rateLimited(10, 1, TimeUnit.MINUTES)
        .build()

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
                verifier(azureADProvider, System.getenv("AZURE_OPENID_CONFIG_ISSUER"))
                challenge{ _, _ -> call.respond(HttpStatusCode.Unauthorized)}
                validate { cred ->
                    if (!cred.audience.contains(System.getenv("AZURE_APP_CLIENT_ID"))) {
                        println("Audience does not match!")
                        return@validate null
                    }

                    JWTPrincipal(cred.payload)
                }
            }
        }
    }
    configureRouting()
}
