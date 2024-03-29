package no.nav.consent

import io.ktor.server.plugins.*
import kotlinx.datetime.LocalDate
import no.nav.consent.ConsentDao.ConsentQueries.DELETE_EXPIRED_CONSENTS
import no.nav.consent.ConsentDao.ConsentQueries.POST_CONSENT
import no.nav.consent.ConsentDao.ConsentQueries.SELECT_ALL_ACTIVE_CONSENTS
import no.nav.consent.ConsentDao.ConsentQueries.SELECT_ALL_EXPIRED_CONSENTS
import no.nav.consent.ConsentDao.ConsentQueries.SELECT_CONSENT_BY_CODE
import no.nav.consent.ConsentDao.ConsentQueries.SELECT_CONSENT_BY_ID
import no.nav.database.singleOrNull
import no.nav.database.toList
import java.sql.Date
import javax.sql.DataSource

class ConsentDao(
    private val dataSource: DataSource
) {
    fun createConsent(consent: CreateConsentRequest, employeeId: String, code: String): String {
        try {
            dataSource.connection.use {
                return it.prepareStatement(POST_CONSENT).apply {
                    setString(1, consent.title)
                    setString(2, consent.responsibleGroup)
                    setString(3, consent.theme)
                    setString(4, consent.purpose)
                    setInt(5, consent.totalInvolved)
                    setDate(6, Date.valueOf(consent.expiration.toString()))
                    setString(7, consent.endResult)
                    setString(8, consent.slackChannelId)
                    setString(9, code)
                    setString(10, employeeId)
                }.executeQuery().singleOrNull { getString(1) }!!
            }
        } catch (e: Exception) {
            throw BadRequestException("Could not create consent")
        }
    }

    fun getActiveConsents(employeeId: String): List<FullConsent> {
        try {
            dataSource.connection.use {
                return it.prepareStatement(SELECT_ALL_ACTIVE_CONSENTS).apply {
                    setString(1, employeeId)
                }.executeQuery().toList {
                    FullConsent(
                        getLong("id"),
                        getString("title"),
                        getString("responsible_group"),
                        getString("theme"),
                        getString("purpose"),
                        getInt("total_involved"),
                        LocalDate(
                            getDate("expiration").toLocalDate().year,
                            getDate("expiration").toLocalDate().month,
                            getDate("expiration").toLocalDate().dayOfMonth
                        ),
                        getString("end_result"),
                        getString("slack_channel_id"),
                        getString("code"),
                        null,
                        null
                    )
                }
            }
        } catch (e: Exception) {
            throw NotFoundException()
        }
    }

    fun getConsentByCode(code: String): FullConsent {
        dataSource.connection.use {
            val result = it.prepareStatement(SELECT_CONSENT_BY_CODE).apply {
                setString(1, code)
            }.executeQuery()
            return if (result.next()) {
                FullConsent(
                    result.getLong("id"),
                    result.getString("title"),
                    result.getString("responsible_group"),
                    result.getString("theme"),
                    result.getString("purpose"),
                    result.getInt("total_involved"),
                    LocalDate(
                        result.getDate("expiration").toLocalDate().year,
                        result.getDate("expiration").toLocalDate().month,
                        result.getDate("expiration").toLocalDate().dayOfMonth
                    ),
                    result.getString("end_result"),
                    result.getString("slack_channel_id"),
                    result.getString("code"),
                    null,
                    null
                )
            } else {
                throw NotFoundException()
            }
        }
    }

    fun getConsentById(consentId: Long): FullConsent {
        dataSource.connection.use {
            val result = it.prepareStatement(SELECT_CONSENT_BY_ID).apply {
                setLong(1, consentId)
            }.executeQuery()
            return if (result.next()) {
                FullConsent(
                    result.getLong("id"),
                    result.getString("title"),
                    result.getString("responsible_group"),
                    result.getString("theme"),
                    result.getString("purpose"),
                    result.getInt("total_involved"),
                    LocalDate(
                        result.getDate("expiration").toLocalDate().year,
                        result.getDate("expiration").toLocalDate().month,
                        result.getDate("expiration").toLocalDate().dayOfMonth
                    ),
                    result.getString("end_result"),
                    result.getString("slack_channel_id"),
                    result.getString("code"),
                    null,
                    null
                )
            } else {
                throw NotFoundException()
            }
        }
    }

    fun getOwnerIdByConsentId(consentId: Long): String {
        dataSource.connection.use {
            val result = it.prepareStatement(SELECT_CONSENT_BY_ID).apply {
                setLong(1, consentId)
            }.executeQuery()
            return if (result.next()) {
                result.getString("employee_id")
            } else {
                throw NotFoundException()
            }
        }
    }

    fun getExpiredConsent(): List<FullConsent> {
        try {
            dataSource.connection.use {
                return it.prepareStatement(SELECT_ALL_EXPIRED_CONSENTS).executeQuery().toList {
                    FullConsent(
                        getLong("id"),
                        getString("title"),
                        getString("responsible_group"),
                        getString("theme"),
                        getString("purpose"),
                        getInt("total_involved"),
                        LocalDate(
                            getDate("expiration").toLocalDate().year,
                            getDate("expiration").toLocalDate().month,
                            getDate("expiration").toLocalDate().dayOfMonth
                        ),
                        getString("end_result"),
                        getString("slack_channel_id"),
                        getString("code"),
                        null,
                        null
                    )
                }
            }
        } catch (e: Exception) {
            throw NotFoundException()
        }
    }

    fun deleteExpiredConsents() {
        try {
            dataSource.connection.use {
                it.prepareStatement(DELETE_EXPIRED_CONSENTS).executeQuery()
            }
        } catch (e: Exception) {
            throw NotFoundException()
        }
    }

    private object ConsentQueries {
        val POST_CONSENT = """
            INSERT INTO consent
            (title, responsible_group, theme, purpose, total_involved, expiration, end_result, slack_channel_id, code, employee_id)
            VALUES
            (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING code
        """.trimIndent()

        val SELECT_ALL_ACTIVE_CONSENTS = """
            SELECT * FROM consent
            WHERE employee_id = ?
        """.trimIndent()

        val SELECT_ALL_EXPIRED_CONSENTS = """
            SELECT * FROM consent
            WHERE expiration < CURRENT_DATE
        """.trimIndent()

        val SELECT_CONSENT_BY_CODE = """
            SELECT * FROM consent
            WHERE code = ?
        """.trimIndent()

        val SELECT_CONSENT_BY_ID = """
            SELECT * FROM consent
            WHERE id = ?
        """.trimIndent()

        val DELETE_EXPIRED_CONSENTS = """
            DELETE FROM consent 
            WHERE expiration < CURRENT_DATE RETURNING *
        """.trimIndent()
    }
}