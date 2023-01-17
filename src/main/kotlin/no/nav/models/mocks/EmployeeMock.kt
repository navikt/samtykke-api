package no.nav.models.mocks

import no.nav.models.Employee

fun employeeMock(): Employee {
    return Employee(
        "s09wug2knb",
        "Dan",
        "Børge",
        "dan.børge@nav.no",
        listOf(),
        listOf()
    )
}