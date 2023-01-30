package no.nav.routes

import io.ktor.server.routing.*

fun Route.citizenRoute() {
    route("consent") {
        route("active") {
            get {
                // TODO: add
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