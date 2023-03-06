package no.nav.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.getEmployeeId
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
            val employee: Employee = employeeService.getEmployee(getEmployeeId(call.principal(), employeeService))
            call.respond(employee)
        }
    }

    route("consent") {
        post {
                val source = call.receive<CreateConsentRequest>()
                consentService.createConsent(source, getEmployeeId(call.principal(), employeeService))
                call.respond(HttpStatusCode.OK)
        }

        route("active") {
            get {
                val activeConsents = consentService.getEmployeeActiveConsents(getEmployeeId(call.principal(), employeeService))
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
            val messages = messageService.getMessagesByEmployeeId(getEmployeeId(call.principal(), employeeService))
            call.respond(messages)
        }
    }
}