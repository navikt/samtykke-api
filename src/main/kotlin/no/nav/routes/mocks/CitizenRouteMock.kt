package no.nav.routes.mocks

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.models.mocks.consentsMock

fun Route.citizenRouteMock() {
    route("consent") {
        route("active") {
            get {
                call.respond(listOf(consentsMock()[0], consentsMock()[1]))
            }
        }

        route("{code}") {
            get {
                val code = call.parameters["code"]!!.toString()

                val consents = consentsMock().filter {
                    it.code == code
                }

                if (consents.size === 1) call.respond(consents[0])
                else call.respond(HttpStatusCode.NotFound)
            }

            route("canditature") {
                get {
                    val code = call.parameters["code"]!!.toString()

                    val consents = consentsMock().filter {
                        it.code == code
                    }

                    if (consents.size === 1 && consents[0].candidates.isNotEmpty()) {
                        call.respond(consents[0].copy(candidates = listOf(consents[0].candidates[0])))
                    } else if (consents.size === 1 && consents[0].candidates.isEmpty()) {
                        call.respond(consents[0].copy(candidates = listOf()))
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                }

                post {
                    call.respond(HttpStatusCode.OK)
                }

                put {
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }
}