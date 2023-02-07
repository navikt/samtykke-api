package no.nav.services

import no.nav.database.dao.ConsentDao
import no.nav.models.Consent
import no.nav.models.CreateConsentRequest

class ConsentService(
    private val consentDao: ConsentDao
) {
    fun createConsent(createConsentRequest: CreateConsentRequest, employeeId: String) {
        //TODO: createConsentCode() should check with database if consent code exists
        consentDao.createConsent(createConsentRequest, employeeId, createConsentCode())
    }

    private fun createConsentCode(): String {
        val allowedChars = ('A'..'Z') + ('0'..'9')
        return (1..7).map {
            allowedChars.random()
        }.joinToString("").toCharArray().also {
            it[3] = '-'
        }.joinToString("")
    }
}