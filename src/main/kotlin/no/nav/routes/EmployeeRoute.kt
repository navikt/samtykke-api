package no.nav.routes

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import kotlinx.coroutines.runBlocking
import no.nav.getEmployeeId
import no.nav.models.CreateConsentPDFRequest
import no.nav.models.CreateConsentRequest
import no.nav.models.Employee
import no.nav.models.ShortenedEmployee
import no.nav.services.ConsentService
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

            route("pdf") {
                get {
                    val code = call.parameters["code"].toString()
                    val consent = consentService.getConsentByCode(code)
                    val employee = employeeService.getEmployee(getEmployeeId(call.principal(), employeeService))
                    val response = httpClient.post {
                        url("${System.getenv("PDFGEN_URL")}/api/v1/genpdf/samtykke/samtykke")
                        contentType(ContentType.Application.Json)
                        setBody(CreateConsentPDFRequest(
                            consent.title,
                            consent.responsibleGroup,
                            consent.theme,
                            consent.purpose,
                            consent.totalInvolved,
                            consent.expiration,
                            consent.endResult,
                            ShortenedEmployee(employee.firstname, employee.lastname, employee.email)
                        ))
                    }
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