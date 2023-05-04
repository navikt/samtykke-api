package no.nav

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import no.nav.citizen.citizenRoute
import no.nav.employee.employeeRoute

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
