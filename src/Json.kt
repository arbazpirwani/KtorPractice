package com.arbaz

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.response.*
import io.ktor.routing.*

data class Developer(val id: Int, val name: String, val expertise: String)


fun Application.json() {
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            serializeNulls()
        }
    }
    routing {
        get("/developer") {
            val model = Developer(1, "Arbaz", "Android")
            call.respond(model)
        }
    }
}