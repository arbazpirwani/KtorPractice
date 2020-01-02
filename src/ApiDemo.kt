package com.arbaz

import com.google.gson.Gson
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Timestamp


object NewsTable : Table("news_table") {
    val id = integer("news_id").primaryKey()
    val title = varchar("title", length = 255)
    val description = text("description")
    val image_path = text("image_path")
}

data class News(val id: Int, val title: String, val description: String, val image_path: String)


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
//            val model = News(1, "Arbaz", "Android", "https://dummyimage.com/600x400/000/fff")
            call.respond(getNews())
        }
    }
}


fun initDB() {
    val url = "jdbc:mysql://root:12345678@localhost:3306/news?useUnicode=true&serverTimezone=UTC&useSSL=false"
    val driver = "com.mysql.cj.jdbc.Driver"
    Database.connect(url, driver)
}


fun getNews(): ArrayList<News> {
//    var json: String = ""
    val newsArray = ArrayList<News>()

    transaction {
        val newsRows = NewsTable.selectAll().orderBy(NewsTable.id, true).limit(5)
        for (f in newsRows) {
            newsArray.add(News(id = f[NewsTable.id], title = f[NewsTable.title], description = f[NewsTable.description], image_path = f[NewsTable.image_path]))
        }
//        json = Gson().toJson(newsArray)
    }
    return newsArray
}