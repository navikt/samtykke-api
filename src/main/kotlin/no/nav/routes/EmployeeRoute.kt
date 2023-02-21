package no.nav.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.models.CreateConsentRequest
import no.nav.models.Employee
import no.nav.services.ConsentService
import no.nav.services.EmployeeService
import no.nav.services.MessageService

fun Route.employeeRoute(
    employeeService: EmployeeService,
    consentService: ConsentService,
    messageService: MessageService
) {
    route("currentEmployee") {
        get {
            // TODO: replace this by getting id from AzureOBO token
            val employee: Employee = employeeService.getEmployee("sgoijh20u5")
            call.respond(employee)
        }
    }

    route("consent") {
        post {
                val source = call.receive<CreateConsentRequest>()
                // TODO: replace this by getting id from AzureOBO token
                consentService.createConsent(source, "bdfhw3fsd")
                call.respond(HttpStatusCode.OK)
        }

        route("active") {
            get {
                try {
                    val activeConsents = consentService.getEmployeeActiveConsents("bdfhw3fsd")
                    call.respond(activeConsents)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.NotAcceptable)
                }
            }
        }

        route("{code}") {
            get {
                try {
                    val code = call.parameters["code"].toString()
                    val consent = consentService.getConsentByCodeWithCandidates(code)
                    call.respond(consent)
                } catch (e: Exception) {
                    call.respondText(
                        "Error getting consent",
                        status = HttpStatusCode.NotFound
                    )
                }
            }
        }
    }

    route("messages") {
        get {
            try {
                val messages = messageService.getMessagesByEmployeeId("sgoijh20u5")
                call.respond(messages)
            } catch (e: Exception) {
                call.respondText(
                    "Error getting messages",
                    status = HttpStatusCode.NotFound
                )
            }
        }
    }
}