package no.nav.services

import no.nav.database.dao.CandidateDao
import no.nav.database.dao.ConsentDao
import no.nav.models.CreateCandidateRequest

class CandidateService(
    private val consentDao: ConsentDao,
    private val candidateDao: CandidateDao
) {
    fun createCandidature(createCandidateRequest: CreateCandidateRequest, code: String, citizenId: String) {
        val consent = consentDao.getConsentByCode(code)
        candidateDao.createCandidature(createCandidateRequest, consent.id, citizenId)
    }

    fun anonymizeCandidate(code: String, citizenId: String) {
        val consent = consentDao.getConsentByCode(code)
        candidateDao.anonymizeCandidate(consent.id, citizenId)
    }
}