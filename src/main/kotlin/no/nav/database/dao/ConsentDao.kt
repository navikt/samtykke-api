package no.nav.database.dao

import no.nav.database.dao.ConsentDao.ConsentQueries.POST_CONSENT
import no.nav.models.Consent
import no.nav.models.CreateConsentRequest
import java.sql.Date
import javax.sql.DataSource

class ConsentDao(
    private val dataSource: DataSource
) {
    fun createConsent(consent: CreateConsentRequest, employeeId: String, code: String) {
        dataSource.connection.use {
            it.prepareStatement(POST_CONSENT).apply {
                setString(1, consent.title)
                setString(2, consent.responsibleGroup)
                setString(3, consent.purpose)
                setInt(4, consent.totalInvolved)
                setDate(5, Date.valueOf(consent.expiration.toString()))
                setString(6, code)
                setString(7, employeeId)
            }.executeUpdate()
        }
    }

    private object ConsentQueries {
        val POST_CONSENT = """
            INSERT INTO consent
            (title, responsible_group, purpose, total_involved, expiration, code, employee_id)
            VALUES
            (?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()
    }
}