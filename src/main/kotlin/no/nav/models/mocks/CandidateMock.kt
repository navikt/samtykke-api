package no.nav.models.mocks

import kotlinx.datetime.LocalDate
import no.nav.models.Candidate
import no.nav.models.CandidateStatus

fun candidatesMock(): List<Candidate> {
    return listOf(
        Candidate(
            1,
            "Lars Pølse",
            "lars.pølse@nav.no",
            CandidateStatus.ACCEPTED,
            LocalDate(2023, 2, 4),
            true,
            false
        ),
        Candidate(
            2,
            "Ole Bolle Brus",
            "ole.bolle.brus@outlook.no",
            CandidateStatus.ACCEPTED,
            LocalDate(2023, 2, 4),
            false,
            true
        ),
        Candidate(
            3,
            "Pelle Politi",
            "pelle.politi@politiet.no",
            CandidateStatus.WITHDRAWN,
            null,
            false,
            false
        ),
        Candidate(
            4,
            "Nasse Nøff",
            "nasse.noeff@svenske.se",
            CandidateStatus.WITHDRAWN,
            null,
            false,
            false
        )
    )
}