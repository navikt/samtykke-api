package no.nav.services

import io.ktor.server.plugins.*
import no.nav.database.dao.MessageDao
import no.nav.models.BaseMessage
import no.nav.models.Message
import no.nav.models.MessageType
import no.nav.models.SlackMessage
import no.nav.slack.sendSlackMessage

class MessageService(
    private val messageDao: MessageDao
) {
    fun getMessagesByEmployeeId(employeeId: String): List<Message> {
        val messages: List<Message> = messageDao.getMessagesByEmployeeId(employeeId)

        if (messages.isEmpty()) throw NotFoundException()

        return messages
    }

    suspend fun createMessage(
        messageType: MessageType,
        consentTitle: String,
        consentCode: String,
        citizenTrackingNumber: String,
        citizenTrackingNumbers: List<String>,
        employeeId: String
    ) {
        try {
            when (messageType) {
                MessageType.CITIZEN_ACCEPT_CONSENT -> {
                    messageDao.createMessage(
                        BaseMessage(
                            "En innbygger har gitt sitt samtykke til: $consentTitle.",
                            "En innbygger har gitt samtykke til: $consentTitle. " +
                                    "Gå til samtykket for å se mer.",
                            "/$consentCode"
                        ), employeeId
                    )
                    sendSlackMessage(SlackMessage(
                        MessageType.CITIZEN_ACCEPT_CONSENT,
                        consentTitle,
                        listOf(),
                        "/$consentCode"
                    ), "")
                }
                MessageType.CITIZEN_WITHDRAW_CONSENT -> {
                    messageDao.createMessage(
                        BaseMessage(
                            "En innbygger har trukket sitt samtykke til: $consentTitle.",
                            "En innbygger med løpenummer: ${citizenTrackingNumber.split("-")[0].uppercase()} har " +
                                    "trukket sitt samtykke til: $consentTitle. Nå må du slette all " +
                                    "ekstern data knyttet til innbyggeren.",
                            "/$consentCode"
                        ), employeeId
                    )
                }
                MessageType.CITIZEN_UPDATE_CONSENT -> {
                    messageDao.createMessage(
                        BaseMessage(
                            "En innbygger har oppdatert sitt samtykke til: $consentTitle.",
                            "En innbygger med løpenummer: ${citizenTrackingNumber.split("-")[0].uppercase()} har " +
                                    "oppdatert sitt samtykke til: $consentTitle. Nå må du oppdatere " +
                                    "all ekstern data knyttet til innbyggeren.",
                            "/$consentCode"
                        ), employeeId
                    )
                }
                MessageType.CONSENT_EXPIRE -> {
                    messageDao.createMessage(
                        BaseMessage(
                            "Samtykket ditt: $consentTitle, har utløpt!",
                            "Samtykket ditt: $consentTitle, har utløpt, og disse løpenumrene " +
                                    "er knyttet til samtykket: ${formatTrackingNumbers(citizenTrackingNumbers!!)}. " +
                                    "Data relatert til samtykket i løsninger har blitt sletter, men dere må " +
                                    "selv slette ekstern data knyttet til løpenumrene.",
                            ""
                        ), employeeId
                    )
                }
            }
        } catch (e: Exception) {
            throw Error("Could not create message of type $messageType")
        }
    }

    private fun formatTrackingNumbers(trackingNumbers: List<String>): String {
        var string = ""

        trackingNumbers.forEachIndexed { index, element ->
            string = if (trackingNumbers.lastIndex == index) {
                "$string${element.split("-")[0].uppercase()}"
            } else {
                "$string${element.split("-")[0].uppercase()}, "
            }
        }

        return string
    }

}