package com.arbaz

import com.google.gson.Gson
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.Parameters
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.request.isMultipart
import io.ktor.request.receiveMultipart
import io.ktor.request.receiveParameters
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.lang.reflect.Parameter
import java.sql.Timestamp


object NewsTable : Table("news_table") {
    val id = integer("news_id").autoIncrement().primaryKey()
    val title = varchar("title", length = 255)
    val description = text("description")
    val image_path = text("image_path")
}

data class News(var id: Int, var title: String, var description: String, var image_path: String) {
    constructor() : this(0, "", "", "")
}


fun Application.apiDemo() {
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            serializeNulls()
        }
    }
    initDB()

    routing {
        get("/news") {
            call.respond(getNews())
        }
        post("/news") {
            val multipart = call.receiveMultipart()
            val news = News()

            if (!call.request.isMultipart()) {
                call.respondText { "Not a multipart request" }
            } else {
                multipart.forEachPart { part ->
                    if (part is PartData.FormItem) {
                        when (part.name) {
                            "title" -> {
                                news.title = part.value
                            }
                            "description" -> {
                                news.description = part.value
                            }
                            "image_path" -> {
                                news.image_path = part.value
                            }
                        }
                    }

                    part.dispose()
                }
                call.respond(insertNews(news))
            }

        }
    }
}


fun initDB() {
    val url = "jdbc:mysql://root:12345678@localhost:3306/news?useUnicode=true&serverTimezone=UTC&useSSL=false"
    val driver = "com.mysql.cj.jdbc.Driver"
    Database.connect(url, driver)
}


/**
 *  API to get all the news from NewsTable
 */

fun getNews(): ArrayList<News> {
    val newsArray = ArrayList<News>()

    transaction {
        val newsRows = NewsTable.selectAll().orderBy(NewsTable.id, false)

        for (row in newsRows) {
            newsArray.add(
                News(
                    id = row[NewsTable.id],
                    title = row[NewsTable.title],
                    description = row[NewsTable.description],
                    image_path = row[NewsTable.image_path]
                )
            )
        }
    }
    return newsArray
}


/**
 *  API to insert news row in NewsTable
 */

fun insertNews(news: News): Any {
    var generatedId: Int? = null

    transaction {
        generatedId = NewsTable.insert {
            it[title] = news.title
            it[description] = news.description
            it[image_path] = news.image_path
        }.generatedKey?.toInt()
    }

    return if (generatedId != null) {
        news.id = generatedId!!
        news
    } else {
        "Something went wrong"
    }

}