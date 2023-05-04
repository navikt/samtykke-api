package no.nav.routes

import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import no.nav.consent.BaseConsent
import no.nav.getEmployeeId
import no.nav.models.*
import no.nav.pdf.generateConsentPDF
import no.nav.consent.ConsentService
import no.nav.services.EmployeeService
import no.nav.services.MessageService

@OptIn(InternalAPI::class)
fun Route.employeeRoute(
    employeeService: EmployeeService,
    consentService: ConsentService,
    messageService: MessageService,
    httpClient: HttpClient
) {
    route("currentEmployee") {
        get {
            val employee: Employee = employeeService.getEmployee(getEmployeeId(call.principal(), employeeService))
            call.respond(employee)
        }
    }

    route("consent") {
        post {
                val source = call.receive<BaseConsent>()
                consentService.createConsent(source, getEmployeeId(call.principal(), employeeService))
                call.respond(HttpStatusCode.OK)
        }

        // Deletes all consents which have expired
        delete {
            consentService.deleteExpiredConsentsAndConnectedCandidates()
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

            route("pdf") {
                get {
                    val code = call.parameters["code"].toString()
                    val consent = consentService.getConsentByCode(code)
                    val employee = employeeService.getEmployee(getEmployeeId(call.principal(), employeeService))
                    val response = generateConsentPDF(httpClient, PDFVersion.EMPLOYEE,  consent, employee, null)
                    call.respond(response.content)
                }
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