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
            val employee: Employee = employeeService.getEmployee("bdfhw3fsd")
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
                // TODO: replace this by getting id from AzureOBO token
                val activeConsents = consentService.getEmployeeActiveConsents("bdfhw3fsd")
                call.respond(activeConsents)
            }
        }

        route("{code}") {
            get {
                val code = call.parameters["code"].toString()
                val consent = consentService.getConsentByCodeWithCandidates(code)
                call.respond(consent)
            }
        }
    }

    route("messages") {
        get {
            val messages = messageService.getMessagesByEmployeeId("bdfhw3fsd")
            call.respond(messages)
        }
    }
}