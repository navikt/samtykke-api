package no.nav.plugins

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.routing.*
import io.ktor.http.*
import no.nav.ApplicationContext
import no.nav.isNais
import no.nav.routes.citizenRoute
import no.nav.routes.employeeRoute
import no.nav.routes.healthRoute
import no.nav.routes.mocks.citizenRouteMock
import no.nav.routes.mocks.employeeRouteMock

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
