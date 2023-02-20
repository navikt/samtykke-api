package no.nav.routes

import io.ktor.server.routing.*

fun Route.citizenRoute() {
    route("consent") {
        route("active") {
            get {
                // TODO: get all active canditatures and consents connected to user
            }
        }

        route("{code}") {
            get {
                // TODO: add
            }
        }

        route("canditature") {
            get {
                // TODO: add
            }

            post {
                // TODO: add
            }

            put {
                // TODO: add
            }
        }
    }
}