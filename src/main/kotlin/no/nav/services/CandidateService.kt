package no.nav.services

import no.nav.database.dao.CandidateDao
import no.nav.database.dao.ConsentDao
import no.nav.models.Candidate
import no.nav.models.Consent
import no.nav.models.CreateCandidateRequest

class CandidateService(
    private val consentDao: ConsentDao,
    private val candidateDao: CandidateDao
) {
    fun createCandidature(createCandidateRequest: CreateCandidateRequest, code: String, citizenId: String) {
        //TODO: Fix this mess
        try {
            val consentId: Long = consentDao.getConsentByCode(code).id
            try {
                candidateDao.getCitizenCandidature(consentId, citizenId)
            } catch (e: Exception) {
                // TODO: add much better exception handling, if any other error is thrown, it will be created
                // If exception is thrown, canditature does not exist for citizen on consent
                try {
                    candidateDao.createCandidature(createCandidateRequest, consentId, citizenId)
                } catch (e: Exception) {
                    throw Exception("Could not create candidate")
                }
            }
        } catch (e: Exception) {
            throw Exception("Something went wrong")
        }
    }
}