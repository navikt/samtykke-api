package no.nav.models.mocks

import kotlinx.datetime.LocalDate
import no.nav.models.Consent

fun consentsMock(): List<Consent> {
    return listOf(
        Consent(
            1,
            "Brukertest for NAV.no",
            "Team Personbruker",
            "Vi skal sjekke om ting er bra med NAV.no",
            4,
            LocalDate(2023, 3, 4),
            "X76-2B3",
            candidatesMock()
        ),
        Consent(
            2,
            "Test av ny AAP kalkulator",
            "AAP",
            "Vi skal teste den nye AAP kalkulatoren for å se om den er bra",
            5,
            LocalDate(2023, 7, 18),
            "L90-12N",
            listOf()
        ),
        Consent(
            3,
            "Dagpengeløsning 2.0",
            "Team Dagpenger",
            "Korona tvungte oss til å gjøre dette",
            3,
            LocalDate(2023, 5, 12),
            "12J-0ZA",
            listOf()
        )
    )
}