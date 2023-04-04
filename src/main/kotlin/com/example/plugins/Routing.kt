package com.example.plugins

import com.example.domain.UserRepository
import com.example.routes.*
import com.example.routes.unauthorizedRoute
import io.ktor.server.routing.*
import io.ktor.server.application.*

fun Application.configureRouting(userRepository: UserRepository) {
    routing {
        rootRoute()
        tokenVerificationRoute(application,userRepository = userRepository)
        authorizedRoute()
        disconnectHeartRoute(app = application, userRepository = userRepository)
        acceptConnectionRequest(app = application, userRepository = userRepository)
        heartStatusRoute(app = application, userRepository = userRepository)
        sendConnectRequestRoute(app = application , userRepository = userRepository)
        unauthorizedRoute()
    }
}
