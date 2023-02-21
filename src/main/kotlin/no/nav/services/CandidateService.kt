package no.nav.services

import io.ktor.http.*
import io.ktor.server.plugins.*
import no.nav.database.dao.CandidateDao
import no.nav.database.dao.ConsentDao
import no.nav.models.Candidate
import no.nav.models.Consent
import no.nav.models.CreateCandidateRequest
import org.postgresql.util.PSQLException
import kotlin.reflect.typeOf

class CandidateService(
    private val consentDao: ConsentDao,
    private val candidateDao: CandidateDao
) {
    fun createCandidature(createCandidateRequest: CreateCandidateRequest, code: String, citizenId: String) {
        val consent = consentDao.getConsentByCode(code)
        candidateDao.createCandidature(createCandidateRequest, consent.id, citizenId)
    }
}