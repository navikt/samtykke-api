package no.nav.plugins

import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.logging.*
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
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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

    val httpClient = HttpClient(CIO) {
        install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
            json()
        }
    }

    routing {
        healthRoute()
        val context = ApplicationContext(System.getenv())
        if (isNais()) {
            authenticate("citizen") {
                route("citizen") {
                    citizenRoute(context.consentService, context.candidateService, context.citizenService, httpClient)
                }
            }
            authenticate("employee") {
                route("employee") {
                    employeeRoute(context.employeeService, context.consentService, context.messageService, httpClient)
                }
            }
        } else {
            route("citizen") {
                citizenRoute(context.consentService, context.candidateService, context.citizenService, httpClient)
            }
            route("employee") {
                employeeRoute(context.employeeService, context.consentService, context.messageService, httpClient)
            }
        }
    }
}
