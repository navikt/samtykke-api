package no.nav.slack

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.*
import no.nav.models.SlackMessage

suspend fun sendSlackMessage(message: SlackMessage, channelId: String) {
    val slackBotPath = "${System.getenv("SLACKBOT_URL")}/message/${channelId}"

    val httpClient = HttpClient(CIO) {
        install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
            json()
        }
    }

    try {
    httpClient.post {
            url(slackBotPath)
            contentType(ContentType.Application.Json)
            setBody(message)
        }
    } catch (e: Exception) {
        throw BadRequestException("Slack message could not be sent")
    }
}