package no.nav.routes.mocks

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.models.mocks.consentsMock
import no.nav.models.mocks.employeeMock
import no.nav.models.mocks.messagesMock

fun Route.employeeRouteMock() {
    route("currentEmployee") {
        get {
            call.respond(employeeMock())
        }
    }

    route("consent") {
        post {
            call.respond(HttpStatusCode.OK)
        }

        route("active") {
            get {
                call.respond(consentsMock())
            }
        }

        route("{code}") {
            get {
                val code = call.parameters["code"]!!.toString()

                val consents = consentsMock().filter {
                    it.code == code
                }

                if (consents.size == 1) call.respond(consents[0])
                else call.respond(HttpStatusCode.NotFound)
            }
        }
    }

    route("messages") {
        get {
            call.respond(messagesMock())
        }
    }
}