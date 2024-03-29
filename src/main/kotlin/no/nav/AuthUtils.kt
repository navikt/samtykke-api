package no.nav

import io.ktor.server.auth.jwt.*
import no.nav.employee.Employee
import no.nav.citizen.CitizenService
import no.nav.employee.EmployeeService

fun getCitizenId(principal: JWTPrincipal?, citizenService: CitizenService): String {
    return if (isNais()) {
        val sub = principal!!.payload.getClaim("sub").asString()
        citizenService.createIfNotExists(sub)
        sub
    } else {
        citizenService.createIfNotExists("sdp40972")
        "sdp40972"
    }
}

fun getEmployeeId(principal: JWTPrincipal?, employeeService: EmployeeService): String {
    return if (System.getenv("NAIS_CLUSTER_NAME") == "prod-gcp") {
        val oid = principal!!.payload.getClaim("oid").asString()
        employeeService.createIfNotExists(
            Employee(
                oid,
                principal.payload.getClaim("name").asString().split(",")[1],
                principal.payload.getClaim("name").asString().split(",")[0],
                principal.payload.getClaim("preferred_username").asString(),
                listOf(),
                listOf()
            )
        )
        oid
    } else {
        employeeService.createIfNotExists(
            Employee(
                "dbpas90j3",
                "Jens",
                "Monsen",
                "jens.monsen@nav.no",
                listOf(),
                listOf()
            )
        )
        "dbpas90j3"
    }
}

fun isNais(): Boolean {
    return System.getenv("NAIS_CLUSTER_NAME") == "dev-gcp" || System.getenv("NAIS_CLUSTER_NAME") == "prod-gcp"
}