package com.example

import com.example.data.remote.ChatService
import com.example.domain.UserRepository
import io.ktor.server.application.*
import com.example.plugins.*
import org.koin.ktor.ext.inject

fun main(args: Array<String>): Unit =
      io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    val userRepository by inject<UserRepository>()
    val chatService by inject<ChatService>()
    configureKoin()
    configureAuth()
    configureSockets()
    configureMonitoring()
    configureSerialization()
    configureRouting(userRepository= userRepository,chatService = chatService)
}
