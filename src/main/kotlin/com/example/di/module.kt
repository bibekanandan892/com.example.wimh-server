package com.example.di

import com.example.data.remote.ChatService
import com.example.data.remote.UserRepositoryImpl
import com.example.domain.UserRepository
import com.example.util.Constants.DATABASE
import com.example.util.Constants.FCM_AUTH_KEY
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
val koinModule = module {
    single {
        KMongo.createClient()//System.getenv("MONGODB_URI"))//using heroki i have to connect to atlas using a connection string or from heroku platform
            .coroutine
            .getDatabase(DATABASE)
    }
    single<UserRepository> {//type of user data source
        UserRepositoryImpl(get(),get())// get would fetch koin instance which is already provided amd already declared aboce
    }
    single {
        ChatService(get())
    }

    single<HttpClient> {
        HttpClient(CIO) {
            install(HttpTimeout){
                socketTimeoutMillis = 60000
                requestTimeoutMillis = 60000
                connectTimeoutMillis = 60000
            }
            install(ContentNegotiation) {
                json(contentType = ContentType.Application.Json)
            }

            defaultRequest {
                contentType(ContentType.Application.Json)
                header("Authorization", "key=$FCM_AUTH_KEY")
            }
            install(Logging){
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
        }
    }
}