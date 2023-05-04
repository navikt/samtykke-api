package no.nav

import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.net.URL
import java.util.concurrent.TimeUnit

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    if (isNais()) {
        val azureADProvider: JwkProvider = JwkProviderBuilder(URL(System.getenv("AZURE_OPENID_CONFIG_JWKS_URI")))
            .cached(10, 24, TimeUnit.HOURS)
            .rateLimited(10, 1, TimeUnit.MINUTES)
            .build()

        val tokenXProvider: JwkProvider = JwkProviderBuilder(URL(System.getenv("TOKEN_X_JWKS_URI")))
            .cached(10,24,TimeUnit.HOURS)
            // if not cached, allow max 10 different keys per minute to be fetched from external provider
            .rateLimited(10, 1, TimeUnit.MINUTES)
            .build()
        install(Authentication) {
            jwt("citizen") {
                verifier(tokenXProvider, System.getenv("TOKEN_X_ISSUER"))
                validate { credentials ->
                    require(credentials.payload.audience?.contains(System.getenv("TOKEN_X_CLIENT_ID")) == true) {
                        "Auth: Valid audience not found in claims"
                    }
                    JWTPrincipal(credentials.payload)
                }
            }
            jwt("employee") {
                verifier(azureADProvider, System.getenv("AZURE_OPENID_CONFIG_ISSUER"))
                validate { credentials ->
                    require(credentials.payload.audience?.contains(System.getenv("AZURE_APP_CLIENT_ID")) == true) {
                        "Auth: Valid audience not found in claims"
                    }
                    JWTPrincipal(credentials.payload)
                }
            }
        }
    }
    configureRouting()
}
