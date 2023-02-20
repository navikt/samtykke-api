package no.nav.database.dao

import kotlinx.datetime.LocalDate
import no.nav.database.dao.ConsentDao.ConsentQueries.POST_CONSENT
import no.nav.database.dao.ConsentDao.ConsentQueries.SELECT_ALL_ACTIVE_CONSENTS
import no.nav.database.dao.ConsentDao.ConsentQueries.SELECT_CONSENT_BY_CODE
import no.nav.database.dao.ConsentDao.ConsentQueries.SELECT_CONSENT_BY_ID
import no.nav.database.toList
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

    fun getActiveConsents(employeeId: String): List<Consent> {
        dataSource.connection.use {
            return it.prepareStatement(SELECT_ALL_ACTIVE_CONSENTS).apply {
                setString(1, employeeId)
            }.executeQuery().toList {
                Consent(
                    getLong("id"),
                    getString("title"),
                    getString("responsible_group"),
                    getString("purpose"),
                    getInt("total_involved"),
                    LocalDate(
                        getDate("expiration").toLocalDate().year,
                        getDate("expiration").toLocalDate().month,
                        getDate("expiration").toLocalDate().dayOfMonth
                    ),
                    getString("code"),
                    null,
                    null
                )
            }
        }
    }

    fun getConsentByCode(code: String): Consent {
        dataSource.connection.use {
            val result = it.prepareStatement(SELECT_CONSENT_BY_CODE).apply {
                setString(1, code)
            }.executeQuery()
            return if (result.next()) {
                Consent(
                    result.getLong("id"),
                    result.getString("title"),
                    result.getString("responsible_group"),
                    result.getString("purpose"),
                    result.getInt("total_involved"),
                    LocalDate(
                        result.getDate("expiration").toLocalDate().year,
                        result.getDate("expiration").toLocalDate().month,
                        result.getDate("expiration").toLocalDate().dayOfMonth
                    ),
                    result.getString("code"),
                    null,
                    null
                )
            } else {
                throw Exception("Could not find consent with code: $code")
            }
        }
    }

    fun getConsentById(consentId: Long): Consent {
        dataSource.connection.use {
            val result = it.prepareStatement(SELECT_CONSENT_BY_ID).apply {
                setLong(1, consentId)
            }.executeQuery()
            return if (result.next()) {
                Consent(
                    result.getLong("id"),
                    result.getString("title"),
                    result.getString("responsible_group"),
                    result.getString("purpose"),
                    result.getInt("total_involved"),
                    LocalDate(
                        result.getDate("expiration").toLocalDate().year,
                        result.getDate("expiration").toLocalDate().month,
                        result.getDate("expiration").toLocalDate().dayOfMonth
                    ),
                    result.getString("code"),
                    null,
                    null
                )
            } else {
                throw Exception("Could not find consent with id: $consentId")
            }
        }
    }

    private object ConsentQueries {
        val POST_CONSENT = """
            INSERT INTO consent
            (title, responsible_group, purpose, total_involved, expiration, code, employee_id)
            VALUES
            (?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        val SELECT_ALL_ACTIVE_CONSENTS = """
            SELECT * FROM consent
            WHERE employee_id = ?
        """.trimIndent()

        val SELECT_CONSENT_BY_CODE = """
            SELECT * FROM consent
            WHERE code = ?
        """.trimIndent()

        val SELECT_CONSENT_BY_ID = """
            SELECT * FROM consent
            WHERE id = ?
        """.trimIndent()
    }
}