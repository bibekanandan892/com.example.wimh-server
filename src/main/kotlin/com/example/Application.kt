package com.example

import com.example.domain.UserRepository
import io.ktor.server.application.*
import com.example.plugins.*
import org.koin.ktor.ext.inject

fun main(args: Array<String>): Unit =
      io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    val userRepository by inject<UserRepository>()
    configureKoin()
    configureAuth()
    configureSockets()
    configureMonitoring()
    configureSerialization()
    configureRouting(userRepository= userRepository)
}
