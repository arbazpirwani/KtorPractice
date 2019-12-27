package com.arbaz

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.authentication() {
    install(Authentication) {

        basic("auth") {
            validate { credentials ->
                if (credentials.name == credentials.password) {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
    }
    routing {
        get("/") {
            call.respondText("You need credentials to access /secure")
        }
        authenticate("auth") {
            get("/secure") {
                val user = call.authentication.principal
                call.respondText("Accessed secure area")
            }
        }
    }
}