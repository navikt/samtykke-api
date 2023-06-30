package no.nav.message

import io.ktor.server.plugins.*
import io.ktor.util.logging.*
import no.nav.message.slack.sendSlackMessage

class MessageService(
    private val messageDao: MessageDao
) {

    private val logger = KtorSimpleLogger("com.example.RequestTracePlugin")

    fun getMessagesByEmployeeId(employeeId: String): List<Message> {
        val messages: List<Message> = messageDao.getMessagesByEmployeeId(employeeId)

        if (messages.isEmpty()) throw NotFoundException()

        return messages
    }

    fun createMessage(
        messageType: MessageType,
        consentTitle: String,
        consentCode: String,
        citizenTrackingNumber: String,
        citizenTrackingNumbers: List<String>,
        employeeId: String,
        slackChannelId: String
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
                    ), slackChannelId)
                }
                MessageType.CITIZEN_WITHDRAW_CONSENT -> {
                    messageDao.createMessage(
                        BaseMessage(
                            "En innbygger har trukket sitt samtykke til: $consentTitle.",
                            "En innbygger med løpenummer: ${citizenTrackingNumber.uppercase()} har " +
                                    "trukket sitt samtykke til: $consentTitle. Nå må du slette all " +
                                    "ekstern data knyttet til innbyggeren.",
                            "/$consentCode"
                        ), employeeId
                    )
                    sendSlackMessage(SlackMessage(
                        MessageType.CITIZEN_WITHDRAW_CONSENT,
                        consentTitle,
                        listOf(citizenTrackingNumber),
                        "/$consentCode"
                    ), slackChannelId)
                }
                MessageType.CITIZEN_UPDATE_CONSENT -> {
                    messageDao.createMessage(
                        BaseMessage(
                            "En innbygger har oppdatert sitt samtykke til: $consentTitle.",
                            "En innbygger med løpenummer: ${citizenTrackingNumber.uppercase()} har " +
                                    "oppdatert sitt samtykke til: $consentTitle. Nå må du oppdatere " +
                                    "all ekstern data knyttet til innbyggeren.",
                            "/$consentCode"
                        ), employeeId
                    )
                    sendSlackMessage(SlackMessage(
                        MessageType.CITIZEN_UPDATE_CONSENT,
                        consentTitle,
                        listOf(citizenTrackingNumber),
                        "/$consentCode"
                    ), slackChannelId)
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
                    sendSlackMessage(SlackMessage(
                        MessageType.CONSENT_EXPIRE,
                        consentTitle,
                        citizenTrackingNumbers,
                        ""
                    ), slackChannelId)
                }
            }
            logger.info("Message of type: $messageType for consent with code: $consentCode created")
        } catch (e: Exception) {
            logger.info("Creation of message failed: ${e.message}")
            throw Error("Could not create message of type $messageType")
        }
    }

    fun markMessageAsRead(messageId: Long) = messageDao.markMessageAsRead(messageId)

    private fun formatTrackingNumbers(trackingNumbers: List<String>): String {
        var string = ""

        trackingNumbers.forEachIndexed { index, element ->
            string = if (trackingNumbers.lastIndex == index) {
                "$string${element.uppercase()}"
            } else {
                "$string${element.uppercase()}, "
            }
        }

        return string
    }

}