package no.nav.models.mocks

import kotlinx.datetime.LocalDate
import no.nav.models.Consent

fun consentsMock(): List<Consent> {
    return listOf(
        Consent(
            1,
            "Brukertest for NAV.no",
            "Team Personbruker",
            "dårlig råd",
            "Vi skal sjekke om ting er bra med NAV.no",
            4,
            LocalDate(2023, 3, 4),
            "rapport",
            "X76-2B3",
            candidatesMock(),
            employeeMock()
        ),
        Consent(
            2,
            "Test av ny AAP kalkulator",
            "AAP",
            "dårlig råd",
            "Vi skal teste den nye AAP kalkulatoren for å se om den er bra",
            5,
            LocalDate(2023, 7, 18),
            "rapport",
            "L90-12N",
            listOf(),
            employeeMock()
        ),
        Consent(
            3,
            "Dagpengeløsning 2.0",
            "Team Dagpenger",
            "dårlig råd",
            "Korona tvungte oss til å gjøre dette",
            3,
            LocalDate(2023, 5, 12),
            "rapport",
            "12J-0ZA",
            listOf(),
            employeeMock()
        )
    )
}