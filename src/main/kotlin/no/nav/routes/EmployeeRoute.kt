package no.nav.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.models.Employee
import no.nav.services.EmployeeService

fun Route.employeeRoute(employeeService: EmployeeService) {
    route("currentEmployee") {
        // TODO: replace this by getting id from AzureOBO token
        get {
            try {
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
}