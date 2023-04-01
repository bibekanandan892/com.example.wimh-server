package com.example.plugins

import com.example.domain.UserRepository
import com.example.routes.authorizedRoute
import com.example.routes.rootRoute
import com.example.routes.tokenVerificationRoute
import com.example.routes.unauthorizedRoute
import io.ktor.server.routing.*
import io.ktor.server.application.*

fun Application.configureRouting(userRepository: UserRepository) {
    routing {
        rootRoute()
        tokenVerificationRoute(application,userRepository = userRepository)
        authorizedRoute()
        unauthorizedRoute()
    }
}
