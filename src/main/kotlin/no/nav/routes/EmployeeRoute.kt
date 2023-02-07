package no.nav.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.models.Consent
import no.nav.models.CreateConsentRequest
import no.nav.models.Employee
import no.nav.services.ConsentService
import no.nav.services.EmployeeService

fun Route.employeeRoute(
    employeeService: EmployeeService,
    consentService: ConsentService
) {
    route("currentEmployee") {
        get {
            try {
                // TODO: replace this by getting id from AzureOBO token
                val employee: Employee = employeeService.getEmployee("sgoijh20u5")
                call.respond(employee)
            } catch (e: Exception) {
                call.respondText(
                    "Error getting employee",
                    status = HttpStatusCode.NotFound
                )
            }
        }
    }

    route("consent") {
        post {
            try {
                val source = call.receive<CreateConsentRequest>()
                // TODO: replace this by getting id from AzureOBO token
                consentService.createConsent(source, "sgoijh20u5")
                call.respond(HttpStatusCode.OK)
            } catch (e: Exception) {
                print(e)
                call.respond(HttpStatusCode.NotAcceptable)
            }
        }

        route("active") {

        }
    }
}