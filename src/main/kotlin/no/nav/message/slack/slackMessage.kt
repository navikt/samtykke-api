package no.nav.message.slack

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.*
import io.ktor.util.logging.*
import kotlinx.coroutines.runBlocking
import no.nav.consent.pdf.getAzureOBOToken
import no.nav.isNais
import no.nav.message.SlackMessage
fun sendSlackMessage(message: SlackMessage, channelId: String) = runBlocking {

    val logger = KtorSimpleLogger("com.example.RequestTracePlugin")

    val slackBotPath = "${System.getenv("SLACKBOT_URL")}/message/${channelId}"

    val httpClient = HttpClient(CIO) {
        install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
            json()
        }
        install(io.ktor.client.plugins.auth.Auth) {
            if (isNais()) {
                bearer {
                    loadTokens {
                        BearerTokens(getAzureOBOToken(), "")
                    }
                }
            }
        }
    }

    try {
        httpClient.post {
            url(slackBotPath)
            contentType(ContentType.Application.Json)
            setBody(message)
        }
        logger.info("Slack message sent of type: ${message.messageType} for consent: ${message.consentTitle}")
    } catch (e: Exception) {
        logger.info("Slack message could not be sent: ${e.message}")
        throw BadRequestException("Slack message could not be sent")
    }
}

