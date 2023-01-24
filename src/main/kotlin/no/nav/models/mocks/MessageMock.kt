package no.nav.models.mocks

import kotlinx.datetime.LocalDate
import no.nav.models.Message

fun messagesMock(): List<Message> {
    return listOf(
        Message(
            1,
            LocalDate(2023, 7, 18),
            "Samtykke gitt til: Brukertest av NAV.no",
            "En innbygger har gitt samtykke til: Brukertest av NAV.no. Klikk deg inn på samtykket for å se hvem som har gitt samtykket",
            false,
            "/X76-2B3"
        ),
        Message(
            2,
            LocalDate(2023, 7, 18),
            "Samtykke trukket til: Brukertest av NAV.no",
            "En innbygger har gitt samtykke til: Brukertest av NAV.no. Klikk deg inn på samtykket for å se hvem som har gitt samtykket",
            false,
            "/X76-2B3"
        ),
        Message(
            3,
            LocalDate(2023, 7, 18),
            "Samtykke: Dagpengeløsning 2.0, slettet",
            "Ditt samtykke Dagpengeløsning 2.0 har blitt slettet siden utløpsdatoen har utgått. Husk på å slette all ekstern data om de involverte i testen!",
            true,
            null
        ),
    )
}