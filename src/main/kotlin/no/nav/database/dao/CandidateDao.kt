package no.nav.database.dao

import kotlinx.datetime.LocalDate
import no.nav.database.dao.CandidateDao.CandidateQueries.SELECT_ALL_CANDIDATES_BY_CONSENT
import no.nav.database.toList
import no.nav.models.Candidate
import no.nav.models.CandidateStatus
import javax.sql.DataSource

class CandidateDao(
    private val dataSource: DataSource
) {
    fun getCandidatesByConsentId(consentId: Long): List<Candidate> {
        dataSource.connection.use {
            return it.prepareStatement(SELECT_ALL_CANDIDATES_BY_CONSENT).apply {
                setLong(1, consentId)
            }.executeQuery().toList {
                Candidate(
                    getLong("id"),
                    getString("name"),
                    getString("email"),
                    CandidateStatus.valueOf(getString("status")),
                    LocalDate(
                        getDate("consented").toLocalDate().year,
                        getDate("consented").toLocalDate().month,
                        getDate("consented").toLocalDate().dayOfMonth
                    ),
                    getBoolean("audio_recording"),
                    getBoolean("store_info")
                )
            }
        }
    }

    private object CandidateQueries {
        val SELECT_ALL_CANDIDATES_BY_CONSENT = """
            SELECT * FROM candidate
            WHERE consent_id = ?
        """.trimIndent()
    }
}