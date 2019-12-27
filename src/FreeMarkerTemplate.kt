package com.arbaz

import freemarker.cache.ClassTemplateLoader
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.freemarker.FreeMarker
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing


fun Application.freeMarker() {

    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(
            this::class.java.classLoader, "templates"
        )
    }

    routing {
        get("/freeMarker") {
            val developerList = listOf(
                Developer(1, "Arbaz", "Android"),
                Developer(2, "Eric", "Ktor")
            )
            call.respond(
                FreeMarkerContent(
                    "index.ftl",
                    mapOf("developers" to developerList)
                )
            )
        }
    }
}