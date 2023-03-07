package no.nav.plugins

import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.response.*
import no.nav.ApplicationContext
import no.nav.isNais
import no.nav.routes.citizenRoute
import no.nav.routes.employeeRoute
import no.nav.routes.healthRoute
import no.nav.routes.mocks.citizenRouteMock
import no.nav.routes.mocks.employeeRouteMock
import java.util.concurrent.TimeUnit

fun Application.configureRouting() {
    install(ContentNegotiation) { json() }
    install(IgnoreTrailingSlash)
    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Put)
    }

    val azureADProvider: JwkProvider = JwkProviderBuilder(System.getenv("AZURE_OPENID_CONFIG_JWKS_URI"))
        .cached(10, 24, TimeUnit.HOURS)
        .rateLimited(10, 1, TimeUnit.MINUTES)
        .build()

    val tokenXProvider: JwkProvider = JwkProviderBuilder(System.getenv("TOKEN_X_ISSUER"))
        .cached(10, 24, TimeUnit.HOURS)
        .rateLimited(10, 1, TimeUnit.MINUTES)
        .build()

    if (isNais()) {
        install(Authentication) {
            try {
                jwt("citizen") {
                    verifier(tokenXProvider, System.getenv("TOKEN_X_ISSUER"))
                    challenge { _, _ -> call.respond(HttpStatusCode.Unauthorized) }
                    validate { cred ->
                        if (!cred.audience.contains(System.getenv("TOKEN_X_CLIENT_ID"))) {
                            println("Audience does not match!")
                            return@validate null
                        }
                        JWTPrincipal(cred.payload)
                    }
                }
            } catch (e: Exception) {
                println(e)
            }
            jwt("employee") {
                verifier(azureADProvider, System.getenv("AZURE_OPENID_CONFIG_ISSUER"))
                challenge { _, _ -> call.respond(HttpStatusCode.Unauthorized) }
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

    routing {
        healthRoute()
        if (System.getenv("NAIS_CLUSTER_NAME") == "labs-gcp") {
            route("citizen") {
                citizenRouteMock()
            }
            route("employee") {
                employeeRouteMock()
            }
        } else {
            val context = ApplicationContext(System.getenv())
            if (isNais()) {
                authenticate("citizen") {
                    route("citizen") {
                        citizenRoute(context.consentService, context.candidateService, context.citizenService)
                    }
                }
                authenticate("employee") {
                    route("employee") {
                        employeeRoute(context.employeeService, context.consentService, context.messageService)
                    }
                }
            } else {
                route("citizen") {
                    citizenRoute(context.consentService, context.candidateService, context.citizenService)
                }
                route("employee") {
                    employeeRoute(context.employeeService, context.consentService, context.messageService)
                }
            }
        }
    }
}
