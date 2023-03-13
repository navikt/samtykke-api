package no.nav.models.mocks

import kotlinx.datetime.LocalDate
import no.nav.models.Candidate
import no.nav.models.CandidateStatus
import java.util.*

fun candidatesMock(): List<Candidate> {
    return listOf(
        Candidate(
            1,
            "Lars Pølse",
            "lars.pølse@nav.no",
            CandidateStatus.ACCEPTED,
            LocalDate(2023, 2, 4),
            UUID.randomUUID().toString(),
            true,
            null
        ),
        Candidate(
            2,
            "Ole Bolle Brus",
            "ole.bolle.brus@outlook.no",
            CandidateStatus.ACCEPTED,
            LocalDate(2023, 2, 4),
            UUID.randomUUID().toString(),
            false,
            null
        ),
        Candidate(
            3,
            "Pelle Politi",
            "pelle.politi@politiet.no",
            CandidateStatus.WITHDRAWN,
            null,
            UUID.randomUUID().toString(),
            false,
            null
        ),
        Candidate(
            4,
            "Nasse Nøff",
            "nasse.noeff@svenske.se",
            CandidateStatus.WITHDRAWN,
            null,
            UUID.randomUUID().toString(),
            false,
            null
        )
    )
}